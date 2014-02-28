package com.jonnyzzz.teamcity.renamer.model;

import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TeamCityFile extends TeamCityElement {

  /**
   * @return containing project file. For ProjectFile returns parent project (if any)
   */
  @Nullable
  public ProjectFile getParentProjectFile() {
    throw new RuntimeException("Must be implemented");
  }

  @NotNull
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    throw new RuntimeException("Must be implemented");
  }
}
