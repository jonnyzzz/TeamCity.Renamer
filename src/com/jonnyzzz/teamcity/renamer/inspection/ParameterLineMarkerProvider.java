package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterLineMarkerProvider implements LineMarkerProvider {

  @Nullable
  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
    final Project project = element.getProject();
    final DomManager dom = DomManager.getDomManager(project);

    if (element instanceof XmlTag) {
      final DomElement domElement = dom.getDomElement((XmlTag) element);

      if (domElement instanceof ParameterElement) {
        final ParameterElement parameterElement = (ParameterElement) domElement;

        final XmlAttributeValue psiValue = parameterElement.getParameterName().getXmlAttributeValue();
        if (psiValue != null) {
          return new ParameterMergeableLineMarkerInfo(parameterElement, psiValue);
        }
      }
    }

    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
  }

  private static class ParameterMergeableLineMarkerInfo extends LineMarkerInfo<XmlAttributeValue> {
    private final String myParameterName;

    public ParameterMergeableLineMarkerInfo(@NotNull final ParameterElement parameter, @NotNull final XmlAttributeValue psiValue) {
      super(psiValue, psiValue.getValueTextRange(), AllIcons.General.OverridingMethod, Pass.UPDATE_ALL, null, null, GutterIconRenderer.Alignment.LEFT);
      myParameterName = parameter.getParameterName().getStringValue();
    }
  }
}
