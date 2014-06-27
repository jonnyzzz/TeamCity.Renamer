package com.jonnyzzz.teamcity.renamer.model.template;

import com.intellij.util.xml.Required;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTemplateFile extends TeamCitySettingsBasedFile {
  @Required
  @Stubbed
  @SubTag("settings")
  public abstract SettingsElement getSettings();

  @NotNull
  @Override
  protected final String getFileKind() {
    return "template";
  }

  @NotNull
  @Override
  public final SettingsElement getSettingsElement() {
    return getSettings();
  }
}
