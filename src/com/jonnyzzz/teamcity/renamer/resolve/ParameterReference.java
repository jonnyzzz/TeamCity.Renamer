package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> {
  @NotNull
  private final GenericDomValue<String> myAttr;
  @NotNull
  private final String myReferredVariableName;

  public ParameterReference(@NotNull final GenericDomValue<String> attr,
                            @NotNull final PsiElement element,
                            @NotNull final TextRange range,
                            @NotNull final String referredVariableName) {
    super(element, range, true);
    myAttr = attr;
    myReferredVariableName = referredVariableName;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    for (DeclaredProperty property : DeclaredProperties.fromContext(myAttr)) {
      if (myReferredVariableName.equals(property.getName())) {
        return property.getResolvedValue();
      }
    }
    return null;
  }


  @NotNull
  @Override
  public Object[] getVariants() {
    final List<LookupElement> result = new ArrayList<LookupElement>(0);
    final Set<String> names = new HashSet<>();
    for (DeclaredProperty variant : DeclaredProperties.fromContext(myAttr)) {
      final String name = variant.getName();

      //skip overrides
      if (!names.add(name)) continue;

      final LookupElementBuilder builder = LookupElementBuilder.create(name).withCaseSensitivity(false);
      final LookupElement element = AutoCompletionPolicy.GIVE_CHANCE_TO_OVERWRITE.applyPolicy(builder);
      result.add(element);
    }

    return result.toArray();
  }
}
