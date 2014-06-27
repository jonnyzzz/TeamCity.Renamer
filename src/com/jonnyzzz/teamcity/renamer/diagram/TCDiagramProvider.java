package com.jonnyzzz.teamcity.renamer.diagram;

import com.google.common.base.Function;
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
    Map<String, BuildTypeFile> bts = new HashMap<>();
    for (BuildTypeFile bt : getTransitiveDeps(tcdElement, SNAPS)) {
      if (!bts.containsKey(bt.getFileId()))
        bts.put(bt.getFileId(), bt);
    }
    for (BuildTypeFile bt : getTransitiveDeps(tcdElement, ARTS)) {
      if (!bts.containsKey(bt.getFileId()))
        bts.put(bt.getFileId(), bt);
    }

    List<TCNode> nodes = new ArrayList<>();
    for (BuildTypeFile bt : bts.values()) {
      nodes.add(new TCNode(this, new TCElement(bt)));
    }

    TCEdgesProducer producer = new TCEdgesProducer() {
      @Override
      public Iterable<TCEdge> getEdges(@NotNull TCDataModel model, @NotNull TCNode node) {
        List<TCEdge> result = new ArrayList<>();
        BuildTypeFile buildType = node.getIdentifyingElement().getBuildType();
        Map<String, BuildTypeFile> snap = idMap(buildType.getSnapshotDependencies());
        Map<String, BuildTypeFile> art = idMap(buildType.getArtifactDependencies());
        for (Map.Entry<String, BuildTypeFile> e : snap.entrySet()) {
          TCNode neighbour = model.getNodeById(e.getKey());
          if (neighbour == null)
            continue;
          if (art.get(e.getKey()) != null) {
            //snap_art
            result.add(new TCEdge(neighbour, node, TCRelationships.SNAPSHOT_ART));
            art.remove(e.getKey());
          } else {
            //snap
            result.add(new TCEdge(neighbour, node, TCRelationships.SNAPSHOT));
          }
        }

        //art
        for (Map.Entry<String, BuildTypeFile> e : art.entrySet()) {
          TCNode neighbour = model.getNodeById(e.getKey());
          if (neighbour == null)
            continue;
          result.add(new TCEdge(neighbour, node, TCRelationships.ARTIFACT));
        }
        return result;
      }
    };

    return new TCDataModel(this, project, nodes, producer);
  }

  @NotNull
  private Iterable<BuildTypeFile> getTransitiveDeps(@Nullable TCElement tcdElement, @NotNull Function<BuildTypeFile, Iterable<BuildTypeFile>> depsFn) {
    if (tcdElement == null)
      return Collections.emptyList();

    Map<String, BuildTypeFile> visited = new HashMap<>();
    Map<String, BuildTypeFile> front = new HashMap<>();
    BuildTypeFile buildType = tcdElement.getBuildType();
    front.put(buildType.getFileId(), buildType);
    do {
      Map<String, BuildTypeFile> newFront = new HashMap<>();
      for (Map.Entry<String, BuildTypeFile> e : front.entrySet()) {
        if (visited.containsKey(e.getKey()))
          continue;
        visited.put(e.getKey(), e.getValue());
        Iterable<BuildTypeFile> deps = depsFn.apply(e.getValue());
        if (deps == null)
          continue;
        for (BuildTypeFile dep : deps) {
          newFront.put(dep.getFileId(), dep);
        }
      }
      front = newFront;
    } while (!front.isEmpty());

    return visited.values();
  }

  private Map<String, BuildTypeFile> idMap(@NotNull List<BuildTypeFile> buildTypes) {
    Map<String, BuildTypeFile> result = new HashMap<>();
    for (BuildTypeFile f : buildTypes) {
      result.put(f.getFileId(), f);
    }
    return result;
  }

  private static final Function<BuildTypeFile, Iterable<BuildTypeFile>> SNAPS = new Function<BuildTypeFile, Iterable<BuildTypeFile>>() {
    @Override
    public Iterable<BuildTypeFile> apply(BuildTypeFile buildType) {
      return buildType.getSnapshotDependencies();
    }
  };

  private static final Function<BuildTypeFile, Iterable<BuildTypeFile>> ARTS = new Function<BuildTypeFile, Iterable<BuildTypeFile>>() {
    @Override
    public Iterable<BuildTypeFile> apply(BuildTypeFile buildType) {
      return buildType.getArtifactDependencies();
    }
  };
}
