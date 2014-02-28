package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TeamCityFile extends TeamCityElement {

  @Nullable
  public String getFileId() {
    throw new RuntimeException("Must be implemented");
  }

  /**
   * @return containing project file. For ProjectFile returns parent project (if any)
   */
  @Nullable
  public ProjectFile getParentProjectFile() {
    throw new RuntimeException("Must be implemented");
  }

  @NotNull
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    throw new RuntimeException("Must be implemented");
  }

  @Override
  public String toString() {
    return "[" + getFileId() + "] " + super.toString();
  }


  @Nullable
  protected static <T extends TeamCityFile> T toTeamCityFile(@NotNull final Class<T> clazz,
                                                             @Nullable final PsiFile psiFile) {
    if (psiFile == null) return null;
    if (!psiFile.getName().endsWith(".xml")) return null;
    if (!(psiFile instanceof XmlFile)) return null;

    final XmlFile xmlFile = (XmlFile) psiFile;
    final Project project = psiFile.getProject();
    final DomElement xml = DomManager
            .getDomManager(project)
            .getDomElement(xmlFile.getRootTag());

    if (xml == null) return null;
    if (!clazz.isInstance(xml)) return null;
    return clazz.cast(xml);
  }

}
