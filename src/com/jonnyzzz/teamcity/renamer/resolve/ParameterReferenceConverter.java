package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReferenceConverter extends Converter<String> implements CustomReferenceConverter<String> {
  @NotNull
  @Override
  public PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
    return new PsiReference[] { new ParameterReference(value, element, ElementManipulators.getManipulator(element).getRangeInElement(element))};
  }

  @Nullable
  @Override
  public String fromString(@Nullable @NonNls String s, ConvertContext context) {
    return s;
  }

  @Nullable
  @Override
  public String toString(@Nullable String s, ConvertContext context) {
    return s;
  }
}
