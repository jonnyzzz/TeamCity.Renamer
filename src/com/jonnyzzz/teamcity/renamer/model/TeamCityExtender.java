package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.reflect.DomExtender;
import com.intellij.util.xml.reflect.DomExtensionsRegistrar;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public class TeamCityExtender extends DomExtender<TeamCityElement> {
  @Override
  public void registerExtensions(@NotNull TeamCityElement teamCityElement,
                                 @NotNull DomExtensionsRegistrar domExtensionsRegistrar) {

  }
}
