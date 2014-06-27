package com.jonnyzzz.teamcity.renamer.resolve.buildTypes;

import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.ManipulatableTarget;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTarget;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TeamCityFileNamedReference extends RenameableFakePsiElement implements PsiTarget, PomTargetPsiElement {
  private final TeamCityFile myFile;

  public TeamCityFileNamedReference(@NotNull TeamCityFile file) {
    super(file.getXmlElement());
    myFile = file;
  }

  @Override
  public String getName() {
    return myFile.getFileName().getStringValue();
  }

  @Override
  public String getTypeName() {
    return myFile.getFileKind();
  }

  @NotNull
  @Override
  public XmlTag getNavigationElement() {
    final GenericDomValue<String> fileName = myFile.getFileName();
    if (fileName != null) {
      final XmlTag xmlElement = fileName.getXmlTag();
      if (xmlElement != null) {
        return xmlElement;
      }
    }

    return myFile.getXmlTag();
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return null;
  }

  @NotNull
  @Override
  public PomTarget getTarget() {
    return new ManipulatableTarget(this);
  }

  @NotNull
  public TeamCityElement getTeamCityContext() {
    return myFile;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    myFile.getFileName().setStringValue(name);
    return null;
  }
}
