package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public abstract class SnapshotDependenciesElement extends TeamCityElement {
  @SubTagList("depend-on")
  public abstract List<SnapshotDependencyElement> getDependencies();
}
