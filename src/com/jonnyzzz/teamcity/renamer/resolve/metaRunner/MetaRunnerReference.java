package com.jonnyzzz.teamcity.renamer.resolve.metaRunner;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import org.jetbrains.annotations.NotNull;

public class MetaRunnerReference extends TeamCityFileReference<MetaRunnerFile> {

  protected MetaRunnerReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(attr, element);
  }

  @Override
  protected Iterable<MetaRunnerFile> getAll(@NotNull ProjectFile projectFile) {
    return projectFile.getAllMetaRunners();
  }
}
