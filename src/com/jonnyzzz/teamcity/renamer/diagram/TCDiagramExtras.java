package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.actions.DiagramAddElementAction;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.diagram.settings.DiagramLayout;
import com.intellij.diagram.util.DiagramUtils;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.graph.GraphManager;
import com.intellij.openapi.graph.layout.LayoutOrientation;
import com.intellij.openapi.graph.layout.Layouter;
import com.intellij.openapi.graph.layout.hierarchic.HierarchicGroupLayouter;
import com.intellij.openapi.graph.layout.hierarchic.HierarchicLayouter;
import com.intellij.openapi.graph.view.Graph2D;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.IncorrectOperationException;
import com.jonnyzzz.teamcity.renamer.model.ArtifactDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.SnapshotDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.resolve.buildTypes.TeamCityFileNamedReference;
import org.jetbrains.annotations.NonNls;
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

  @Override
  public DiagramElementsProvider<TCElement>[] getElementsProviders() {
    return new DiagramElementsProvider[]{ new TCDiagramDependenciesProvider(), new TCDiagramDependingOnMeProvider() };
  }

  @Override
  public Layouter getCustomLayouter(Graph2D graph, Project project) {
    final Layouter layouter = Utils.getLayouter(graph, project, DiagramLayout.HIERARCHIC_GROUP);
    if (layouter instanceof HierarchicGroupLayouter) {
      HierarchicGroupLayouter groupLayouter = (HierarchicGroupLayouter)layouter;
      groupLayouter.setOrientationLayouter(GraphManager.getGraphManager().createOrientationLayouter(LayoutOrientation.TOP_TO_BOTTOM));
      groupLayouter.setMinimalNodeDistance(20);
      groupLayouter.setMinimalLayerDistance(50);
      groupLayouter.setRoutingStyle(HierarchicLayouter.ROUTE_ORTHOGONAL);
    }
    return layouter;
  }

  @Nullable
  @Override
  public Object getData(String dataId, List<DiagramNode<TCElement>> diagramNodes, final DiagramBuilder builder) {
    if (CommonDataKeys.PSI_ELEMENT.is(dataId)) {
      if ((diagramNodes.size() == 1)) {
        final TeamCitySettingsBasedFile tcFile = diagramNodes.get(0).getIdentifyingElement().getFile();
        return new TeamCityFileNamedReference(tcFile) {
          @Override
          public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
            PsiElement result = super.setName(name);

            ApplicationManager.getApplication().invokeLater(new Runnable() {
              @Override
              public void run() {
                builder.update(true, false);
                builder.getPresentationModel().update();
                builder.updateDataModel();
              }
            });
            return result;
          }
        };
      }

      final List<DiagramEdge> edges = DiagramUtils.getSelectedEdges(builder);
      if (edges.size() == 1) {
        DiagramEdge e = edges.get(0);
        if (e instanceof TCEdge) {
          TCEdge edge = (TCEdge) e;
          DiagramNode<TCElement> source = edge.getSource();
          DiagramNode<TCElement> target = edge.getTarget();
          TeamCitySettingsBasedFile tcFile = target.getIdentifyingElement().getFile();
          if (e.getRelationship() == TCRelationships.SNAPSHOT_ART || e.getRelationship() == TCRelationships.SNAPSHOT) {
            for (SnapshotDependencyElement el : tcFile.getSettingsElement().getSnapshotDependencies().getDependencies()) {
              if (source.getIdentifyingElement().getId().equals(el.getSourceBuildTypeId().getValue())) {
                XmlElement xmlElement = el.getXmlElement();
                if (xmlElement != null)
                  return xmlElement;
              }
            }
          } else {
            for (ArtifactDependencyElement el : tcFile.getSettingsElement().getArtifactDependencies().getDependencies()) {
              if (source.getIdentifyingElement().getId().equals(el.getSourceBuildTypeId().getValue())) {
                XmlElement xmlElement = el.getXmlElement();
                if (xmlElement != null)
                  return xmlElement;
              }
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

  @Nullable
  @Override
  public DiagramAddElementAction getAddElementHandler() {
    return new AddChildrenAction();
  }
}
