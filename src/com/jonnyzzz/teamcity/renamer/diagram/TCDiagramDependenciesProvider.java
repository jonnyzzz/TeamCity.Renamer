package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.Project;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.resolve.deps.Dependencies;

import javax.swing.*;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramDependenciesProvider extends TCDiagramDependenciesProviderBase {

  @Override
  public TCElement[] getElements(TCElement element, Project project) {
    final ArrayList<TCElement> items = new ArrayList<>();
    for (BuildTypeFile file : Dependencies.getDependencies(element.getFile())) {
      items.add(new TCElement(file));
    }
    return items.toArray(new TCElement[items.size()]);
  }

  @Override
  public ShortcutSet getShortcutSet() {
    return new CustomShortcutSet(KeyStroke.getKeyStroke(VK_ENTER, 0));
  }


  @Override
  public String getName() {
    return "Show Dependencies";
  }

  @Override
  public String getHeaderName(TCElement element, Project project) {
    return "Dependencies for " + element.getName();
  }

}
