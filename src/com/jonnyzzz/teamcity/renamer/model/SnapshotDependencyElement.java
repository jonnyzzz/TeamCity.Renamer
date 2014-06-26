package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.resolve.buildTypes.BuildTypeReferenceConverter;

public abstract class SnapshotDependencyElement extends TeamCityElement {
  @Attribute("sourceBuildTypeId")
  @Convert(value = BuildTypeReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getSourceBuildTypeId();
}
