package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class SettingsElement extends TeamCityElement {

  @SubTag("vcs-settings")
  public abstract VcsSettingsElement getVcsRoots();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

}
