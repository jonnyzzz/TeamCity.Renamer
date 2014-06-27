package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.Nullable;

class TCVfsResolver implements DiagramVfsResolver<TCElement> {
  @Override
  public String getQualifiedName(TCElement tcdElement) {
    return tcdElement.getId();
  }

  @Nullable
  @Override
  public TCElement resolveElementByFQN(String s, Project project) {
    PsiFile[] files = FilenameIndex.getFilesByName(project, s + ".xml", GlobalSearchScope.allScope(project));
    if (files.length == 0)
      return null;
    TeamCitySettingsBasedFile f = TeamCityFile.toTeamCityFile(TeamCitySettingsBasedFile.class, files[0]);
    if (f == null)
      return null;
    return new TCElement(f);
  }
}
