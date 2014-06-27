package com.jonnyzzz.teamcity.renamer.resolve.buildTypes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.RenameableTeamCityFileElement;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuildTypeReference extends TeamCityFileReference<BuildTypeFile> {

  protected BuildTypeReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(attr, element);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    String value = myAttr.getValue();
    if (value == null)
      return null;

    TeamCityFile file = myAttr.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    BuildTypeFile buildType = file.findBuildTypeById(value);
    if (buildType == null)
      return null;
    XmlElement xmlElement = buildType.getXmlElement();
    if (xmlElement == null)
      return null;
    final PsiFile containingFile = xmlElement.getContainingFile();
    return new RenameableTeamCityFileElement(containingFile);
  }

  @Override
  protected Iterable<BuildTypeFile> getAll(@NotNull ProjectFile projectFile) {
    return projectFile.getAllBuildTypes();
  }
}
