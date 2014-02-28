package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DeclaredTemplate {
  private final String myName;
  private final BuildTemplateFile myFile;

  public DeclaredTemplate(@NotNull final String name,
                          @NotNull final BuildTemplateFile file) {
    myName = name;
    myFile = file;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public BuildTemplateFile getFile() {
    return myFile;
  }
}
