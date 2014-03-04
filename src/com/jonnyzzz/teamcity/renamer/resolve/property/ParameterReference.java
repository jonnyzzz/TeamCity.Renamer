package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


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
    super(element, range, false);
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
  private Set<String> findSelfNames() {
    final ParameterElement self = myAttr.getParentOfType(ParameterElement.class, false);
    if (self != null) return Collections.singleton(self.getParameterNameString());
    return Collections.emptySet();
  }


  @NotNull
  @Override
  public Object[] getVariants() {
    final List<LookupElement> result = new ArrayList<LookupElement>(0);
    final Set<String> names = new HashSet<>(findSelfNames());
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
