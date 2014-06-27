package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramProvider extends BaseDiagramProvider<TCElement> {

  private DiagramElementManager<TCElement> myElementManager = new TCElementManager();
  private DiagramVfsResolver<TCElement> myVcsResolver = new TCVfsResolver();

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
  public String getPresentableName() {
    return "TeamCity Build Dependencies";
  }

  @Override
  public DiagramDataModel<TCElement> createDataModel(@NotNull Project project,
                                                      @Nullable TCElement tcdElement,
                                                      @Nullable VirtualFile virtualFile,
                                                      DiagramPresentationModel diagramPresentationModel) {
    List<BuildTypeFile> deps = tcdElement == null ? new ArrayList<BuildTypeFile>() : tcdElement.getBuildType().getSnapshotDependencies();
    final List<DiagramNode<TCElement>> nodes = new ArrayList<>();
    final List<DiagramEdge<TCElement>> edges = new ArrayList<>();
    if (tcdElement != null) {
      TCNode source = new TCNode(this, tcdElement);
      nodes.add(source);
      for (BuildTypeFile f : deps) {
        nodes.add(new TCNode(this, new TCElement(f)));
        edges.add(new TCEdge(source, new TCNode(this, new TCElement(f))));
      }
    }

    return new TCDataModel(this, project, nodes, edges);
  }
}
