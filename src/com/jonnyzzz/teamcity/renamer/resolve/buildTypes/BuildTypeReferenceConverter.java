package com.jonnyzzz.teamcity.renamer.resolve.buildTypes;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReferenceConverter;
import org.jetbrains.annotations.NotNull;

public class BuildTypeReferenceConverter extends TeamCityFileReferenceConverter<BuildTypeReference> {

  @Override
  protected BuildTypeReference createReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    return new BuildTypeReference(attr, element);
  }
}
