package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramElementsProvider;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.ShortcutSet;
import com.intellij.openapi.project.Project;
import com.intellij.uml.utils.UmlBundle;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDiagramElementsProvider implements DiagramElementsProvider<TCElement> {

  private static final Comparator<TCElement> COMPARATOR = new Comparator<TCElement>() {
    @Override
    public int compare(TCElement o1, TCElement o2) {
      return o1.getName().compareToIgnoreCase(o2.getName());
    }
  };

  @Override
  public TCElement[] getElements(TCElement element, Project project) {
    final ArrayList<TCElement> items = new ArrayList<>();

    final Set<String> used = new HashSet<>();

    for (BuildTypeFile snapshotDependency : element.getFile().getSnapshotDependencies()) {
      if (!used.add(snapshotDependency.getFileId())) continue;
      items.add(new TCElement(snapshotDependency));
    }
    for (BuildTypeFile artifactDependency : element.getFile().getArtifactDependencies()) {
      if (!used.add(artifactDependency.getFileId())) continue;
      items.add(new TCElement(artifactDependency));
    }
    return items.toArray(new TCElement[items.size()]);
  }

  @Override
  public String getName() {
    return "Show Dependencies";
  }

  @Override
  public String getHeaderName(TCElement element, Project project) {
    return "Dependencies for " + element.getName();
  }

  @Override
  public ShortcutSet getShortcutSet() {
    return new CustomShortcutSet(KeyStroke.getKeyStroke(VK_ENTER, 0));
  }

  @Override
  public Comparator<? super TCElement> getComparator() {
    return COMPARATOR;
  }

  @Override
  public boolean showProgress() {
    return true;
  }

  @Override
  public String getProgressMessage() {
    return UmlBundle.message("looking.for.dependencies");
  }

  @Override
  public boolean isEnabledOn(TCElement element) {
    return true;
  }

}
