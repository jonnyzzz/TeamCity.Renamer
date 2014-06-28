package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityPredefined;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReferenceConverter;
import com.jonnyzzz.teamcity.renamer.resolve.property.RenameableParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.generate.tostring.util.StringUtil;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParameterElement extends TeamCityElement implements AutoFoldableElement {

  @Required
  @NameValue
  @Attribute("name")
  public abstract GenericAttributeValue<String> getParameterName();

  @Attribute("value")
  @Referencing(value = ParameterReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getParameterValue();

  @TagValue
  public abstract String getTagValue();

  @TagValue
  public abstract void setTagValue(String s);

  @Nullable
  public String getStringValue() {
    String value = getParameterValue().getValue();
    if (value == null) {
      value = getTagValue();
    }
    return value;
  }

  @Nullable
  public String getParameterNameString() {
    final String name = getParameterName().getStringValue();
    if (name == null || StringUtil.isEmpty(name)) return null;
    return name.trim();
  }

  @Nullable
  public final RenameableParameterElement toRenameableReference(@NotNull final DomElement requestor) {
    final DeclaredProperty apply = DeclaredProperty.FROM_PARAMETER_ELEMENT.apply(this);
    if (apply == null) return null;

    return new RenameableParameterElement(requestor, apply);
  }


  @Nullable
  public static ParameterElement fromPsiElement(@Nullable final PsiElement element) {
    if (element == null) return null;
    if (element instanceof TeamCityPredefined) return null;

    if (element instanceof PomTargetPsiElement) {
      final PomTarget target = ((PomTargetPsiElement) element).getTarget();
      if (target instanceof DomTarget) {
        final DomElement domElement = ((DomTarget) target).getDomElement();
        if (domElement != null) {
          return domElement.getParentOfType(ParameterElement.class, false);
        }
      }
    }

    final DomManager dom = DomManager.getDomManager(element.getProject());
    final XmlAttribute attr = PsiTreeUtil.getParentOfType(element, XmlAttribute.class, false);
    if (attr != null) {
      final GenericAttributeValue el = dom.getDomElement(attr);
      if (el != null) {
        return el.getParentOfType(ParameterElement.class, false);
      }
    }

    final XmlTag tag = PsiTreeUtil.getParentOfType(element, XmlTag.class, false);
    if (tag != null) {
      final DomElement el = dom.getDomElement(tag);
      if (el instanceof ParameterElement) {
        return (ParameterElement) el;
      }
    }

    return null;
  }

  public void setParameterName(@NotNull String newName) {
    getParameterName().setStringValue(newName);
  }

  @Nullable
  @Override
  public String getFoldedText() {
    final XmlTag xmlTag = getXmlTag();
    if (xmlTag == null) return null;
    if (xmlTag.isEmpty()) return null;

    final String name = getParameterNameString();
    if (name == null) return null;

    return "name=\"" + name + "\" value=...";
  }
}
