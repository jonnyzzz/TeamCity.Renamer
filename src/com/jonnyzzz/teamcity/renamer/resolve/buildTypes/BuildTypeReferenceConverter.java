package com.jonnyzzz.teamcity.renamer.resolve.buildTypes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuildTypeReferenceConverter extends Converter<String> implements CustomReferenceConverter<String> {

  @NotNull
  @Override
  public PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
    GenericAttributeValue<String> val = (GenericAttributeValue<String>) value;
    XmlAttributeValue xmlValue = val.getXmlAttributeValue();
    if(xmlValue == null) return PsiReference.EMPTY_ARRAY;

    return new PsiReference[]{new BuildTypeReference(val, element)};
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
