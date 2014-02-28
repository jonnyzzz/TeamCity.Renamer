package com.jonnyzzz.teamcity.renamer.model.template;

import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTemplateFile extends TeamCitySettingsBasedFile {
  @Required
  @SubTag("settings")
  public abstract SettingsElement getSettings();
}
