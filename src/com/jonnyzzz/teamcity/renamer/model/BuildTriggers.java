package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*/
public abstract class BuildTriggers extends TeamCityElement {
  @SubTagList("build-trigger")
  public abstract List<BuildTrigger> getTriggers();
}
