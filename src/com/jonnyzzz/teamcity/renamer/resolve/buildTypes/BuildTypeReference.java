package com.jonnyzzz.teamcity.renamer.resolve.buildTypes;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.vcsRoot.RenameableTeamCityFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuildTypeReference extends PsiReferenceBase<PsiElement> {

  private final GenericDomValue<String> myRef;

  public BuildTypeReference(@NotNull GenericDomValue<String> ref, @NotNull PsiElement element) {
    super(element);
    myRef = ref;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    String value = myRef.getValue();
    if (value == null)
      return null;

    TeamCityFile file = myRef.getParentOfType(TeamCityFile.class, false);
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

  @NotNull
  @Override
  public Object[] getVariants() {
    String value = myRef.getValue();
    if (value == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    TeamCityFile file = myRef.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    List<LookupElement> result = new ArrayList<>();
    for (BuildTypeFile f : projectFile.getAllBuildTypes()) {
      XmlElement xmlElement = f.getXmlElement();
      if (xmlElement == null)
        continue;

      PsiFile containingFile = xmlElement.getContainingFile();
      result.add(LookupElementBuilder.create(containingFile, FileUtil.getNameWithoutExtension(containingFile.getName()))
              .withTypeText(FileUtil.getNameWithoutExtension(containingFile.getName())));
    }
    return result.toArray();
  }
}
