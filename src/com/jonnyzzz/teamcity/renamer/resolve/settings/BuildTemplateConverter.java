package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReferenceConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTemplateConverter extends TeamCityFileReferenceConverter<BuildTemplateReference> {

  @Override
  protected BuildTemplateReference createReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    return new BuildTemplateReference(attr, element);
  }
}
