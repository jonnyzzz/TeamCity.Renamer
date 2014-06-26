package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> {
  private static final Pattern BUILT_IN_PARAMETER_PATTERN = Pattern.compile("env\\..*|teamcity\\.tool\\..*|system\\.agent\\..*");

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
    if (checkIfBuiltInParameter()) {
      return new TeamCityPredefinedParameter(myReferredVariableName);
    }

    final PsiElement declaredResolve = resolvePropertyFromContext(myAttr, myReferredVariableName);
    if (declaredResolve != null) return declaredResolve;

    final PsiElement depResolve = resolveDepParameter();
    if (depResolve != null) return depResolve;

    return null;
  }

  @Nullable
  private PsiElement resolvePropertyFromContext(@NotNull final DomElement context, @NotNull String referredVariableName) {
    final TeamCityFile file = TeamCityFile.toTeamCityFile(TeamCityFile.class, myElement.getContainingFile());
    if (file == null) return null;

    for (DeclaredProperty property : DeclaredProperties.fromContext(context)) {
      if (referredVariableName.equals(property.getName())) {
        return new RenameableParameterElement(file, property);
      }
    }
    return null;
  }

  @Nullable
  private PsiElement resolveDepParameter() {
    if (!myReferredVariableName.startsWith("dep.")) return null;

    final int dot2 = myReferredVariableName.indexOf('.', "dep.".length());
    if (dot2 <= 0 && dot2 + 1 < myReferredVariableName.length()) return null;

    final String buildTypeId = myReferredVariableName.substring("dep.".length(), dot2);
    final BuildTypeFile buildType = Visitors.findBuildType(myAttr, buildTypeId);
    if (buildType == null) return null;

    return resolvePropertyFromContext(buildType, myReferredVariableName.substring(dot2+1));
  }

  private boolean checkIfBuiltInParameter() {
    return BUILT_IN_PARAMETER_PATTERN.matcher(myReferredVariableName).matches();
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

  private static class TeamCityPredefinedParameter extends FakePsiElement {
    private final String myName;

    public TeamCityPredefinedParameter(@NotNull final String name) {
      myName = name;
    }

    @Override
    public boolean isWritable() {
      return false;
    }

    @Override
    public String getName() {
      return myName;
    }

    @Override
    public PsiElement getParent() {
      return null;
    }

    @Override
    public String getPresentableText() {
      return getLocationString() + " :: " + getName();
    }

    @Nullable
    @Override
    public String getLocationString() {
      return "[TeamCity Predefined]";
    }
  }
}
