package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.jonnyzzz.teamcity.renamer.resolve.ParameterReferenceConverter;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParameterElement extends TeamCityElement {

  @NameValue
  @Attribute("name")
  public abstract GenericAttributeValue<String> getParameterName();

  @Attribute("value")
  @Convert(value = ParameterReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getParameterValue();

}
