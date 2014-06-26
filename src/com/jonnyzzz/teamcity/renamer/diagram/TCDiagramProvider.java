package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.*;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.SimpleColoredText;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramProvider extends BaseDiagramProvider<TCDElement> {
  @Override
  @Pattern("[a-zA-Z0-9_-]*")
  public String getID() {
    return "TeamCity";
  }

  @Override
  public DiagramElementManager<TCDElement> getElementManager() {
    return new AbstractDiagramElementManager<TCDElement>() {
      @Nullable
      @Override
      public TCDElement findInDataContext(@NotNull final DataContext context) {
        final Project project = CommonDataKeys.PROJECT.getData(context);
        if (project == null) return null;

        final PsiFile psiFile = DataKeys.PSI_FILE.getData(context);
        if (!(psiFile instanceof XmlFile)) return null;


        final BuildTypeFile file = TeamCityFile.toTeamCityFile(BuildTypeFile.class, psiFile);
        if (file == null) return null;

        return new TCDElement(file);
      }

      @Override
      public boolean isAcceptableAsNode(Object o) {
        return o instanceof TCDElement;
      }

      @Nullable
      @Override
      public String getElementTitle(TCDElement tcdElement) {
        return tcdElement.getName();
      }

      @Nullable
      @Override
      public SimpleColoredText getItemName(Object o, DiagramState diagramState) {
        return new SimpleColoredText(((TCDElement)o).getName(), DEFAULT_TITLE_ATTR);
      }

      @Override
      public String getNodeTooltip(TCDElement tcdElement) {
        return null;
      }
    };
  }

  @Override
  public DiagramVfsResolver<TCDElement> getVfsResolver() {
    return new DiagramVfsResolver<TCDElement>() {
      @Override
      public String getQualifiedName(TCDElement tcdElement) {
        return tcdElement.getId();
      }

      @Nullable
      @Override
      public TCDElement resolveElementByFQN(String s, Project project) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, s + ".xml", GlobalSearchScope.allScope(project));
        if (files.length == 0)
          return null;
        BuildTypeFile f = TeamCityFile.toTeamCityFile(BuildTypeFile.class, files[0]);
        return new TCDElement(f);
      }
    };
  }

  @Override
  public String getPresentableName() {
    return "TeamCity Build Dependencies";
  }


  private class TCDiagramNode extends DiagramNodeBase<TCDElement> {
    private final TCDElement myElement;

    public TCDiagramNode(@NotNull final TCDElement element) {
      super(TCDiagramProvider.this);
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
    public TCDElement getIdentifyingElement() {
      return myElement;
    }
  }

  private class TCDiagramEdge extends DiagramEdgeBase<TCDElement> {
    private final TCDiagramNode mySource;

    public TCDiagramEdge(@NotNull final TCDiagramNode source, @NotNull final TCDiagramNode target) {
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
    public TCDElement getIdentifyingElement() {
      return mySource.myElement;
    }
  }

  @Override
  public DiagramDataModel<TCDElement> createDataModel(@NotNull Project project,
                                                      @Nullable TCDElement tcdElement,
                                                      @Nullable VirtualFile virtualFile,
                                                      DiagramPresentationModel diagramPresentationModel) {
    List<BuildTypeFile> deps = tcdElement == null ? new ArrayList<BuildTypeFile>() : tcdElement.getBuildType().getSnapshotDependencies();
    final List<DiagramNode<TCDElement>> nodes = new ArrayList<>();
    final List<DiagramEdge<TCDElement>> edges = new ArrayList<>();
    if (tcdElement != null) {
      TCDiagramNode source = new TCDiagramNode(tcdElement);
      nodes.add(source);
      for (BuildTypeFile f : deps) {
        nodes.add(new TCDiagramNode(new TCDElement(f)));
        edges.add(new TCDiagramEdge(source, new TCDiagramNode(new TCDElement(f))));
      }
    }


    return new DiagramDataModel<TCDElement>(project, this) {

      private final List<DiagramNode<TCDElement>> myNodes = nodes;
      private final List<DiagramEdge<TCDElement>> myEdges = edges;

      @NotNull
      @Override
      public Collection<? extends DiagramNode<TCDElement>> getNodes() {
        return myNodes;
      }

      @NotNull
      @Override
      public Collection<? extends DiagramEdge<TCDElement>> getEdges() {
        return myEdges;
      }

      @NotNull
      @Override
      public String getNodeName(DiagramNode<TCDElement> diagramNode) {
        return diagramNode.getIdentifyingElement().getName();
      }

      @Nullable
      @Override
      public DiagramNode<TCDElement> addElement(final TCDElement tcdElement) {
        final TCDiagramNode el = new TCDiagramNode(tcdElement);
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
    };
  }
}
