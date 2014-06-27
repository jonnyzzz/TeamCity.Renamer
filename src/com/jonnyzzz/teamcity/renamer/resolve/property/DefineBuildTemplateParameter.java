package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.NotNull;

public class DefineBuildTemplateParameter extends AbstractDefineNewParameterQuickFix {
  private final BuildTemplateFile myTemplate;

  public DefineBuildTemplateParameter(String name, BuildTemplateFile template) {
    super(name);
    myTemplate = template;
  }

  @Override
  protected ParametersBlockElement getWhereToPlace(ParameterElement element) {
    return myTemplate.getSettingsElement().getParametersBlock();
  }

  @NotNull
  @Override
  public String getName() {
    return "Create build type template parameter";
  }
}
