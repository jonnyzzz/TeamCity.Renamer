package com.jonnyzzz.teamcity.renamer.model.project;

import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityElement {

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

}
