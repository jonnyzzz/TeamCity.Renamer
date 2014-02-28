package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;

import static com.jonnyzzz.teamcity.renamer.resolve.Visitors.getProjectFiles;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DeclaredProperties {

  @NotNull
  public static Iterable<DeclaredProperty> fromContext(@NotNull final DomElement element) {
    final TeamCityFile file = element.getParentOfType(TeamCityFile.class, false);
    if (file == null) return ImmutableList.of();

    return Iterables.concat(
            file.getDeclaredParameters(),
            FluentIterable
                    .from(getProjectFiles(file.getParentProjectFile()))
                    .transformAndConcat(new Function<ProjectFile, Iterable<DeclaredProperty>>() {
                      @Override
                      public Iterable<DeclaredProperty> apply(ProjectFile projectFile) {
                        return projectFile.getDeclaredParameters();
                      }
                    }),
            getParametersFromTemplate(file)
    );
  }


  @NotNull
  private static Iterable<DeclaredProperty> getParametersFromTemplate(@NotNull final TeamCityFile file) {
    //TODO: fixme
    return ImmutableList.of();
  }


}
