package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class RenameableParameterElement extends RenameableFakePsiElement {
  private final TeamCityFile myRequester;
  private final DeclaredProperty myProperty;

  public RenameableParameterElement(@NotNull final TeamCityFile requester,
                                    @NotNull final DeclaredProperty property) {
    super(property.getResolvedValue());
    myRequester = requester;
    myProperty = property;
  }

  @Override
  public String getName() {
    return myProperty.getName();
  }

  @Override
  public String getPresentableText() {
    return getLocationString() + " :: " + getName();
  }

  @Nullable
  @Override
  public String getLocationString() {
    final TeamCityFile file = myProperty.getParameterElement().getParentOfType(TeamCityFile.class, false);

    if (file == null) return super.getPresentableText();
    return file.getFilePresentableNameText();
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    myProperty.getParameterElement().setParameterName(name);
    return this;
  }

  @Override
  public String getTypeName() {
    return "Parameter";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return null;
  }

  @NotNull
  @Override
  public PsiElement getNavigationElement() {
    return myProperty.getResolvedValue();
  }

  @Override
  public TextRange getTextRange() {
    return myProperty.getResolvedValue().getTextRange();
  }

  @Override
  public boolean isEquivalentTo(PsiElement another) {
    if (another instanceof RenameableParameterElement && ((RenameableParameterElement) another).myProperty.getName().equals(myProperty.getName())) {
      return myProperty.getParameterElement().equals(((RenameableParameterElement) another).myProperty.getParameterElement());
    }
    return myProperty.getParameterElement().equals(ParameterElement.fromPsiElement(another));
  }
}
