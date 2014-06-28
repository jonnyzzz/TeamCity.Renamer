package com.jonnyzzz.teamcity.renamer.inspection;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsInspection;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperties;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ParameterOverridesHimselfInspection extends DomElementsInspection<TeamCityElement> {
  public ParameterOverridesHimselfInspection() {
    super(TeamCityElement.class, ParametersBlockElement.class, ParameterElement.class);
  }

  @Override
  protected void checkDomElement(DomElement element, DomElementAnnotationHolder holder, DomHighlightingHelper helper) {
    if (!(element instanceof ParameterElement)) {
      return;
    }
    final ParameterElement pe = (ParameterElement) element;
    String value = pe.getStringValue();
    final Iterable<DeclaredProperty> withSameName = Iterables.filter(DeclaredProperties.fromContext(pe), new Predicate<DeclaredProperty>() {
      @Override
      public boolean apply(DeclaredProperty declaredProperty) {
        return declaredProperty.getName().equals(pe.getParameterNameString()) && declaredProperty.getParameterElement() != pe;
      }
    });

    if (withSameName.iterator().hasNext()) {
      if (("%" + pe.getParameterName() + "%").equals(value)) {
        if (pe.getParentOfType(BuildRunnerElement.class, false) == null) {
          holder.createProblem(pe, HighlightSeverity.WARNING, "Can be simplified", new RemovePropertyQuickFix());
        }
      }
      final DeclaredProperty dp = withSameName.iterator().next();
      if (value != null && value.equals(dp.getRawValue())) {
        holder.createProblem(pe, HighlightSeverity.WARNING, "Can be simplified", new RemovePropertyQuickFix());
      }
    }
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return "TeamCity";
  }

  private static class RemovePropertyQuickFix implements LocalQuickFix {
    @NotNull
    @Override
    public String getName() {
      return "Remove property";
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return "TeamCity Renamer";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
      final PsiElement psiElement = problemDescriptor.getPsiElement();
      assert psiElement instanceof XmlTag;
      final ParameterElement pe = TeamCityFile.toTeamCityElement(ParameterElement.class, psiElement);
      assert pe != null;
      pe.undefine();
    }
  }
}
