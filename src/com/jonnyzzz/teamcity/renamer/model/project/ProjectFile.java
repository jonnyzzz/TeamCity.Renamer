package com.jonnyzzz.teamcity.renamer.model.project;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.settings.DeclaredTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityFile {

  @Attribute("parent-id")
  public abstract GenericAttributeValue<String> getParentProjectIdElement();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

  @Nullable
  public String getParentProjectId() {
    final GenericAttributeValue<String> parentProjectAttribute = getParentProjectIdElement();
    if (parentProjectAttribute != null) {
      final String parentProjectId = parentProjectAttribute.getStringValue();
      if (parentProjectId != null && parentProjectId.trim().length() > 0) {
        return parentProjectId;
      }
    }
    if ("_Root".equals(getFileId())) return null;
    return "_Root";
  }

  @Nullable
  public String getFileId() {
    final PsiDirectory containingDirectory = getContainingDirectory();
    if (containingDirectory == null) return null;

    return containingDirectory.getName();
  }


  @NotNull
  @Override
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    return getParametersBlock().getDeclarations();
  }

  @Nullable
  @Override
  public ProjectFile getParentProjectFile() {
    final String parentProjectId = getParentProjectId();
    if (parentProjectId == null) return null;

    final PsiDirectory containingDir = getContainingDirectory();
    if (containingDir == null) return null;

    final PsiDirectory projectsDir = containingDir.getParent();
    if (projectsDir == null) return null;

    final PsiDirectory parentDir = projectsDir.findSubdirectory(parentProjectId);
    if (parentDir == null) return null;

    return toTeamCityFile(ProjectFile.class, parentDir.findFile(PROJECT_CONFIG_FILE_NAME));
  }

  @NotNull
  public Iterable<BuildTypeFile> getBuildTypes() {
    return getBuildOrTemplates(BuildTypeFile.class);
  }

  @NotNull
  public Iterable<BuildTemplateFile> getTemplates() {
    return getBuildOrTemplates(BuildTemplateFile.class);
  }

  @NotNull
  public Iterable<DeclaredTemplate> getDeclaredTemplates() {
    return FluentIterable
            .from(getTemplates())
            .transform(new Function<BuildTemplateFile, DeclaredTemplate>() {
              @Override
              public DeclaredTemplate apply(BuildTemplateFile buildTemplateFile) {
                final String fileId = buildTemplateFile.getFileId();
                if (fileId == null) return null;
                return new DeclaredTemplate(fileId, buildTemplateFile);
              }
            }).filter(Predicates.notNull());
  }

  @NotNull
  private <T extends TeamCityFile> Iterable<T> getBuildOrTemplates(@NotNull final Class<T> type) {
    final PsiDirectory dir = getContainingDirectory();
    if (dir == null) return ImmutableList.of();

    final PsiDirectory buildTypesDir = dir.findSubdirectory("buildTypes");
    if (buildTypesDir == null) return ImmutableList.of();

    return FluentIterable
            .from(ImmutableList.copyOf(buildTypesDir.getFiles()))
            .transform(new Function<PsiFile, T>() {
              @Override
              public T apply(PsiFile xmlFile) {
                return toTeamCityFile(type, xmlFile);
              }
            }).filter(Predicates.notNull());
  }
}
