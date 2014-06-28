package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.DomPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.patterns.XmlTagPattern;
import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.*;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityPredefined;
import com.jonnyzzz.teamcity.renamer.resolve.metaRunner.MetaRunnerReference;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.property.RenameableParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterInMetaRunnerReferenceConvertor extends PsiReferenceContributor {

  @Override
  public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
    XmlTagPattern.Capture request = XmlPatterns.xmlTag()
            .and(DomPatterns.withDom(DomPatterns.domElement(ParameterElement.class).withSuperParent(2, BuildRunnerElement.class)));
    registrar.registerReferenceProvider(request, new PsiReferenceProvider() {
      @NotNull
      @Override
      public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        final ParameterElement parameter = ParameterElement.fromPsiElement(element);
        if (parameter == null) return PsiReference.EMPTY_ARRAY;

        final BuildRunnerElement runnerElement = parameter.getParentOfType(BuildRunnerElement.class, false);
        if (runnerElement == null) return PsiReference.EMPTY_ARRAY;

        final ParametersBlockElement domElement = runnerElement.getParametersBlock();
        if (domElement == null) return PsiReference.EMPTY_ARRAY;


        GenericAttributeValue<String> parameterName = parameter.getParameterName();
        if (parameterName == null) return PsiReference.EMPTY_ARRAY;

        XmlAttribute xmlAttributeValue = parameterName.getXmlAttribute();
        if (xmlAttributeValue == null) return PsiReference.EMPTY_ARRAY;

        return new PsiReference[]{
                new MetaRunnerParameterReference(parameter, xmlAttributeValue)
        };
      }
    });
  }

  private static class MetaRunnerParameterReference extends PsiReferenceBase<PsiElement> {
    @NotNull
    private final ParameterElement myParameter;

    private MetaRunnerParameterReference(@NotNull final ParameterElement parameter,
                                         @NotNull final XmlAttribute element) {
      super(element, false);
      myParameter = parameter;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
      final String myParameterName = myParameter.getParameterNameString();
      if (myParameterName == null) return null;
      if (myParameterName.equals("teamcity.step.mode")) return new TeamCityPredefined(myParameterName);


      final BuildRunnerElement runnerUsage = myParameter.getParentOfType(BuildRunnerElement.class, false);
      if (runnerUsage == null) return null;

      final MetaRunnerFile file = MetaRunnerReference.resolveReference(runnerUsage);
      if (file == null) return new TeamCityPredefined(myParameterName);

      for (DeclaredProperty property : file.getDeclaredParameters()) {
        final ParameterElement element = property.getParameterElement();
        final String parameterNameString = element.getParameterNameString();
        if (parameterNameString == null) continue;

        if (parameterNameString.equals(myParameterName)) {
          return new RenameableParameterElement(myParameter, property);
        }
      }

      return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      final List<LookupElement> result = new ArrayList<>(0);

      final String myParameterName = myParameter.getParameterNameString();
      if (myParameterName == null) return EMPTY_ARRAY;
      if (myParameterName.equals("teamcity.step.mode")) return EMPTY_ARRAY;


      final BuildRunnerElement runnerUsage = myParameter.getParentOfType(BuildRunnerElement.class, false);
      if (runnerUsage == null) return EMPTY_ARRAY;

      final MetaRunnerFile file = MetaRunnerReference.resolveReference(runnerUsage);
      if (file == null) return EMPTY_ARRAY;

      for (DeclaredProperty property : file.getDeclaredParameters()) {
        final ParameterElement element = property.getParameterElement();
        final String parameterNameString = element.getParameterNameString();
        if (parameterNameString == null) continue;

        final LookupElementBuilder builder = LookupElementBuilder.create(property, parameterNameString).withCaseSensitivity(false);
        result.add(builder);
      }

      return result.toArray();
    }
  }

  private static class MetaRunnerParameterTarget extends RenameableFakePsiElement implements PsiTarget, PomTargetPsiElement {
    @NotNull
    private final ParameterElement myElement;

    public MetaRunnerParameterTarget(@NotNull final ParameterElement element) {
      super(element.getXmlTag());
      myElement = element;
    }

    @NotNull
    @Override
    public PomTarget getTarget() {
      return new ManipulatableTarget(this);
    }

    @Override
    public String getName() {
      return myElement.getParameterNameString();
    }

    @Override
    public String getTypeName() {
      return "Meta Runner Parameter";
    }

    @Nullable
    @Override
    public Icon getIcon() {
      return null;
    }
  }
}
