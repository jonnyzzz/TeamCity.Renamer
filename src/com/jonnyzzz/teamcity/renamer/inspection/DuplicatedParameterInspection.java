package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsInspection;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import com.intellij.util.xml.highlighting.RemoveDomElementQuickFix;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicatedParameterInspection extends DomElementsInspection<TeamCityElement> {
  public DuplicatedParameterInspection() {
    super(TeamCityElement.class, ParametersBlockElement.class, ParameterElement.class);
  }

  @Override
  protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
    if (!(element instanceof ParametersBlockElement)) {
      return;
    }
    final ParametersBlockElement pbe = (ParametersBlockElement) element;
    final List<ParameterElement> parameters = pbe.getParameters();
    Set<String> names = new HashSet<>();
    Set<String> dups = new HashSet<>();
    for (ParameterElement parameter : parameters) {
      final String name = parameter.getParameterName().getValue();
      if (!names.add(name)) {
        dups.add(name);
      }
    }
    for (ParameterElement parameter : parameters) {
      final String name = parameter.getParameterName().getValue();
      if (!dups.contains(name)) {
        continue;
      }
      holder.createProblem(parameter, HighlightSeverity.ERROR, "Duplicated property name", new RemoveDomElementQuickFix(parameter));
    }
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return "TeamCity";
  }
}
