package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTag;

public abstract class ArtifactDependencyElement extends DependencyElement  {

  @SubTag("artifact")
  public abstract ArtifactElement getArtifact();

}
