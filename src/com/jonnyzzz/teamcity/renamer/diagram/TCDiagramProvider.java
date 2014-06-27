package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramProvider extends BaseDiagramProvider<TCElement> {

  private DiagramElementManager<TCElement> myElementManager = new TCElementManager();
  private DiagramVfsResolver<TCElement> myVcsResolver = new TCVfsResolver();
  private TCColorManager myColorManager = new TCColorManager();

  @Override
  @Pattern("[a-zA-Z0-9_-]*")
  public String getID() {
    return "TeamCity";
  }

  @Override
  public DiagramElementManager<TCElement> getElementManager() {
    return myElementManager;
  }

  @Override
  public DiagramVfsResolver<TCElement> getVfsResolver() {
    return myVcsResolver;
  }

  @Override
  public DiagramColorManager getColorManager() {
    return myColorManager;
  }

  @Override
  public String getPresentableName() {
    return "TeamCity Build Dependencies";
  }

  @Override
  public DiagramDataModel<TCElement> createDataModel(@NotNull Project project,
                                                      @Nullable TCElement tcdElement,
                                                      @Nullable VirtualFile virtualFile,
                                                      DiagramPresentationModel diagramPresentationModel) {
    Map<String, BuildTypeFile> snap = idMap(getSnapshotDependencies(tcdElement));
    Map<String, BuildTypeFile> art = idMap(getArtifactDependencies(tcdElement));
    final List<DiagramNode<TCElement>> nodes = new ArrayList<>();
    final List<DiagramEdge<TCElement>> edges = new ArrayList<>();
    if (tcdElement != null) {
      TCNode target = new TCNode(this, tcdElement);
      nodes.add(target);
      for (Map.Entry<String, BuildTypeFile> e : snap.entrySet()) {
        BuildTypeFile f = e.getValue();
        nodes.add(new TCNode(this, new TCElement(f)));
        nodes.add(new TCNode(this, new TCElement(f)));
        if (art.containsKey(f.getFileId())) {
          //snap+art
          edges.add(new TCEdge(new TCNode(this, new TCElement(f)), target, TCRelationships.SNAPSHOT_ART));
          art.remove(f.getFileId());
        } else {
          //snap
          edges.add(new TCEdge(new TCNode(this, new TCElement(f)), target, TCRelationships.SNAPSHOT));
        }
      }

      //only art
      for (Map.Entry<String, BuildTypeFile> e : art.entrySet()) {
        BuildTypeFile f = e.getValue();
        nodes.add(new TCNode(this, new TCElement(f)));
        edges.add(new TCEdge(new TCNode(this, new TCElement(f)), target, TCRelationships.ARTIFACT));
      }
    }

    return new TCDataModel(this, project, nodes, edges);
  }

  @NotNull
  private List<BuildTypeFile> getSnapshotDependencies(@Nullable TCElement tcdElement) {
    if (tcdElement == null)
      return Collections.emptyList();
    return tcdElement.getBuildType().getSnapshotDependencies();
  }


  @NotNull
  private List<BuildTypeFile> getArtifactDependencies(@Nullable TCElement tcdElement) {
    if (tcdElement == null)
      return Collections.emptyList();
    return tcdElement.getBuildType().getArtifactDependencies();
  }

  private Map<String, BuildTypeFile> idMap(@NotNull List<BuildTypeFile> buildTypes) {
    Map<String, BuildTypeFile> result = new HashMap<>();
    for (BuildTypeFile f : buildTypes) {
      result.put(f.getFileId(), f);
    }
    return result;
  }
}
