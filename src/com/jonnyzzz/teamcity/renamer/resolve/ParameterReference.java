package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> {
  @NotNull
  private final GenericDomValue<String> myAttr;

  public ParameterReference(@NotNull final GenericDomValue<String> attr,
                            @NotNull final PsiElement element,
                            @NotNull final TextRange range) {
    super(element, range, true);
    myAttr = attr;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }



}
