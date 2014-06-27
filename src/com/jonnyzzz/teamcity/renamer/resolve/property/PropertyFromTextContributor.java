package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlToken;

import static com.intellij.patterns.XmlPatterns.*;

public class PropertyFromTextContributor extends PsiReferenceContributor {

  static final XmlElementPattern.XmlTextPattern textInParamTag = xmlText().inside(
          xmlTag().withName("param")
  );
  public static final PsiElementPattern.Capture[] PATTERNS = new PsiElementPattern.Capture[]{
          PlatformPatterns.psiElement(XmlToken.class).withParent(textInParamTag).withText(string().contains("%")),
          PlatformPatterns.psiElement(XmlToken.class).withParent(psiElement(XmlElementType.XML_CDATA).withParent(textInParamTag)).withText(string().contains("%"))
  };

  @Override
  public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
    for (PsiElementPattern.Capture pattern : PATTERNS) {
      registrar.registerReferenceProvider(pattern, new PropertyFromTextProvider());
    }
  }
}
