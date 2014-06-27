package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramNodeBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class TCNode extends DiagramNodeBase<TCElement> {
  private TCDiagramProvider myTcDiagramProvider;
  private final TCElement myElement;

  public TCNode(TCDiagramProvider tcDiagramProvider, @NotNull final TCElement element) {
    super(tcDiagramProvider);
    myTcDiagramProvider = tcDiagramProvider;
    myElement = element;
  }

  @Nullable
  @Override
  public String getTooltip() {
    return null;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @NotNull
  @Override
  public TCElement getIdentifyingElement() {
    return myElement;
  }
}
