package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlElementPattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlToken;

import static com.intellij.patterns.XmlPatterns.*;

public class PropertyFromTextContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
    final XmlElementPattern.XmlTextPattern textInParamTag = xmlText().inside(
            xmlTag().withName("param")
    );
    registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlToken.class).withSuperParent(1, textInParamTag).withText(string().contains("%")),
            new PropertyFromTextProvider()
    );
    registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlToken.class).withSuperParent(2, textInParamTag).withText(string().contains("%")),
            new PropertyFromTextProvider()
    );
  }
}
