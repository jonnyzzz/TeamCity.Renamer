package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;

import static com.jonnyzzz.teamcity.renamer.resolve.Visitors.getProjectFiles;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DeclaredTemplates {
  @NotNull
  public static Iterable<DeclaredTemplate> fromContext(@NotNull final DomElement element) {
    final TeamCityFile file = element.getParentOfType(TeamCityFile.class, false);
    if (file == null) return ImmutableList.of();

    return FluentIterable
            .from(getProjectFiles(file.getParentProjectFile()))
            .transformAndConcat(new Function<ProjectFile, Iterable<DeclaredTemplate>>() {
              @Override
              public Iterable<DeclaredTemplate> apply(ProjectFile projectFile) {
                return projectFile.getDeclaredTemplates();
              }
            });
  }

}
