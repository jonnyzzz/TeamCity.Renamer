package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VcsRootReference extends PsiReferenceBase<PsiElement> {
  private final GenericDomValue<String> myAttr;

  public VcsRootReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(element);
    myAttr = attr;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    String value = myAttr.getStringValue();
    if (value == null)
      return null;

    VcsRootFile f = VcsRoots.resolveVcsRoot(myAttr, value);
    if (f == null) return null;
    final XmlElement xmlElement = f.getXmlElement();
    if (xmlElement == null)
      return null;

    final PsiFile containingFile = xmlElement.getContainingFile();
    return new RenameableTeamCityFileElement(containingFile);
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    if (newElementName == null)
      return super.handleElementRename(null);

    if (newElementName.endsWith(".xml")) {
      String name = newElementName.substring(0, newElementName.length() - 4);
      return super.handleElementRename(name);
    }

    return super.handleElementRename(newElementName);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    TeamCityFile file = myAttr.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    String value = myAttr.getValue();
    if (value == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    List<LookupElement> result = new ArrayList<>();
    for (final VcsRootFile f : projectFile.getAllVcsRoots()) {
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
