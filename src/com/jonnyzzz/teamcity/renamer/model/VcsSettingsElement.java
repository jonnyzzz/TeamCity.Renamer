package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public abstract class VcsSettingsElement extends TeamCityElement {
  @SubTagList("vcs-entry-ref")
  public abstract List<VcsRootEntryRefElement> getVcsRoots();
}
