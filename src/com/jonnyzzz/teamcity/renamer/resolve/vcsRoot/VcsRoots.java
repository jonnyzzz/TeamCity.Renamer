package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class VcsRoots {

  @Nullable
  public static VcsRootFile resolve(@Nullable final DomElement context,
                                    @Nullable final String rootId) {
    if (context == null) return null;
    if (rootId == null) return null;

    TeamCityFile file = context.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return null;

    for (final VcsRootFile vcsRootFile : projectFile.getAllVcsRoots()) {
      if (rootId.equals(vcsRootFile.getFileId())) {
        return vcsRootFile;
      }
    }
    return null;
  }
}
