package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ResolvedInspection extends BasicDomElementsInspection<SettingsElement> {
  public ResolvedInspection() {
    //noinspection unchecked
    super(SettingsElement.class);
  }
}
