package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTag;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class SettingsElement extends TeamCityElement {

  @SubTag("vcs-settings")
  public abstract VcsSettingsElement getVcsSettings();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

  @SubTag("dependencies")
  public abstract SnapshotDependenciesElement getSnapshotDependencies();

}
