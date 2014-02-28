package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class SettingsRefConverter extends Converter<String> implements CustomReferenceConverter<String>{

  @NotNull
  @Override
  public PsiReference[] createReferences(@NotNull final GenericDomValue<String> value,
                                         @NotNull final PsiElement element,
                                         @NotNull final ConvertContext context) {
    if (!(value instanceof GenericAttributeValue)) return PsiReference.EMPTY_ARRAY;

    final GenericAttributeValue<String> attr = (GenericAttributeValue<String>) value;
    final XmlAttributeValue attrValue = attr.getXmlAttributeValue();

    if (attrValue == null) return PsiReference.EMPTY_ARRAY;

    final TextRange range = attrValue.getValueTextRange().shiftRight(1-attrValue.getTextOffset());
    return new PsiReference[] { new SettingsReference(attrValue, range)};
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
