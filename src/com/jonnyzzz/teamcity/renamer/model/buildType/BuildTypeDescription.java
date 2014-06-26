package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeDescription extends TeamCityFileDescriptionBase<BuildTypeFile> {
  public BuildTypeDescription() {
    super(BuildTypeFile.class, "build-type");
  }
}
