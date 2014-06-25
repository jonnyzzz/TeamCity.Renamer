package com.jonnyzzz.teamcity.renamer.model.vcsRoot;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VcsRootFile extends TeamCityFile {

  @NotNull
  @Override
  protected final String getFileKind() {
    return "vcsRoot";
  }

  @NotNull
  @Override
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    return super.getDeclaredParameters();
  }

  @Nullable
  public final ProjectFile getParentProjectFile() {
    final PsiDirectory containingDir = getContainingDirectory();
    if (containingDir == null) return null;

    final PsiDirectory parentDir = containingDir.getParentDirectory();
    if (parentDir == null) return null;

    final PsiFile projectFile = parentDir.findFile(PROJECT_CONFIG_FILE_NAME);
    return TeamCityFile.toTeamCityFile(ProjectFile.class, projectFile);
  }
}
