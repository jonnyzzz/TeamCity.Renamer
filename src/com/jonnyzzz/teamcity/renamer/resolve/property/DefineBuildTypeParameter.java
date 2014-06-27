package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import org.jetbrains.annotations.NotNull;

public class DefineBuildTypeParameter extends AbstractDefineNewParameterQuickFix {
  private final BuildTypeFile myBuildTypeFile;

  public DefineBuildTypeParameter(String name, BuildTypeFile buildTypeFile) {
    super(name);
    myBuildTypeFile = buildTypeFile;
  }

  @Override
  protected ParametersBlockElement getWhereToPlace(ParameterElement element) {
    return myBuildTypeFile.getSettingsElement().getParametersBlock();
  }

  @NotNull
  @Override
  public String getName() {
    return "Create build type parameter";
  }
}
