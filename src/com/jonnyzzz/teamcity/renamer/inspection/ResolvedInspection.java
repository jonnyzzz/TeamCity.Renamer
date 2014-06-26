package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ResolvedInspection extends BasicDomElementsInspection<TeamCityElement> {
  public ResolvedInspection() {
    //noinspection unchecked
    super(TeamCityElement.class);
  }
}
