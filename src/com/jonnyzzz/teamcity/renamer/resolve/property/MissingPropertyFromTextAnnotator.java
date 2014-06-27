package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public class MissingPropertyFromTextAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!isMatches(element)) {
      return;
    }

    final PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders(element);
    for (PsiReference reference : references) {
      if (!(reference instanceof ParameterReference)) continue;
      final PsiElement resolve = reference.resolve();
      if (resolve != null) continue;
      final Annotation annotation = holder.createErrorAnnotation(element, "Missing property");
      final LocalQuickFix[] fixes = ((ParameterReference) reference).getQuickFixes();
      if (fixes == null) continue;
      for (LocalQuickFix fix : fixes) {
        final InspectionManager inspectionManagerBase = InspectionManager.getInstance(element.getProject());
        final ProblemDescriptor descriptor = inspectionManagerBase.createProblemDescriptor(reference.getElement(), "Ololo", fix, ProblemHighlightType.ERROR, true);
        annotation.registerFix(fix, null, null, descriptor);
      }
    }
  }

  private boolean isMatches(PsiElement element) {
    for (PsiElementPattern.Capture pattern : PropertyFromTextContributor.PATTERNS) {
      if (pattern.accepts(element)) {
        return true;
      }
    }
    return false;
  }
}
