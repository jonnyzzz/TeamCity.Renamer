package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.pom.Navigatable;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCElement implements Navigatable {
  private final BuildTypeFile myFile;

  public TCElement(@NotNull final BuildTypeFile file) {
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

  @NotNull
  public BuildTypeFile getBuildType() {
    return myFile;
  }

  @Override
  public void navigate(boolean requestFocus) {

  }

  @Override
  public boolean canNavigate() {
    return false;
  }

  @Override
  public boolean canNavigateToSource() {
    return false;
  }
}
