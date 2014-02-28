package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.settings.SettingsRefConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTypeSettingsElement extends SettingsElement {
  @NotNull
  @Required
  @Attribute("ref")
  @Convert(value = SettingsRefConverter.class, soft = false)
  public abstract GenericAttributeValue<BuildTemplateFile> getBaseTemplate();
}
