package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> {
  @NotNull
  private final GenericDomValue<String> myAttr;
  @NotNull
  private final String myReferredVariableName;

  public ParameterReference(@NotNull final GenericDomValue<String> attr,
                            @NotNull final PsiElement element,
                            @NotNull final TextRange range,
                            @NotNull final String referredVariableName) {
    super(element, range, true);
    myAttr = attr;
    myReferredVariableName = referredVariableName;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    final ParametersBlockElement nearParameters = myAttr.getParentOfType(ParametersBlockElement.class, true);

    if (nearParameters != null) {
      for (ParameterElement element : nearParameters.getParameters()) {
        if (myReferredVariableName.equals(element.getParameterName().getStringValue())) {
          return element.getParameterName().getXmlAttributeValue();
        }
      }
    }

    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }



}
