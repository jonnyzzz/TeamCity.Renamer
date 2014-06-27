package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public abstract class ArtifactDependenciesElement extends TeamCityElement {
  @SubTagList("dependency")
  public abstract List<ArtifactDependencyElement> getDependencies();

  @SubTagList("dependency")
  public abstract ArtifactDependencyElement addArtifactDependencyElement();
}
