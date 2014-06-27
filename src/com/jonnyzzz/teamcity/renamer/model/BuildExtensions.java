package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*/
public abstract  class BuildExtensions extends TeamCityElement {
  @SubTagList("extension")
  public abstract List<BuildExtension> getExtensions();
}
