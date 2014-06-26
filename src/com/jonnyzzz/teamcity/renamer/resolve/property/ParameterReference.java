package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> {
  @NotNull
  private final DomElement myAttr;
  @NotNull
  private final String myReferredVariableName;

  public ParameterReference(@NotNull final DomElement attr,
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
    final TeamCityFile file = TeamCityFile.toTeamCityFile(TeamCityFile.class, myElement.getContainingFile());
    if (file == null) return null;

    for (DeclaredProperty property : DeclaredProperties.fromContext(myAttr)) {
      if (myReferredVariableName.equals(property.getName())) {
        return new RenameableParameterElement(file, property);
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

      final LookupElementBuilder builder = LookupElementBuilder.create(variant, name).withCaseSensitivity(false);
      result.add(builder);
    }

    return result.toArray();
  }

  //TODO: add     <automaticRenamerFactory implementation="com.intellij.refactoring.rename.naming.AutomaticInheritorRenamerFactory"/>


  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return super.handleElementRename(newElementName);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return super.isReferenceTo(element);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return super.bindToElement(element);
  }

  private static class RenameableParameterElement extends RenameableFakePsiElement {
    private final TeamCityFile myRequester;
    private final DeclaredProperty myProperty;

    private RenameableParameterElement(@NotNull final TeamCityFile requester,
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
}
