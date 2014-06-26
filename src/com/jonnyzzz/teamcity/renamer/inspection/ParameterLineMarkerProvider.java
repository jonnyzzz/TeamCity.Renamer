package com.jonnyzzz.teamcity.renamer.inspection;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.awt.RelativePoint;
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
  //TODO: use com.intellij.lang.annotation.Annotator


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

    final Multimap<ParameterElement, ParameterElement> resolved = DeclaredProperties.findOverriddenParametersFromParents(parameters);
    final Multimap<ParameterElement, ParameterElement> children = DeclaredProperties.findOverriddenByChildrenParameters(parameters);

    for (Map.Entry<ParameterElement, Collection<ParameterElement>> e : resolved.asMap().entrySet()) {
      final ParameterElement toHighlight = e.getKey();
      final XmlAttributeValue value = toHighlight.getParameterName().getXmlAttributeValue();
      if (value == null) continue;

      result.add(new OverriddenUpMarker(toHighlight, value, e.getValue()));
    }

    for (Map.Entry<ParameterElement, Collection<ParameterElement>> e : children.asMap().entrySet()) {
      final ParameterElement toHighlight = e.getKey();
      final XmlAttributeValue value = toHighlight.getParameterName().getXmlAttributeValue();
      if (value == null) continue;

      result.add(new OverriddenDownMarker(toHighlight, value, e.getValue()));
    }
  }

  private static class OverriddenUpMarker extends LineMarkerInfo<XmlAttributeValue> {
    public OverriddenUpMarker(@NotNull final ParameterElement parameter,
                              @NotNull final XmlAttributeValue psiValue,
                              @NotNull final Collection<ParameterElement> target) {
      super(psiValue,
              psiValue.getValueTextRange(),
              AllIcons.Gutter.OverridingMethod,
              Pass.UPDATE_ALL,
              tooltip("Overrides from ", target),
              navigation(parameter.getParameterNameString(), target),
              GutterIconRenderer.Alignment.LEFT);
    }
  }

  private static class OverriddenDownMarker extends LineMarkerInfo<XmlAttributeValue> {
    public OverriddenDownMarker(@NotNull final ParameterElement parameter,
                                @NotNull final XmlAttributeValue psiValue,
                                @NotNull final Collection<ParameterElement> targets) {
      super(psiValue,
              psiValue.getValueTextRange(),
              AllIcons.Gutter.OverridenMethod,
              Pass.UPDATE_ALL,
              tooltip("Overridden in ", targets),
              navigation(parameter.getParameterNameString(), targets),
              GutterIconRenderer.Alignment.LEFT);
    }
  }

  private static GutterIconNavigationHandler<XmlAttributeValue> navigation(@Nullable final String parameterName,
                                                                           @NotNull final Collection<ParameterElement> target) {
    if (parameterName == null || target.isEmpty()) return null;
    return new GutterIconNavigationHandler<XmlAttributeValue>() {
      @Override
      public void navigate(MouseEvent e, XmlAttributeValue elt) {
        if (target.isEmpty()) return;

        if (target.size() == 1) {
          PsiNavigateUtil.navigate(target.iterator().next().getParameterName().getXmlAttributeValue());
          return;
        }

        if (e == null) return;

        final XmlAttributeValue[] elements = FluentIterable.from(target)
                .transform(TO_PSI_ELEMENT)
                .filter(Predicates.notNull())
                .toArray(XmlAttributeValue.class);

        final PsiElementListCellRenderer<PsiElement> render = new DefaultPsiElementCellRenderer(){
          @Override
          public String getContainerText(PsiElement element, String name) {
            final ParameterElement param = ParameterElement.fromPsiElement(element);
            if (param == null) return super.getContainerText(element, name);

            final TeamCityFile file = param.getParentOfType(TeamCityFile.class, false);
            if (file == null) return super.getContainerText(element, name);

            return file.getFilePresentableNameText();
          }

          @Override
          public String getElementText(PsiElement element) {
            return super.getElementText(element);
          }
        };
        NavigationUtil
                .getPsiElementPopup(elements, render, "Select parameter to navigate\u2026")
                .show(new RelativePoint(e));
      }
    };
  }

  private static final Function<ParameterElement, XmlAttributeValue> TO_PSI_ELEMENT = new Function<ParameterElement, XmlAttributeValue>() {
    @Override
    public XmlAttributeValue apply(ParameterElement parameterElement) {
      return parameterElement.getParameterName().getXmlAttributeValue();
    }
  };


  private static com.intellij.util.Function<? super XmlAttributeValue, String> tooltip(@NotNull final String prefix,
                                                                                       @NotNull final Collection<ParameterElement> target) {
    return new com.intellij.util.Function<XmlAttributeValue, String>() {
      @Override
      public String fun(XmlAttributeValue xmlAttributeValue) {
        return prefix + Joiner.on(", ").join(FluentIterable.from(target).transform(new Function<ParameterElement, Object>() {
          @Override
          public Object apply(ParameterElement parameterElement) {
            final TeamCityFile file = parameterElement.getParentOfType(TeamCityFile.class, false);
            if (file == null) return null;
            return file.getFilePresentableNameHTML();
          }
        }).filter(Predicates.notNull()));
      }
    };
  }
}
