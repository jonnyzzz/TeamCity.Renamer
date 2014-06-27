package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDefineNewParameterQuickFix implements LocalQuickFix {
  private final String myNewParameterName;

  public AbstractDefineNewParameterQuickFix(String newParameterName) {
    myNewParameterName = newParameterName;
  }

  @NotNull
  @Override
  public final String getFamilyName() {
    return "Create parameter";
  }

  @Override
  public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
    final PsiElement element = descriptor.getPsiElement();
    final DomElement domElement = DomUtil.getDomElement(element);
    if (domElement == null) {
      return;
    }
    final ParameterElement pe = domElement.getParentOfType(ParameterElement.class, false);
    if (pe == null) {
      return;
    }
    ParametersBlockElement parameters = getWhereToPlace(pe);
    if (parameters == null) {
      return;
    }
    final XmlTag parameter = ParameterReference.addParameter(parameters, myNewParameterName, "");
    //noinspection ConstantConditions
    final XmlAttributeValue ve = parameter.getAttribute("value").getValueElement();
    if (ve instanceof Navigatable) {
      ((Navigatable) ve).navigate(true);
    }
  }

  protected abstract ParametersBlockElement getWhereToPlace(ParameterElement element);
}
