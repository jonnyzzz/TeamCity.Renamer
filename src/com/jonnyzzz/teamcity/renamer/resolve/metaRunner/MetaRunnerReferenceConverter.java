package com.jonnyzzz.teamcity.renamer.resolve.metaRunner;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReferenceConverter;
import org.jetbrains.annotations.NotNull;

public class MetaRunnerReferenceConverter extends TeamCityFileReferenceConverter<MetaRunnerReference> {
  @Override
  protected MetaRunnerReference createReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    return new MetaRunnerReference(attr, element);
  }
}
