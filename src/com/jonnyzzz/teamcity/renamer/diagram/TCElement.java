package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.pom.Navigatable;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TCElement implements Navigatable {
  private final TeamCitySettingsBasedFile myFile;

  public TCElement(@NotNull final TeamCitySettingsBasedFile file) {
    myFile = file;
  }

  @NotNull
  public String getId() {
    return "" + myFile.getFileId();
  }

  @NotNull
  public String getName() {
    return "" + myFile.getFileName().getStringValue();
  }

  @NotNull
  public TeamCitySettingsBasedFile getFile() {
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
