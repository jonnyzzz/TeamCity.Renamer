package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import org.jetbrains.annotations.NotNull;

public class DefineBuildRunnerParameter extends AbstractDefineNewParameterQuickFix {
  private final BuildRunnerElement myRunner;

  public DefineBuildRunnerParameter(String name, BuildRunnerElement runner) {
    super(name);
    myRunner = runner;
  }

  @Override
  protected ParametersBlockElement getWhereToPlace(ParameterElement element) {
    return myRunner.getParametersBlock();
  }

  @NotNull
  @Override
  public String getName() {
    return "Create runner parameter";
  }
}
