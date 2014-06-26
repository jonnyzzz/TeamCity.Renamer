package com.jonnyzzz.teamcity.renamer.model.project;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.settings.DeclaredTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityFile {
  @Attribute("parent-id")
  public abstract GenericAttributeValue<String> getParentProjectIdElement();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();


  @NotNull
  @Override
  protected final String getFileKind() {
    return "project";
  }

  @Nullable
  public final String getParentProjectId() {
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
  public final String getFileId() {
    final PsiDirectory containingDirectory = getContainingDirectory();
    if (containingDirectory == null) return null;

    return containingDirectory.getName();
  }


  @NotNull
  @Override
  public final Iterable<DeclaredProperty> getDeclaredParameters() {
    return getParametersBlock().getDeclarations();
  }

  @Nullable
  @Override
  public final ProjectFile getParentProjectFile() {
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
  public final Iterable<BuildTypeFile> getBuildTypes() {
    return getBuildOrTemplates(BuildTypeFile.class);
  }

  @NotNull
  public final Iterable<BuildTemplateFile> getTemplates() {
    return getBuildOrTemplates(BuildTemplateFile.class);
  }

  @NotNull
  public final Iterable<VcsRootFile> getOwnVcsRoots() {
    return getProjectEntities("vcsRoots", VcsRootFile.class);
  }

  private static final Function<ProjectFile, Iterable<VcsRootFile>> FILE_TO_ROOTS
          = new Function<ProjectFile, Iterable<VcsRootFile>>() {
    @Override
    public Iterable<VcsRootFile> apply(ProjectFile projectFile) {
      return projectFile.getOwnVcsRoots();
    }
  };

  public final Iterable<VcsRootFile> getAllVcsRoots() {
    return Iterables.concat(FluentIterable
            .from(Visitors.getProjectFiles(this))
            .transformAndConcat(FILE_TO_ROOTS));
  }

  @NotNull
  public final Iterable<ProjectFile> getSubProjects() {
    final PsiDirectory thisDir = getContainingDirectory();
    if (thisDir == null) return Collections.emptyList();

    final PsiDirectory projectsDir = thisDir.getParentDirectory();
    if (projectsDir == null) return Collections.emptyList();

    final String thisProjectId = getFileId();
    if (thisProjectId == null) return Collections.emptyList();

    return FluentIterable
            .from(ImmutableList.copyOf(projectsDir.getSubdirectories()))
            .transform(new Function<PsiDirectory, ProjectFile>() {
              @Override
              public ProjectFile apply(PsiDirectory psiDirectory) {
                return toTeamCityFile(ProjectFile.class, psiDirectory.findFile(PROJECT_CONFIG_FILE_NAME));
              }
            })
            .filter(Predicates.notNull())
            .filter(new Predicate<ProjectFile>() {
              @Override
              public boolean apply(ProjectFile projectFile) {
                return thisProjectId.equals(projectFile.getParentProjectId());
              }
            });
  }

  @NotNull
  public final Iterable<DeclaredTemplate> getDeclaredTemplates() {
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
    return getProjectEntities("buildTypes", type);
  }

  @NotNull
  private <T extends TeamCityFile> Iterable<T> getProjectEntities(@NotNull final String subdirName,
                                                                  @NotNull final Class<T> entityType) {
    final PsiDirectory dir = getContainingDirectory();
    if (dir == null) return ImmutableList.of();

    final PsiDirectory subdir = dir.findSubdirectory(subdirName);
    if (subdir == null) return ImmutableList.of();

    return FluentIterable
            .from(ImmutableList.copyOf(subdir.getFiles()))
            .transform(new Function<PsiFile, T>() {
              @Override
              public T apply(PsiFile xmlFile) {
                return toTeamCityFile(entityType, xmlFile);
              }
            }).filter(Predicates.notNull());
  }
}
