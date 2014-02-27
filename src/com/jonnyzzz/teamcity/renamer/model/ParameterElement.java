package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.resolve.ParameterReferenceConverter;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParameterElement extends TeamCityElement {

  @Attribute("name")
  public abstract GenericAttributeValue<String> getParameterName();

  @Attribute("value")
  @Convert(ParameterReferenceConverter.class)
  public abstract GenericAttributeValue<String> getParameterValue();

}
