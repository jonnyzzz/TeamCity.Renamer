package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReferenceConverter;
import org.jetbrains.annotations.NotNull;

public class VcsRootReferenceConverter extends TeamCityFileReferenceConverter<VcsRootReference> {

  @Override
  protected VcsRootReference createReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    return new VcsRootReference(attr, element);
  }
}
