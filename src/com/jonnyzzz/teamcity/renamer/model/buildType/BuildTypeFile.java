package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.settings.BuildTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  @Stubbed
  @SubTag("settings")
  public abstract BuildTypeSettingsElement getSettings();

  @NotNull
  @Override
  public final SettingsElement getSettingsElement() {
    return getSettings();
  }

  @Nullable
  public final BuildTemplateFile getBaseTemplate() {
    final GenericAttributeValue<String> baseTemplateElement = getSettings().getBaseTemplate();
    final String templateId = baseTemplateElement.getStringValue();

    final BuildTemplateFile resolved = BuildTemplates.resolve(this, templateId);
    if (resolved == null) return null;

    return resolved;
  }
}
