package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTypeFile extends TeamCityElement {

  @SubTag("name")
  public abstract GenericDomValue<String> getBuildTypeName();

  @SubTag("description")
  public abstract GenericDomValue<String> getBuildTypeDescription();

  @SubTag("settings")
  public abstract SettingsElement getSettings();

}
