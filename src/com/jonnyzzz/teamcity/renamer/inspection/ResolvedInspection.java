package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ResolvedInspection extends BasicDomElementsInspection<TeamCityElement> {
  public ResolvedInspection() {
    //noinspection unchecked
    super(TeamCityElement.class);
  }

  @Override
  protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
    if (element instanceof ParameterElement)  {
      super.checkDomElement(element, holder, helper);
    }
    else if (element.getParentOfType(SettingsElement.class, false) != null)  {
      super.checkDomElement(element, holder, helper);
    }
  }

}
