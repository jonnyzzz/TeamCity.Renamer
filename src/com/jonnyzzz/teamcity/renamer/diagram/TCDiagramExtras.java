package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.diagram.util.DiagramUtils;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.xml.XmlElement;
import com.jonnyzzz.teamcity.renamer.model.SnapshotDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.resolve.buildTypes.TeamCityFileNamedReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

  @Nullable
  @Override
  public Object getData(String dataId, List<DiagramNode<TCElement>> diagramNodes, DiagramBuilder builder) {
    if (CommonDataKeys.PSI_ELEMENT.is(dataId)) {
      if ((diagramNodes.size() == 1)) {
        final TeamCitySettingsBasedFile tcFile = diagramNodes.get(0).getIdentifyingElement().getFile();
        return new TeamCityFileNamedReference(tcFile);
      }

      final List<DiagramEdge> edges = DiagramUtils.getSelectedEdges(builder);
      if (edges.size() == 1) {
        DiagramEdge e = edges.get(0);
        if (e instanceof TCEdge) {
          TCEdge edge = (TCEdge) e;
          DiagramNode<TCElement> source = edge.getSource();
          DiagramNode<TCElement> target = edge.getTarget();
          TeamCitySettingsBasedFile tcFile = target.getIdentifyingElement().getFile();
          for (SnapshotDependencyElement el : tcFile.getSettingsElement().getSnapshotDependencies().getDependencies()) {
            if (source.getIdentifyingElement().getId().equals(el.getSourceBuildTypeId().getValue())) {
              XmlElement xmlElement = el.getXmlElement();
              if (xmlElement != null)
                return xmlElement;
            }
          }
        }
      }
    }

    if (CommonDataKeys.PROJECT.is(dataId)) {
      return builder.getProject();
    }

    return super.getData(dataId, diagramNodes, builder);
  }
}
