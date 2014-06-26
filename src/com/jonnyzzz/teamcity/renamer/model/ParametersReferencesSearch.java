package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.property.RenameableParameterElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParametersReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
  public ParametersReferencesSearch() {
    super(true);
  }

  @Override
  public void processQuery(@NotNull final ReferencesSearch.SearchParameters queryParameters,
                           @NotNull final Processor<PsiReference> consumer) {
    final PsiElement element = queryParameters.getElementToSearch();
    final ParameterElement param = ParameterElement.fromPsiElement(element);
    if (param == null) return;

    final TeamCityFile file = param.getParentOfType(TeamCityFile.class, false);
    if (file == null) return;

    /*

    report(consumer, file, DeclaredProperties.findOverriddenByChildrenParameters(Arrays.asList(param)).values());
    report(consumer, file, DeclaredProperties.findOverriddenParametersFromParents(Arrays.asList(param)).values());
  */}

  private void report(@NotNull final Processor<PsiElement> consumer,
                      @NotNull final TeamCityFile file,
                      @NotNull final Collection<ParameterElement> values) {
    for (ParameterElement value : values) {
      final DeclaredProperty apply = DeclaredProperty.FROM_PARAMETER_ELEMENT.apply(value);
      if (apply == null) continue;

      consumer.process(new RenameableParameterElement(file, apply));
    }
  }


}
