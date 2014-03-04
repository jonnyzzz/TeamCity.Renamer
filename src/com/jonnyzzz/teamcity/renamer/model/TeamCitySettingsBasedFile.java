package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TeamCitySettingsBasedFile extends TeamCityFile {

  @Nullable
  public final String getFileId() {
    final PsiFile containingFile = getContainingFile();
    if (containingFile == null) return null;

    return FileUtil.getNameWithoutExtension(containingFile.getName());
  }

  /**
   * @return containing project file. For ProjectFile returns parent project (if any)
   */
  @Nullable
  public final ProjectFile getParentProjectFile() {
    final PsiDirectory containingDir = getContainingDirectory();
    if (containingDir == null) return null;

    final PsiDirectory parentDir = containingDir.getParentDirectory();
    if (parentDir == null) return null;

    final PsiFile projectFile = parentDir.findFile(PROJECT_CONFIG_FILE_NAME);
    return TeamCityFile.toTeamCityFile(ProjectFile.class, projectFile);
  }

  @NotNull
  public SettingsElement getSettingsElement() {
    throw new RuntimeException("Must be implemented");
  }

  @NotNull
  @Override
  public final Iterable<DeclaredProperty> getDeclaredParameters() {
    return getSettingsElement().getParametersBlock().getDeclarations();
  }

}
