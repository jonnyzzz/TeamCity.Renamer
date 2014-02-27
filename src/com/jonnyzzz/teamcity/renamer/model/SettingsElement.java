package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTag;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class SettingsElement extends TeamCityElement {

//  @SubTagList("vcs-settings")
//  public abstract List<VcsRootElement> getVcsRoots();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

}
