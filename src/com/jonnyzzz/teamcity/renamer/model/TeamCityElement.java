package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public abstract class TeamCityElement implements DomElement {

  public String toString() {
    final XmlTag tag = getXmlTag();
    if (tag == null) {
      return super.toString();
    }
    final String name = tag.getName();
    if ("".equals(name)) {
      return super.toString();
    }
    return name;
  }

}
