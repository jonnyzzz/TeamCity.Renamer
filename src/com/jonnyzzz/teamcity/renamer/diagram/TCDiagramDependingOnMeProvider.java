package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.Project;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.resolve.deps.Dependencies;

import javax.swing.*;
import java.util.ArrayList;

import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramDependingOnMeProvider extends TCDiagramDependenciesProviderBase {
  @Override
  public TCElement[] getElements(TCElement element, Project project) {
    final ArrayList<TCElement> items = new ArrayList<>();
    for (TeamCitySettingsBasedFile file : Dependencies.getDependingOnMe(element.getFile())) {
      items.add(new TCElement(file));
    }
    return items.toArray(new TCElement[items.size()]);
  }


  @Override
  public ShortcutSet getShortcutSet() {
    return new CustomShortcutSet(KeyStroke.getKeyStroke(VK_ENTER, SHIFT_MASK));
  }

  @Override
  public String getName() {
    return "Show referrers";
  }

  @Override
  public String getHeaderName(TCElement element, Project project) {
    return "Referrers for " + element.getName();
  }

}
