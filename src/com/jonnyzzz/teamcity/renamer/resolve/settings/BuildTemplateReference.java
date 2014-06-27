package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTemplateReference extends TeamCityFileReference<BuildTemplateFile> {
  protected BuildTemplateReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(attr, element);
  }

  @Override
  protected Iterable<BuildTemplateFile> getAll(@NotNull ProjectFile projectFile) {
    return projectFile.getAllBuildTemplates();
  }
}
