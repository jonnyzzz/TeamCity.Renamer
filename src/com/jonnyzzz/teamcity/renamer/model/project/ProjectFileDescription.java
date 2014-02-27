package com.jonnyzzz.teamcity.renamer.model.project;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ProjectFileDescription extends DomFileDescription<ProjectFile> {
  public ProjectFileDescription() {
    super(ProjectFile.class, "project");
  }
}
