package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.collect.Iterables;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixProvider;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.*;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityPredefined;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import com.jonnyzzz.teamcity.renamer.resolve.deps.Dependencies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterReference extends PsiReferenceBase<PsiElement> implements LocalQuickFixProvider {
  private static final Pattern BUILT_IN_PARAMETER_PATTERN = Pattern.compile("vcsroot\\..*|env\\..*|teamcity\\.tool\\..*|system\\.agent\\..*");
  public static final String DEPENDENCY_PREFIX = "dep.";

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

  @NotNull
  public DomElement getContainingDomElement() {
    return myAttr;
  }

  @NotNull
  public TextRange getReferenceTextRange() {
    return getRangeInElement().shiftRight(getElement().getTextOffset());
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    final PsiElement declaredResolve = resolvePropertyFromContext(myAttr, myReferredVariableName);
    if (declaredResolve != null) return declaredResolve;

    final PsiElement depResolve = resolveDepParameter();
    if (depResolve != null) return depResolve;

    if (checkIfBuiltInParameter()) {
      return new TeamCityPredefined(myReferredVariableName);
    }

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
    if (!isDependencyParameter(myReferredVariableName)) return null;

    final TeamCitySettingsBasedFile file = myAttr.getParentOfType(TeamCitySettingsBasedFile.class, false);
    if (file == null) return null;

    if (!Dependencies.getDependencyIds(myAttr).iterator().hasNext()) return null;

    final int dot2 = myReferredVariableName.indexOf('.', DEPENDENCY_PREFIX.length());
    if (dot2 <= 0 || dot2 + 1 >= myReferredVariableName.length()) return null;

    final String buildTypeId = myReferredVariableName.substring("dep.".length(), dot2);
    final BuildTypeFile buildType = Visitors.findBuildType(myAttr, buildTypeId);
    if (buildType == null) return null;

    if (!Iterables.contains(Dependencies.getDependencyIds(myAttr), buildTypeId)) return null;
    return resolvePropertyFromContext(buildType, myReferredVariableName.substring(dot2+1));
  }

  private static boolean isDependencyParameter(@NotNull final String name) {
    return name.startsWith(DEPENDENCY_PREFIX);
  }

  @Nullable
  private static String getDependencyId(@NotNull final String name) {
    if (isDependencyParameter(name)) {
      final int dot2 = name.indexOf('.', DEPENDENCY_PREFIX.length());
      if (dot2 > 0 && dot2 + 1 < name.length()) {
        return name.substring(DEPENDENCY_PREFIX.length(), dot2);
      }
    }
    return null;
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
    final List<LookupElement> result = new ArrayList<>(0);
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
    if (isDependencyParameter(myReferredVariableName)) {
      final int dot2 = myReferredVariableName.indexOf('.', DEPENDENCY_PREFIX.length());
      if (dot2 > 0 && dot2 + 1 < myReferredVariableName.length()) {
        final String buildTypeId = myReferredVariableName.substring(DEPENDENCY_PREFIX.length(), dot2);

        return super.handleElementRename(DEPENDENCY_PREFIX + buildTypeId + "." + newElementName);
      }
    }
    return super.handleElementRename(newElementName);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return super.isReferenceTo(element);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element)  throws IncorrectOperationException {
    return super.bindToElement(element);
  }

  @Nullable
  @Override
  public LocalQuickFix[] getQuickFixes() {
    if (resolve() == null) {
      return getLocalQuickFixesForParameter(myAttr.getParentOfType(ParameterElement.class, false), myReferredVariableName);
    }
    return null;
  }

  @Nullable
  public static LocalQuickFix[] getLocalQuickFixesForParameter(@Nullable final ParameterElement parameter, @NotNull final String name) {
    if (parameter == null) {
      return null;
    }
    final List<LocalQuickFix> fixes = new ArrayList<>();

    final BuildTypeFile buildTypeFile = parameter.getParentOfType(BuildTypeFile.class, false);
    final BuildTemplateFile buildTemplateFile = parameter.getParentOfType(BuildTemplateFile.class, false);

    if (isDependencyParameter(name)) {
      if ((buildTypeFile != null || buildTemplateFile != null)) {
        final String depId = getDependencyId(name);
        final BuildTypeFile bt = Visitors.findBuildType(parameter, depId);
        if (bt != null) {
          if (!Iterables.contains(Dependencies.getDependencyIds(parameter), depId)) {
            fixes.add(new AddDependencyQuickFix(depId));
          } else {
            // Dependency already exists. Should create parameter in dependency.
            String inDependencyName = name.substring(DEPENDENCY_PREFIX.length() + depId.length() + 1 );

            fixes.add(new DefineSettingsParameter(inDependencyName, bt.getSettingsElement(), "Create dependency parameter"));
            // TODO: Not sure about that:
//            final BuildTemplateFile template = bt.getBaseTemplate();
//            if (template != null) {
//              fixes.add(new DefineBuildTemplateParameter(inDependencyName, template));
//            }
//            fixes.add(new DefineProjectParameter(inDependencyName, bt.getParentProjectFile()));
          }
        }
      }
    } else  {
      TeamCityFile teamCityFile = parameter.getParentOfType(TeamCityFile.class, false);
      if (teamCityFile != null) {
        SettingsElement settingsElement = parameter.getParentOfType(SettingsElement.class, false);
        ProjectFile parentProjectFile = teamCityFile.getParentProjectFile();

        if (settingsElement != null) {
          fixes.add(new DefineSettingsParameter(name, settingsElement, String.format("Create %s parameter", teamCityFile.getFileKind())));
        }
        if (buildTypeFile != null) {
          BuildTemplateFile baseTemplate = buildTypeFile.getBaseTemplate();
          if (baseTemplate != null) {
            fixes.add(new DefineSettingsParameter(name, baseTemplate.getSettings(), "Create base template parameter"));
          }
        }
        if (teamCityFile instanceof ProjectFile) {
          fixes.add(new DefineProjectParameter(name, parentProjectFile, "Create project parameter"));
        }
        if (parentProjectFile != null) {
          fixes.add(new DefineProjectParameter(name, parentProjectFile, "Create parent project parameter"));
        }
      }
    }
    return fixes.toArray(new LocalQuickFix[fixes.size()]);
  }

  @NotNull
  public static XmlTag addParameter(@NotNull final ParametersBlockElement parameters, @NotNull final String name, @NotNull final String value) {
    return ApplicationManager.getApplication().runWriteAction(new Computable<XmlTag>() {
      @Override
      public XmlTag compute() {
        final XmlTag parametersXmlTag = parameters.getXmlTag();
        final XmlTag child = parametersXmlTag.createChildTag("param", parametersXmlTag.getNamespace(), null, false);
        child.setAttribute("name", name);
        child.setAttribute("value", value);
        return parametersXmlTag.addSubTag(child, false);
      }
    });
  }

  private static class AddDependencyQuickFix implements LocalQuickFix {
    private final String myDepId;

    public AddDependencyQuickFix(String depId) {
      myDepId = depId;
    }

    @NotNull
    @Override
    public String getName() {
      return "Add dependency";
    }

    @NotNull
    @Override
    public String getFamilyName() {
      return "TeamCity Renamer";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor) {
      final PsiElement psiElement = problemDescriptor.getPsiElement();
      final DomElement element = TeamCityFile.findContainingDomElement(psiElement);
      if (element == null) {
        throw new IllegalStateException("PsiElement '" + psiElement + "' should have DomElement");
      }
      final BuildTypeFile type = element.getParentOfType(BuildTypeFile.class, false);
      final BuildTemplateFile template = element.getParentOfType(BuildTemplateFile.class, false);
      if (type != null) {
        final SnapshotDependencyElement dependency = type.getSettings().getSnapshotDependencies().addSnapshotDependencyElement();
        dependency.getSourceBuildTypeId().setStringValue(myDepId);
      } else if (template != null) {
        final SnapshotDependencyElement dependency = template.getSettings().getSnapshotDependencies().addSnapshotDependencyElement();
        dependency.getSourceBuildTypeId().setStringValue(myDepId);
      }
    }
  }
}
