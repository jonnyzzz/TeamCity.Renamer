package com.jonnyzzz.teamcity.renamer.resolve.metaRunner;

import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class MetaRunners {

  @Nullable
  public static MetaRunnerFile resolve(@Nullable final DomElement context,
                                       @Nullable final String type) {
    if (context == null) return null;
    if (type == null) return null;

    TeamCityFile file = context.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return null;

    for (final MetaRunnerFile metaRunnerFile : projectFile.getAllMetaRunners()) {
      if (type.equals(metaRunnerFile.getFileId())) {
        return metaRunnerFile;
      }
    }
    return null;
  }
}
