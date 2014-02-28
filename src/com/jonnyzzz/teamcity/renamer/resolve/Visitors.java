package com.jonnyzzz.teamcity.renamer.resolve;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

}
