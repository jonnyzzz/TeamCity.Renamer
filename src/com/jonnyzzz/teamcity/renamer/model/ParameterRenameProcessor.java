package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.collect.Iterables;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperties;
import com.jonnyzzz.teamcity.renamer.resolve.property.RenameableParameterElement;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterRenameProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return null != ParameterElement.fromPsiElement(element);
  }

  @Override
  public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames, SearchScope scope) {
    super.prepareRenaming(element, newName, allRenames, ProjectScope.getAllScope(element.getProject()));


    final ParameterElement param = ParameterElement.fromPsiElement(element);
    if (param == null) return;

    final TeamCityFile paramFile = param.getParentOfType(TeamCityFile.class, false);
    if (paramFile == null) return;

    for (ParameterElement p : Iterables.concat(
            DeclaredProperties.findOverriddenByChildrenParameters(Arrays.asList(param)).values(),
            DeclaredProperties.findOverriddenParametersFromParents(Arrays.asList(param)).values())) {

      RenameableParameterElement ref = p.toRenameableReference(paramFile);
      if (ref != null) {
        allRenames.put(ref, newName);
      }
    }
  }

  @Override
  public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames) {
    super.prepareRenaming(element, newName, allRenames);
  }

}
