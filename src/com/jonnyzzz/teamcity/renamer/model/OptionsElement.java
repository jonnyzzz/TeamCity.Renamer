package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;

import java.util.List;

public abstract class OptionsElement extends TeamCityElement {
  @SubTagList("option")
  public abstract List<OptionElement> getOptions();

  @SubTagsList(value="option", tagName = "option")
  public abstract OptionElement addOption();

  @SubTagsList(value="option", tagName = "option")
  public abstract OptionElement addOption(int index);

}


