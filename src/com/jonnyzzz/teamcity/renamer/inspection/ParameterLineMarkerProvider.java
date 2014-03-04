package com.jonnyzzz.teamcity.renamer.inspection;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.PsiNavigateUtil;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterLineMarkerProvider implements LineMarkerProvider {

  @Nullable
  @Override
  public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
    return null;
  }

  @Override
  public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    final List<ParameterElement> parameters = FluentIterable.from(elements)
            .transform(new Function<PsiElement, ParameterElement>() {
              @Override
              public ParameterElement apply(PsiElement element) {
                final Project project = element.getProject();
                final DomManager dom = DomManager.getDomManager(project);

                if (element instanceof XmlTag) {
                  final DomElement domElement = dom.getDomElement((XmlTag) element);

                  if (domElement instanceof ParameterElement) {
                    final ParameterElement parameterElement = (ParameterElement) domElement;

                    final XmlAttributeValue psiValue = parameterElement.getParameterName().getXmlAttributeValue();
                    if (psiValue != null) {
                      return parameterElement;
                    }
                  }
                }
                return null;
              }
            }).filter(Predicates.notNull())
            .toList();

    final Map<ParameterElement,ParameterElement> resolved = DeclaredProperties.findParametersOverride(parameters);
    for (Map.Entry<ParameterElement, ParameterElement> e : resolved.entrySet()) {
      final ParameterElement toHighlight = e.getKey();
      final XmlAttributeValue value = toHighlight.getParameterName().getXmlAttributeValue();
      if (value == null) continue;

      result.add(new OverriddenMarker(toHighlight, value, e.getValue()));
    }
  }

  private static class OverriddenMarker extends LineMarkerInfo<XmlAttributeValue> {
    private final String myParameterName;

    public OverriddenMarker(@NotNull final ParameterElement parameter,
                            @NotNull final XmlAttributeValue psiValue,
                            @NotNull final ParameterElement target) {
      super(psiValue,
              psiValue.getValueTextRange(),
              AllIcons.General.OverridingMethod,
              Pass.UPDATE_ALL,
              tooltip(target),
              navigation(target, psiValue),
              GutterIconRenderer.Alignment.LEFT);
      myParameterName = parameter.getParameterName().getStringValue();
    }

    private static GutterIconNavigationHandler<XmlAttributeValue> navigation(@NotNull final ParameterElement target,
                                                                             @NotNull final XmlAttributeValue navigation) {
      return new GutterIconNavigationHandler<XmlAttributeValue>() {
        @Override
        public void navigate(MouseEvent e, XmlAttributeValue elt) {
          PsiNavigateUtil.navigate(target.getParameterName().getXmlAttributeValue());
        }
      };
    }

    private static com.intellij.util.Function<? super XmlAttributeValue, String> tooltip(@NotNull final ParameterElement target) {
      return new com.intellij.util.Function<XmlAttributeValue, String>() {
        @Override
        public String fun(XmlAttributeValue xmlAttributeValue) {
          final TeamCityFile file = target.getParentOfType(TeamCityFile.class, false);
          if (file != null)
            return "Overrides parameter from " + file.getFilePresentableName();
          return "Overrides parameter from ???";
        }
      };
    }
  }
}
