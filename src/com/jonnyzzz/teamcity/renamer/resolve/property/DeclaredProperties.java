package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

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

  @NotNull
  private static Iterable<ProjectFile> getProjectFiles(@Nullable final ProjectFile file) {
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

}
