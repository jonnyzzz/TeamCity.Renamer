package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReferenceConverter;

public abstract class ArtifactElement extends TeamCityElement {
  @Attribute("sourcePath")
  @Referencing(value = ParameterReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getSourcePath();
}
