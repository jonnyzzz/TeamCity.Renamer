package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class SettingsReference extends PsiReferenceBase<PsiElement> {
  @NotNull
  private final DomElement myContext;
  @NotNull
  private final String myName;

  public SettingsReference(@NotNull final DomElement context,
                           @NotNull final String name,
                           @NotNull final PsiElement element,
                           @NotNull final TextRange range) {
    super(element, range, false);
    myContext = context;
    myName = name;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    for (DeclaredTemplate template : DeclaredTemplates.fromContext(myContext)) {
      if (template.getName().equals(myName)) {
        return template.getFile().getSettingsElement().getXmlElement();
      }
    }

    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    final List<LookupElement> result = new ArrayList<LookupElement>(0);
    final Set<String> names = new HashSet<>();
    for (DeclaredTemplate variant : DeclaredTemplates.fromContext(myContext)) {
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
