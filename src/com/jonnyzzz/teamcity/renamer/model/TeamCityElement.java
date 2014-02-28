package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public abstract class TeamCityElement implements DomElement {
  @Nullable
  protected PsiFile getContainingFile() {
    final XmlElement xmlElement = getXmlElement();
    if (xmlElement == null) return null;

    return xmlElement.getContainingFile();
  }

  @Nullable
  protected PsiDirectory getContainingDirectory() {
    final PsiFile containingFile = getContainingFile();
    if (containingFile == null) return null;

    final PsiDirectory containingDirectory = containingFile.getContainingDirectory();
    if (containingDirectory == null) return null;
    return containingDirectory;
  }

  public String toString() {
    final XmlTag tag = getXmlTag();
    if (tag == null) {
      return super.toString();
    }
    final String name = tag.getName();
    if ("".equals(name)) {
      return super.toString();
    }
    return name;
  }

}
