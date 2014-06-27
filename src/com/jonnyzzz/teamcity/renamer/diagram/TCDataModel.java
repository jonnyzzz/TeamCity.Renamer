package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.xml.XmlTag;
import com.jonnyzzz.teamcity.renamer.model.ArtifactDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.SnapshotDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
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
    final String sourceBuildTypeId = edge.getSource().getIdentifyingElement().getId();
    final TeamCitySettingsBasedFile file = edge.getTarget().getIdentifyingElement().getFile();

    WriteCommandAction.runWriteCommandAction(getProject(), new Runnable() {
      @Override
      public void run() {
        FileDocumentManager.getInstance().saveAllDocuments();
        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();

        CommandProcessor.getInstance().executeCommand(getProject(), new Runnable() {
          private void deleteDependency(@NotNull final TeamCityElement el) {
            final XmlTag tag = el.getXmlTag();
            final XmlTag parent = tag.getParentTag();

            tag.delete();

            if (parent != null &&parent.getSubTags().length == 0) {
              parent.delete();
            }
          }

          @Override
          public void run() {
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

        FileDocumentManager.getInstance().saveAllDocuments();
        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
        getBuilder().updateDataModel();
      }
    });

  }

  @NotNull
  @Override
  public ModificationTracker getModificationTracker() {
    return ModificationTracker.NEVER_CHANGED;
  }

  @Override
  public void dispose() {

  }
}
