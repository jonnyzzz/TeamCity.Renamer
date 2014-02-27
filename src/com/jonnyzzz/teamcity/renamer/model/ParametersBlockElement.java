package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParametersBlockElement extends TeamCityElement {

  @SubTagList("param")
  public abstract List<ParameterElement> getParameters();

}
