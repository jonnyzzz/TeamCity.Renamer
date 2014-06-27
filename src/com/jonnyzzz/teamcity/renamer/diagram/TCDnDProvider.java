package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.Nullable;

public class TCDnDProvider implements DiagramDnDProvider<TCElement> {
  @Override
  public boolean isAcceptedForDnD(Object o, Project project) {
    return o instanceof PsiElement;
  }

  @Nullable
  @Override
  public TCElement[] wrapToModelObject(Object o, Project project) {
    if (o instanceof PsiElement) {
      TeamCitySettingsBasedFile file = TeamCityFile.toTeamCityElement(TeamCitySettingsBasedFile.class, (PsiElement) o);
      if (file != null)
        return new TCElement[]{new TCElement(file)};
    }
    return new TCElement[0];
  }
}
