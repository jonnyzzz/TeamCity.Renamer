package com.jonnyzzz.teamcity.renamer.diagram;

import com.google.common.base.Function;
import com.intellij.diagram.*;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.resolve.deps.Dependencies;
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
  private DiagramExtras<TCElement> myExtras = new TCDiagramExtras();

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

  @Nullable
  @Override
  public DiagramEdgeCreationPolicy<TCElement> getEdgeCreationPolicy() {
    return new DiagramEdgeCreationPolicy<TCElement>() {
      @Override
      public boolean acceptSource(@NotNull DiagramNode<TCElement> source) {
        return true;
      }

      @Override
      public boolean acceptTarget(@NotNull DiagramNode<TCElement> target) {
        return true;
      }
    };
  }

  @NotNull
  @Override
  public DiagramExtras<TCElement> getExtras() {
    return myExtras;
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
    Map<String, TeamCitySettingsBasedFile> bts = new HashMap<>();
    for (TeamCitySettingsBasedFile bt : SNAPS.apply(tcdElement.getFile())) {
      if (!bts.containsKey(bt.getFileId()))
        bts.put(bt.getFileId(), bt);
    }
    for (TeamCitySettingsBasedFile bt : ARTS.apply(tcdElement.getFile())) {
      if (!bts.containsKey(bt.getFileId()))
        bts.put(bt.getFileId(), bt);
    }

    List<TCNode> nodes = new ArrayList<>();
    for (TeamCitySettingsBasedFile bt : bts.values()) {
      nodes.add(new TCNode(this, new TCElement(bt)));
    }

    TCEdgesProducer producer = new TCEdgesProducer() {
      @Override
      public Iterable<TCEdge> getEdges(@NotNull TCDataModel model, @NotNull TCNode node) {
        List<TCEdge> result = new ArrayList<>();
        TeamCitySettingsBasedFile buildType = node.getIdentifyingElement().getFile();
        Map<String, BuildTypeFile> snap = idMap(Dependencies.getAllSnapshotDependencies(buildType));
        Map<String, BuildTypeFile> art = idMap(Dependencies.getAllArtifactDependencies(buildType));
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
  private Iterable<TeamCitySettingsBasedFile> getTransitiveDeps(@Nullable TCElement tcdElement, @NotNull Function<TeamCitySettingsBasedFile, Iterable<BuildTypeFile>> depsFn) {
    if (tcdElement == null)
      return Collections.emptyList();

    Map<String, TeamCitySettingsBasedFile> visited = new HashMap<>();
    Map<String, TeamCitySettingsBasedFile> front = new HashMap<>();
    TeamCitySettingsBasedFile buildType = tcdElement.getFile();
    front.put(buildType.getFileId(), buildType);
    do {
      Map<String, TeamCitySettingsBasedFile> newFront = new HashMap<>();
      for (Map.Entry<String, TeamCitySettingsBasedFile> e : front.entrySet()) {
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

  @NotNull
  private Map<String, BuildTypeFile> idMap(@NotNull Iterable<BuildTypeFile> buildTypes) {
    Map<String, BuildTypeFile> result = new TreeMap<>();
    for (BuildTypeFile f : buildTypes) {
      result.put(f.getFileId(), f);
    }
    return result;
  }

  private static final Function<TeamCitySettingsBasedFile, Iterable<BuildTypeFile>> SNAPS = new Function<TeamCitySettingsBasedFile, Iterable<BuildTypeFile>>() {
    @Override
    public Iterable<BuildTypeFile> apply(TeamCitySettingsBasedFile buildType) {
      return Dependencies.getAllSnapshotDependencies(buildType);
    }
  };

  private static final Function<TeamCitySettingsBasedFile, Iterable<BuildTypeFile>> ARTS = new Function<TeamCitySettingsBasedFile, Iterable<BuildTypeFile>>() {
    @Override
    public Iterable<BuildTypeFile> apply(TeamCitySettingsBasedFile buildType) {
      return Dependencies.getAllArtifactDependencies(buildType);
    }
  };
}
