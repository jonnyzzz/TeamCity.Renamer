package com.jonnyzzz.teamcity.renamer.model.project;

import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ProjectFileDescription extends TeamCityFileDescriptionBase<ProjectFile> {
  public ProjectFileDescription() {
    super(ProjectFile.class, "project");
  }
}
