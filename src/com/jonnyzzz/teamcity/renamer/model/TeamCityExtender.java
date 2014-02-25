package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.reflect.DomExtender;
import com.intellij.util.xml.reflect.DomExtensionsRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class TeamCityExtender extends DomExtender<TeamCityElement> {
  @Override
  public void registerExtensions(@NotNull TeamCityElement teamCityElement, @NotNull DomExtensionsRegistrar domExtensionsRegistrar) {
    if (teamCityElement instanceof XmlElement && teamCityElement.getXmlElementName().equals("build-type")) {
      domExtensionsRegistrar.

    }
  }
}
