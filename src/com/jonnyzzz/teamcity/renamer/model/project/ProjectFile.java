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
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ParametersSettingsBlock;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.apache.commons.lang.text.StrTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityFile {
  @Attribute("parent-id")
  public abstract GenericAttributeValue<String> getParentProjectIdElement();

  @Stubbed
  @SubTag("parameters")
  public abstract ParametersSettingsBlock getParametersBlock();


  @NotNull
  @Override
  public final String getFileKind() {
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

  public final Iterable<BuildTypeFile> getAllBuildTypes() {
    final PsiDirectory containingDir = getContainingDirectory();
    if (containingDir == null) return ImmutableList.of();

    PsiDirectory projectsDir = containingDir.getParent();
    if (projectsDir == null) return ImmutableList.of();

    List<ProjectFile> projects = new ArrayList<>();
    for (PsiDirectory projectDir : projectsDir.getSubdirectories()) {
      ProjectFile p = toTeamCityFile(ProjectFile.class, projectDir.findFile(PROJECT_CONFIG_FILE_NAME));
      if (p != null)
        projects.add(p);
    }

    return Iterables.concat(FluentIterable
            .from(projects)
            .transformAndConcat(FILE_TO_DECLARED_BUILD_TYPES));
  }

  @NotNull
  public final Iterable<BuildTemplateFile> getTemplates() {
    return getBuildOrTemplates(BuildTemplateFile.class);
  }

  @NotNull
  public final Iterable<VcsRootFile> getOwnVcsRoots() {
    return getProjectEntities("vcsRoots", VcsRootFile.class);
  }

  public final Iterable<VcsRootFile> getAllVcsRoots() {
    return Iterables.concat(FluentIterable
            .from(Visitors.getProjectFiles(this))
            .transformAndConcat(FILE_TO_ROOTS));
  }

  @Nullable
  public PsiDirectory getOrCreateMetaRunnersDirectory() {
    PsiDirectory dir = getContainingDirectory();
    if (dir == null) return null;

    PsiDirectory pd = dir.findSubdirectory("pluginData");
    if (pd == null) pd = dir.createSubdirectory("pluginData");

    PsiDirectory mr = pd.findSubdirectory("metaRunners");
    if (mr == null) mr = pd.createSubdirectory("metaRunners");

    return mr;
  }

  public final Iterable<MetaRunnerFile> getOwnMetaRunners() {
    return getProjectEntities("pluginData/metaRunners", MetaRunnerFile.class);
  }

  public final Iterable<MetaRunnerFile> getAllMetaRunners() {
    return Iterables.concat(FluentIterable
            .from(Visitors.getProjectFiles(this))
            .transformAndConcat(FILE_TO_META_RUNNERS));
  }

  @NotNull
  public final Iterable<BuildTemplateFile> getOwnBuildTemplates() {
    return getProjectEntities("buildTypes", BuildTemplateFile.class);
  }

  @NotNull
  public final Iterable<BuildTypeFile> getOwnBuildTypes() {
    return getProjectEntities("buildTypes", BuildTypeFile.class);
  }

  public final Iterable<BuildTemplateFile> getAllBuildTemplates() {
    return Iterables.concat(FluentIterable
            .from(Visitors.getProjectFiles(this))
            .transformAndConcat(FILE_TO_BUILD_TEMPLATES));
  }

  @NotNull
  public final Iterable<ProjectFile> getSubProjects() {
    final String thisProjectId = getFileId();
    if (thisProjectId == null) return ImmutableList.of();
    return FluentIterable.from(Visitors.getAllProjects(this))
            .filter(new Predicate<ProjectFile>() {
              @Override
              public boolean apply(ProjectFile projectFile) {
                return thisProjectId.equals(projectFile.getParentProjectId());
              }
            });
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

    StrTokenizer subDirTokenizer = new StrTokenizer(subdirName, '/');
    PsiDirectory targetDir = dir;
    while(subDirTokenizer.hasNext() && targetDir != null) {
      targetDir = targetDir.findSubdirectory(subDirTokenizer.nextToken());
    }
    if (targetDir == null) return ImmutableList.of();

    return FluentIterable
            .from(ImmutableList.copyOf(targetDir.getFiles()))
            .transform(new Function<PsiFile, T>() {
              @Override
              public T apply(PsiFile xmlFile) {
                return toTeamCityFile(entityType, xmlFile);
              }
            }).filter(Predicates.notNull());
  }

  private static final Function<ProjectFile, Iterable<VcsRootFile>> FILE_TO_ROOTS
          = new Function<ProjectFile, Iterable<VcsRootFile>>() {
    @Override
    public Iterable<VcsRootFile> apply(ProjectFile projectFile) {
      return projectFile.getOwnVcsRoots();
    }
  };

  private static final Function<ProjectFile, Iterable<MetaRunnerFile>> FILE_TO_META_RUNNERS
          = new Function<ProjectFile, Iterable<MetaRunnerFile>>() {
    @Override
    public Iterable<MetaRunnerFile> apply(ProjectFile projectFile) {
      return projectFile.getOwnMetaRunners();
    }
  };

  private static final Function<ProjectFile, Iterable<BuildTemplateFile>> FILE_TO_BUILD_TEMPLATES
          = new Function<ProjectFile, Iterable<BuildTemplateFile>>() {
    @Override
    public Iterable<BuildTemplateFile> apply(ProjectFile projectFile) {
      return projectFile.getOwnBuildTemplates();
    }
  };

  private static final Function<ProjectFile, Iterable<BuildTypeFile>> FILE_TO_DECLARED_BUILD_TYPES
          = new Function<ProjectFile, Iterable<BuildTypeFile>>() {
    @Override
    public Iterable<BuildTypeFile> apply(ProjectFile projectFile) {
      return projectFile.getBuildTypes();
    }
  };
}
