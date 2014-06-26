package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PropertyFromTextProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof XmlToken)) return PsiReference.EMPTY_ARRAY;
    final PsiElement parent = element.getParent();
    if (!(parent instanceof XmlText) && !(parent.getParent() instanceof XmlText)) {
      return PsiReference.EMPTY_ARRAY;
    }
    DomElement value = TeamCityFile.findContainingDomElement(parent);
    if (value == null) {
      return PsiReference.EMPTY_ARRAY;
    }

    int startOffset = element.getTextRange().getStartOffset() - element.getTextOffset();
    final String text = element.getText();

    final List<ParameterReference> references = new ArrayList<>(0);

    if (text == null) return PsiReference.EMPTY_ARRAY;
    for (int idx = 0; idx < text.length(); idx++) {

      if (text.charAt(idx) == '%') {
        idx++;
        final int refStart = idx + startOffset;

        while (idx < text.length() && text.charAt(idx) != '%') idx++;
        if (idx >= text.length()) break;

        final int refEnd = idx + startOffset;

        if (refStart < refEnd) {
          references.add(new ParameterReference(
                  value,
                  element,
                  new TextRange(refStart, refEnd),
                  text.substring(refStart - startOffset, refEnd - startOffset)));
        }

        //skip last % char
        idx++;
      }
    }

    return references.toArray(new PsiReference[references.size()]);
  }
}
