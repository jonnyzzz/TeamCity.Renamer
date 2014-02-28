package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReferenceConverter extends Converter<String> implements CustomReferenceConverter<String> {
  @NotNull
  @Override
  public PsiReference[] createReferences(GenericDomValue<String> _value, PsiElement element, ConvertContext context) {
    final GenericAttributeValue<String> value = (GenericAttributeValue<String>) _value;
    final XmlAttributeValue xmlValue = value.getXmlAttributeValue();
    if(xmlValue == null) return PsiReference.EMPTY_ARRAY;


    int startOffset = xmlValue.getValueTextRange().getStartOffset() - xmlValue.getTextOffset() + 1;
    final String text = value.getStringValue();

    final List<ParameterReference> references = new ArrayList<ParameterReference>(0);

    if (text == null) return PsiReference.EMPTY_ARRAY;
    for(int idx = 0; idx < text.length(); idx++) {

      if (text.charAt(idx) == '%') {
        idx++;
        final int refStart = idx + startOffset;

        while (idx < text.length() && text.charAt(idx) != '%') idx++;
        if (idx >= text.length()) break;

        final int refEnd = idx + startOffset;

        if (refStart < refEnd) {
          references.add(new ParameterReference(
                  value,
                  xmlValue,
                  new TextRange(refStart, refEnd),
                  text.substring(refStart - startOffset, refEnd - startOffset)));
        }

        //skip last % char
        idx++;
      }
    }

    return references.toArray(new PsiReference[references.size()]);
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
