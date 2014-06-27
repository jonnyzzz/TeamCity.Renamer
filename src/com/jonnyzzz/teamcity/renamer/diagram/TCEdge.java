package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramEdgeBase;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.presentation.DiagramLineType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

class TCEdge extends DiagramEdgeBase<TCElement> {
  private final TCNode mySource;

  public TCEdge(@NotNull final TCNode source, @NotNull final TCNode target) {
    super(source, target, new DiagramRelationshipInfoAdapter("snapshot", DiagramLineType.DASHED) {
      @Override
      public Shape getStartArrow() {
        return null;
      }
    });
    mySource = source;
  }

  @NotNull
  @Override
  public TCElement getIdentifyingElement() {
    return mySource.getIdentifyingElement();
  }
}
