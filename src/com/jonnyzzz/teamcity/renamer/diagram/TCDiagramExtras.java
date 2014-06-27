package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class TCDiagramExtras extends DiagramExtras<TCElement> {

  private final DiagramDnDProvider<TCElement> myDnDProvider = new TCDnDProvider();

  @Nullable
  @Override
  public DiagramDnDProvider<TCElement> getDnDProvider() {
    return myDnDProvider;
  }

  @NotNull
  @Override
  public JComponent createNodeComponent(DiagramNode<TCElement> node, DiagramBuilder builder, Point basePoint) {
    return super.createNodeComponent(node, builder, basePoint);
  }
}
