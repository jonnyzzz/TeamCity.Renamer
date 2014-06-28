package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

import java.util.List;

public abstract class BuildRunnersElement extends TeamCityElement {

  @Attribute("order")
  public abstract GenericAttributeValue<String> getOrder();

  @SubTagList("runner")
  public abstract List<BuildRunnerElement> getRunners();

  @SubTagsList(value="runner", tagName = "runner")
  public abstract BuildRunnerElement addRunner();

  @SubTagsList(value="runner", tagName = "runner")
  public abstract BuildRunnerElement addRunner(int index);
}
