package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.AbstractDiagramElementManager;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.SimpleColoredText;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class TCElementManager extends AbstractDiagramElementManager<TCElement> {
  @Nullable
  @Override
  public TCElement findInDataContext(@NotNull final DataContext context) {
    final Project project = CommonDataKeys.PROJECT.getData(context);
    if (project == null) return null;

    final PsiFile psiFile = DataKeys.PSI_FILE.getData(context);
    if (!(psiFile instanceof XmlFile)) return null;


    final TeamCitySettingsBasedFile file = TeamCityFile.toTeamCityFile(TeamCitySettingsBasedFile.class, psiFile);
    if (file == null) return null;

    return new TCElement(file);
  }

  @Override
  public boolean isAcceptableAsNode(Object o) {
    return o instanceof TCElement;
  }

  @Nullable
  @Override
  public String getElementTitle(TCElement tcdElement) {
    return tcdElement.getName();
  }

  @Nullable
  @Override
  public SimpleColoredText getItemName(Object o, DiagramState diagramState) {
    return new SimpleColoredText(((TCElement)o).getName(), DEFAULT_TITLE_ATTR);
  }

  @Override
  public String getNodeTooltip(TCElement tcdElement) {
    return null;
  }
}
