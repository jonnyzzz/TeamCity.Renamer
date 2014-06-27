package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import org.jetbrains.annotations.NotNull;

public class DefineSettingsParameter extends AbstractDefineNewParameterQuickFix {
  private final SettingsElement mySettings;
  private final String myText;

  public DefineSettingsParameter(String name, SettingsElement settings, String text) {
    super(name);
    mySettings = settings;
    myText = text;
  }

  @Override
  protected ParametersBlockElement getWhereToPlace(ParameterElement element) {
    return mySettings.getParametersBlock();
  }

  @NotNull
  @Override
  public String getName() {
    return myText;
  }
}
