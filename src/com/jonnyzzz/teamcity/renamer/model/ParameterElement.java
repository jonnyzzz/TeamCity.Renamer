package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReferenceConverter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.generate.tostring.util.StringUtil;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParameterElement extends TeamCityElement {

  @Required
  @NameValue
  @Attribute("name")
  public abstract GenericAttributeValue<String> getParameterName();

  @Attribute("value")
  @Convert(value = ParameterReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getParameterValue();


  @Nullable
  public String getParameterNameString() {
    final String name = getParameterName().getStringValue();
    if (name == null || StringUtil.isEmpty(name)) return null;
    return name.trim();
  }
}
