package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReferenceConverter implements CustomReferenceConverter<String> {
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

    List<TextRange> refRanges = Util.getRefRanges(text);

    for (TextRange refRange : refRanges) {
      int refStart = refRange.getStartOffset();
      int refEnd = refRange.getEndOffset();
      references.add(new ParameterReference(
              value,
              xmlValue,
              new TextRange(refStart + startOffset, refEnd + startOffset),
              text.substring(refStart, refEnd)));
    }

    return references.toArray(new PsiReference[references.size()]);
  }
}
