package com.jonnyzzz.teamcity.renamer.resolve.settings;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class SettingsReference extends PsiReferenceBase<PsiElement> {
  public SettingsReference(@NotNull final PsiElement element,
                           @NotNull final TextRange range) {
    super(element, range, false);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
