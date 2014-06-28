package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.xml.DomManager;
import com.jonnyzzz.teamcity.renamer.model.*;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.deps.Dependencies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class TCDataModel extends DiagramDataModel<TCElement> {

  private TCDiagramProvider myTcDiagramProvider;
  private final Map<String, TCNode> myId2Node = new HashMap<>();
  private final List<TCNode> myNodes;
  private final List<DiagramEdge<TCElement>> myEdges;
  private TCEdgesProducer myEdgesProducer;

  public TCDataModel(@NotNull TCDiagramProvider tcDiagramProvider,
                     @NotNull Project project,
                     @NotNull List<TCNode> nodes,
                     @NotNull TCEdgesProducer edgesProducer) {
    super(project, tcDiagramProvider);
    myTcDiagramProvider = tcDiagramProvider;
    myNodes = nodes;
    for (TCNode n : myNodes) {
      myId2Node.put(n.getIdentifyingElement().getId(), n);
    }
    myEdgesProducer = edgesProducer;
    myEdges = new ArrayList<>();
    refreshDataModel();
  }

  @NotNull
  @Override
  public Collection<? extends DiagramNode<TCElement>> getNodes() {
    return myNodes;
  }

  @NotNull
  @Override
  public Collection<? extends DiagramEdge<TCElement>> getEdges() {
    return myEdges;
  }

  @NotNull
  @Override
  public String getNodeName(DiagramNode<TCElement> diagramNode) {
    return diagramNode.getIdentifyingElement().getName();
  }

  @Nullable
  @Override
  public DiagramNode<TCElement> addElement(final TCElement tcdElement) {
    TCNode node = myId2Node.get(tcdElement.getId());
    if (node != null)
      return null;

    node = new TCNode(myTcDiagramProvider, tcdElement);
    myNodes.add(node);
    myId2Node.put(tcdElement.getId(), node);
    return node;
  }

  @Override
  public void refreshDataModel() {
    myEdges.clear();
    for (TCNode node : myNodes) {
      Iterable<TCEdge> neighbours = myEdgesProducer.getEdges(this, node);
      if (neighbours == null)
        continue;
      for (DiagramEdge<TCElement> edge : neighbours) {
        myEdges.add(edge);
      }
    }
  }

  @Nullable
  public TCNode getNodeById(@NotNull String id) {
    return myId2Node.get(id);
  }

  @Override
  public void removeNode(DiagramNode<TCElement> node) {
    myNodes.remove(node);
    myId2Node.remove(node.getIdentifyingElement().getId());
    refreshDataModel();
  }

  @Override
  public void removeEdge(final DiagramEdge<TCElement> edge) {
    myEdges.remove(edge);
    refreshDataModel();

    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        WriteCommandAction.runWriteCommandAction(getProject(), new Runnable() {
          @Override
          public void run() {
            FileDocumentManager.getInstance().saveAllDocuments();
            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();

            CommandProcessor.getInstance().executeCommand(getProject(), new Runnable() {
              private void deleteDependency(@NotNull final TeamCityElement el) {
                el.undefine();
              }

              @Override
              public void run() {
                final String sourceBuildTypeId = edge.getSource().getIdentifyingElement().getId();
                final TeamCitySettingsBasedFile file = edge.getTarget().getIdentifyingElement().getFile();

                final BuildTemplateFile baseTemplate = (file instanceof BuildTypeFile) ? ((BuildTypeFile) file).getBaseTemplate() : null;

                if (edge.getRelationship() == TCRelationships.SNAPSHOT || edge.getRelationship() == TCRelationships.SNAPSHOT_ART) {
                  for (final SnapshotDependencyElement dep : file.getSettingsElement().getSnapshotDependencies().getDependencies()) {
                    String id = dep.getSourceBuildTypeId().getStringValue();
                    if (id == null)
                      continue;

                    if (id.equals(sourceBuildTypeId))
                      deleteDependency(dep);
                  }

                  if (baseTemplate != null) {
                    for (final SnapshotDependencyElement dep : baseTemplate.getSettingsElement().getSnapshotDependencies().getDependencies()) {
                      String id = dep.getSourceBuildTypeId().getStringValue();
                      if (id == null)
                        continue;
                      if (id.equals(sourceBuildTypeId))
                        deleteDependency(dep);

                    }
                  }
                }
                if (edge.getRelationship() == TCRelationships.ARTIFACT || edge.getRelationship() == TCRelationships.SNAPSHOT_ART) {
                  for (final ArtifactDependencyElement dep : file.getSettingsElement().getArtifactDependencies().getDependencies()) {
                    String id = dep.getSourceBuildTypeId().getStringValue();
                    if (id == null)
                      continue;
                    if (id.equals(sourceBuildTypeId))
                      deleteDependency(dep);
                  }

                  if (baseTemplate != null) {
                    for (final ArtifactDependencyElement dep : baseTemplate.getSettingsElement().getArtifactDependencies().getDependencies()) {
                      String id = dep.getSourceBuildTypeId().getStringValue();
                      if (id == null)
                        continue;
                      if (id.equals(sourceBuildTypeId))
                        deleteDependency(dep);
                    }
                  }
                }
              }
            }, "Remove dependencies", "TeamCity");

            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
            FileDocumentManager.getInstance().saveAllDocuments();

            final DiagramBuilder builder = getBuilder();
            ApplicationManager.getApplication().invokeLater(
                    new Runnable() {
                      @Override
                      public void run() {
                        builder.updateDataModel();
                        builder.update(true, false);
                      }
                    }
            );

          }
        });
      }
    });
  }

  @NotNull
  @Override
  public ModificationTracker getModificationTracker() {
    return DomManager.getDomManager(getProject());
  }

  @Override
  public void collapseNode(DiagramNode<TCElement> node) {
    Map<String, BuildTypeFile> reachable = idMap(Dependencies.getDependencies(node.getIdentifyingElement().getFile()));
    for (TCNode n : myNodes) {
      if (reachable.containsKey(n.getIdentifyingElement().getId()) || node == n)
        continue;
      for (String id : idMap(Dependencies.getDependencies(n.getIdentifyingElement().getFile())).keySet()) {
        if (reachable.remove(id) != null) {
          int i = 0;
        }

      }
      if (reachable.isEmpty())
        break;
    }

    for (String id : reachable.keySet()) {
      TCNode n = getNodeById(id);
      removeNode(n);
    }
    getBuilder().setSelected(node, true);
    refreshDataModel();
  }

  @Override
  public void expandNode(DiagramNode<TCElement> node) {
    for (BuildTypeFile bt : Dependencies.getDependencies(node.getIdentifyingElement().getFile())) {
      addElement(new TCElement(bt));
    }
    getBuilder().setSelected(node, true);
    refreshDataModel();
  }

  @NotNull
  private Map<String, BuildTypeFile> idMap(@NotNull Iterable<BuildTypeFile> buildTypes) {
    Map<String, BuildTypeFile> result = new TreeMap<>();
    for (BuildTypeFile f : buildTypes) {
      result.put(f.getFileId(), f);
    }
    return result;
  }


  @Override
  public void dispose() {

  }

  @Nullable
  @Override
  public DiagramEdge<TCElement> createEdge(@NotNull final DiagramNode<TCElement> from, @NotNull final DiagramNode<TCElement> to) {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        WriteCommandAction.runWriteCommandAction(getProject(), new Runnable() {
          @Override
          public void run() {
            FileDocumentManager.getInstance().saveAllDocuments();
            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();

            SnapshotDependencyElement sd = to.getIdentifyingElement().getFile().getSettingsElement().getSnapshotDependencies().addSnapshotDependencyElement();
            sd.getSourceBuildTypeId().setStringValue(from.getIdentifyingElement().getId());

            OptionsElement opts = sd.getOptions();
            OptionElement opel1 = opts.addOption();
            opel1.setOptionName("run-build-if-dependency-failed");
            opel1.setOptionValue("true");
            OptionElement opel2 = opts.addOption();
            opel2.setOptionName("take-started-build-with-same-revisions");
            opel2.setOptionValue("true");

            FileDocumentManager.getInstance().saveAllDocuments();
            PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
          }
        });
      }
    });



    return super.createEdge(from, to);
  }
}
