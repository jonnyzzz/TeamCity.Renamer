package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.collect.Iterables;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperties;
import com.jonnyzzz.teamcity.renamer.resolve.property.RenameableParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParametersFindUsagesFactory extends FindUsagesHandlerFactory {
  @Override
  public boolean canFindUsages(@NotNull PsiElement element) {
    return ParameterElement.fromPsiElement(element) != null;
  }

  @Nullable
  @Override
  public FindUsagesHandler createFindUsagesHandler(@NotNull final PsiElement element, boolean forHighlightUsages) {
    return new FindUsagesHandler(element) {
      @NotNull
      @Override
      public PsiElement[] getPrimaryElements() {
        final ParameterElement param = ParameterElement.fromPsiElement(element);
        if (param == null) return super.getPrimaryElements();

        final List<PsiElement> elements = new ArrayList<>();
        for (ParameterElement p : Iterables.concat(
                DeclaredProperties.findOverriddenByChildrenParameters(Arrays.asList(param)).values(),
                DeclaredProperties.findOverriddenParametersFromParents(Arrays.asList(param)).values())) {

          final RenameableParameterElement pP = p.toRenameableReference(param);
          if (pP != null) {
            elements.add(pP);
          }
        }
        Collections.addAll(elements, super.getPrimaryElements());

        return elements.toArray(new PsiElement[elements.size()]);
      }
    };
  }
}
