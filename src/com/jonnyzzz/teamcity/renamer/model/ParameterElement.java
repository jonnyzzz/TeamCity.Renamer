package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReferenceConverter;

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

}
