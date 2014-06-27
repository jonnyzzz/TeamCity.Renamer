package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.resolve.settings.BuildTemplateConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTypeSettingsElement extends SettingsElement {
  @NotNull
  @Attribute("ref")
  @Convert(value = BuildTemplateConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getBaseTemplate();
}
