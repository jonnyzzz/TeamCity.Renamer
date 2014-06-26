package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.resolve.vcsRoot.VcsRootReferenceConverter;

public abstract class VcsRootEntryRefElement extends TeamCityElement {
  @Attribute("root-id")
  @Convert(value = VcsRootReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getVcsRootId();
}
