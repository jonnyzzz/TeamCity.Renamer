package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramEdgeBase;
import org.jetbrains.annotations.NotNull;

class TCEdge extends DiagramEdgeBase<TCElement> {
  private final TCNode mySource;

  public TCEdge(@NotNull final TCNode source, @NotNull final TCNode target) {
    super(source, target, TCRelationships.SNAPSHOT);
    mySource = source;
  }

  @NotNull
  @Override
  public TCElement getIdentifyingElement() {
    return mySource.getIdentifyingElement();
  }
}
