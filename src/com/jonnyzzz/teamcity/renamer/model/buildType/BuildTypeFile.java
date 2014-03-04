package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTypeFile extends TeamCitySettingsBasedFile {

  @NotNull
  @Override
  protected String getFileKind() {
    return "build configuration";
  }

  @Required
  @SubTag("settings")
  public abstract BuildTypeSettingsElement getSettings();

  @NotNull
  @Override
  public final SettingsElement getSettingsElement() {
    return getSettings();
  }
}
