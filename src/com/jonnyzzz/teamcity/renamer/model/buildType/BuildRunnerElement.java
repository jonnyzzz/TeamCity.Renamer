package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

public abstract class BuildRunnerElement extends TeamCityElement {

  @Attribute("id")
  public abstract GenericAttributeValue<String> getId();

  @Required
  @Attribute("type")
  public abstract GenericAttributeValue<String> getType();

  @Attribute("name")
  public abstract GenericAttributeValue<String> getName();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();
}
