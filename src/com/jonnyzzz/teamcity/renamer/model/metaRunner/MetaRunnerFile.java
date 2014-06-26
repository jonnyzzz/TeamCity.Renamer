package com.jonnyzzz.teamcity.renamer.model.metaRunner;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeSettingsElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ivan Chirkov
 */
public abstract class MetaRunnerFile extends TeamCitySettingsBasedFile {
  @Required
  @Attribute("name")
  public abstract GenericAttributeValue<String> getNameAttr();

  @Required
  @SubTag("settings")
  public abstract BuildTypeSettingsElement getSettings();

  @NotNull
  @Override
  public SettingsElement getSettingsElement() {
    return getSettings();
  }
}
