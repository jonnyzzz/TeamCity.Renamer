package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTemplates {

  @Nullable
  public static BuildTemplateFile resolve(@Nullable final DomElement context,
                                          @Nullable final String rootId) {
    if (context == null) return null;
    if (rootId == null) return null;

    TeamCityFile file = context.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return null;

    for (final BuildTemplateFile buildTemplateFile : projectFile.getAllBuildTemplates()) {
      if (rootId.equals(buildTemplateFile.getFileId())) {
        return buildTemplateFile;
      }
    }
    return null;
  }
}
