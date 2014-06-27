package com.jonnyzzz.teamcity.renamer.model.vcsRoot;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class VcsRootFile extends TeamCityFile {

  @NotNull
  @Override
  protected final String getFileKind() {
    return "vcsRoot";
  }

  @Stubbed
  @SubTagList("param")
  public abstract List<ParameterElement> getParameters();

  @SubTag("name")
  public abstract GenericAttributeValue<String> getVcsRootName();

  @NotNull
  @Override
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    return FluentIterable
            .from(getParameters())
            .transform(DeclaredProperty.FROM_PARAMETER_ELEMENT)
            .filter(Predicates.notNull());
  }

  @Nullable
  @Override
  public String getFileId() {
    final PsiFile containingFile = getContainingFile();
    if (containingFile == null) return null;

    return FileUtil.getNameWithoutExtension(containingFile.getName());
  }

  @Override
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
