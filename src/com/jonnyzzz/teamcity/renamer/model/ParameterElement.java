package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.resolve.property.ParameterReferenceConverter;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.generate.tostring.util.StringUtil;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParameterElement extends TeamCityElement {

  @Required
  @NameValue
  @Attribute("name")
  public abstract GenericAttributeValue<String> getParameterName();

  @Attribute("value")
  @Convert(value = ParameterReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getParameterValue();


  @Nullable
  public String getParameterNameString() {
    final String name = getParameterName().getStringValue();
    if (name == null || StringUtil.isEmpty(name)) return null;
    return name.trim();
  }


  @Nullable
  public static ParameterElement fromPsiElement(@Nullable final PsiElement element) {
    if (element == null) return null;

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
}
