package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class VcsRootElement extends TeamCityElement {

  @Attribute("root-id")
  public abstract GenericAttributeValue<String> getVcsRootId();


}
