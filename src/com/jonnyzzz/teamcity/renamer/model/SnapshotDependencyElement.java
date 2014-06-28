package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTag;

public abstract class SnapshotDependencyElement extends DependencyElement {
  @SubTag("options")
  public abstract OptionsElement getOptions();

  @SubTag(value="options")
  public abstract OptionsElement addOptions();
}
