package com.jonnyzzz.teamcity.renamer.resolve;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class Visitors {
  @NotNull
  public static Iterable<ProjectFile> getProjectFiles(@Nullable final ProjectFile file) {
    if (file == null) return ImmutableList.of();

    return new Iterable<ProjectFile>() {
      @Override
      public Iterator<ProjectFile> iterator() {
        return new AbstractIterator<ProjectFile>() {
          private ProjectFile nextFile = file;

          @Override
          protected ProjectFile computeNext() {
            final ProjectFile next = nextFile;
            if (next == null) return endOfData();

            nextFile = next.getParentProjectFile();
            return next;
          }
        };
      }
    };
  }

  @Nullable
  public static BuildTypeFile findBuildType(@Nullable final DomElement context,
                                            @Nullable final String buildTypeId) {
    if (context == null) return null;
    if (buildTypeId == null) return null;


    for (ProjectFile projectFile : getAllProjects(context)) {
      for (BuildTypeFile buildTypeFile : projectFile.getAllBuildTypes()) {

        if (buildTypeId.equals(buildTypeFile.getFileId())) {
          return buildTypeFile;
        }
      }
    }
    return null;
  }

  @Nullable
  public static VcsRootFile findVCSRoot(@Nullable final TeamCityFile context,
                                        @Nullable String vcsRootId) {
    if (context == null) return null;
    if (vcsRootId == null) return null;

    for (ProjectFile projectFile : getAllProjects(context)) {
      for (VcsRootFile buildTypeFile : projectFile.getAllVcsRoots()) {

        if (vcsRootId.equals(buildTypeFile.getFileId())) {
          return buildTypeFile;
        }
      }
    }
    return null;
  }

  @Nullable
  private static PsiDirectory getRootDirectory(@Nullable final TeamCityElement element) {
    if (element == null) return null;

    final ProjectFile projectFile = element.getParentOfType(ProjectFile.class, false);
    if (projectFile != null) {
      return projectFile.getContainingDirectory();
    }

    final TeamCityFile file = element.getParentOfType(TeamCityFile.class, false);
    if (file == null) return null;

    final ProjectFile theProject = file.getParentProjectFile();
    if (theProject == null) return null;
    return theProject.getContainingDirectory();
  }

  @NotNull
  public static Iterable<ProjectFile> getAllProjects(@Nullable DomElement element) {
    if (element == null) return ImmutableList.of();

    final ProjectFile project = element.getParentOfType(ProjectFile.class, false);
    if (project != null) {
      return getAllProjects(project);
    }

    final TeamCityFile file = element.getParentOfType(TeamCityFile.class, false);
    if (file != null) {
      return getAllProjects(file.getParentProjectFile());
    }
    return ImmutableList.of();
  }

  @NotNull
  public static Iterable<ProjectFile> getAllProjects(@Nullable final ProjectFile project) {
    if (project == null) return ImmutableList.of();

    //project directory
    final PsiDirectory thisDir = project.getContainingDirectory();
    if (thisDir == null) return Collections.emptyList();

    //all projects directory
    final PsiDirectory projectsDir = thisDir.getParentDirectory();
    if (projectsDir == null) return Collections.emptyList();

    return FluentIterable
            .from(ImmutableList.copyOf(projectsDir.getSubdirectories()))
            .transform(new Function<PsiDirectory, ProjectFile>() {
              @Override
              public ProjectFile apply(PsiDirectory psiDirectory) {
                return TeamCityFile.toTeamCityFile(ProjectFile.class, psiDirectory.findFile(ProjectFile.PROJECT_CONFIG_FILE_NAME));
              }
            })
            .filter(Predicates.notNull());
  }

}
