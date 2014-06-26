package com.jonnyzzz.teamcity.renamer.diagram;

import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCDElement {
  private final BuildTypeFile myFile;

  public TCDElement(@NotNull final BuildTypeFile file) {
    myFile = file;
  }

  @NotNull
  public String getId() {
    return "" + myFile.getFileId();
  }

  @NotNull
  public String getName() {
    return "" + myFile.getName().getStringValue();
  }

}
