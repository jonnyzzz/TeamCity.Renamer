package com.jonnyzzz.teamcity.renamer.model.metaRunner;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Chirkov
 */
public abstract class MetaRunnerFile extends TeamCityFile {
  @Required
  @Attribute("name")
  public abstract GenericAttributeValue<String> getRunnerName();

  @Required
  @Stubbed
  @SubTag("settings")
  public abstract SettingsElement getSettings();

  @Override
  @Nullable
  public final ProjectFile getParentProjectFile() {
    final PsiDirectory containingDir = getContainingDirectory();
    if (containingDir == null) return null;

    PsiDirectory parentDir = containingDir.getParentDirectory();
    if (parentDir == null) return null;
    parentDir = parentDir.getParentDirectory();
    if (parentDir == null) return null;

    final PsiFile projectFile = parentDir.findFile(PROJECT_CONFIG_FILE_NAME);
    return TeamCityFile.toTeamCityFile(ProjectFile.class, projectFile);
  }

}
