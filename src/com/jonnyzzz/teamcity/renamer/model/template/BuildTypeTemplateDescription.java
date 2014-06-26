package com.jonnyzzz.teamcity.renamer.model.template;

import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeTemplateDescription extends TeamCityFileDescriptionBase<BuildTemplateFile> {
  public BuildTypeTemplateDescription() {
    super(BuildTemplateFile.class, "template");
  }
}
