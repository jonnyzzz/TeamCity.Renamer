package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramEdgeBase;
import com.intellij.diagram.DiagramRelationshipInfo;
import org.jetbrains.annotations.NotNull;

class TCEdge extends DiagramEdgeBase<TCElement> {
  private final TCNode mySource;

  public TCEdge(@NotNull TCNode source, @NotNull TCNode target, @NotNull DiagramRelationshipInfo relationship) {
    super(source, target, relationship);
    mySource = source;
  }

  @NotNull
  @Override
  public TCElement getIdentifyingElement() {
    return mySource.getIdentifyingElement();
  }
}
