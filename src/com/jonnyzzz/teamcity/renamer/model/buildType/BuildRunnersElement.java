package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

import java.util.List;

public abstract class BuildRunnersElement extends TeamCityElement {

  @Attribute("order")
  public abstract GenericAttributeValue<String> getOrder();

  @SubTagList("runner")
  public abstract List<BuildRunnerElement> getRunners();
}
