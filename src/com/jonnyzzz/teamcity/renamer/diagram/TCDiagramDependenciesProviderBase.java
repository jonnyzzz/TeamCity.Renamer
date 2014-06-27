package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramElementsProvider;
import com.intellij.openapi.project.Project;
import com.intellij.uml.utils.UmlBundle;

import java.util.Comparator;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TCDiagramDependenciesProviderBase implements DiagramElementsProvider<TCElement> {
  private static final Comparator<TCElement> COMPARATOR = new Comparator<TCElement>() {
    @Override
    public int compare(TCElement o1, TCElement o2) {
      return o1.getName().compareToIgnoreCase(o2.getName());
    }
  };

  @Override
  public abstract TCElement[] getElements(TCElement element, Project project);

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
