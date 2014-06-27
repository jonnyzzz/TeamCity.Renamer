package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

class TCDataModel extends DiagramDataModel<TCElement> {

  private TCDiagramProvider myTcDiagramProvider;
  private final List<DiagramNode<TCElement>> myNodes;
  private final List<DiagramEdge<TCElement>> myEdges;

  public TCDataModel(TCDiagramProvider tcDiagramProvider, Project project, List<DiagramNode<TCElement>> nodes, List<DiagramEdge<TCElement>> edges) {
    super(project, tcDiagramProvider);
    myTcDiagramProvider = tcDiagramProvider;
    myNodes = nodes;
    myEdges = edges;
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
    final TCNode el = new TCNode(myTcDiagramProvider, tcdElement);
    myNodes.add(el);

    return el;
  }

  @Override
  public void refreshDataModel() {

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
