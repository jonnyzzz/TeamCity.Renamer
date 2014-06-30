package com.jonnyzzz.teamcity.renamer.resolve;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Chirkov
 */
public abstract class TeamCityFileReference<T extends TeamCityFile> extends PsiReferenceBase<PsiElement> {
  protected final GenericDomValue<String> myAttr;

  protected TeamCityFileReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(element);
    myAttr = attr;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    TeamCityFile file = myAttr.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return null;

    String value = myAttr.getValue();
    if (value == null)
      return null;

    for (final T f : getAll(projectFile)) {
      if (value.equals(f.getFileId())) {
        final XmlElement xmlElement = f.getXmlElement();
        if (xmlElement == null)
          continue;

        final PsiFile containingFile = xmlElement.getContainingFile();
        return new RenameableTeamCityFileElement(containingFile);
      }
    }

    if (isBuiltIn()) {
      return new TeamCityPredefined(myElement.getProject(), value);
    }

    return null;
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
    String value = myAttr.getValue();
    if (value == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    TeamCityFile file = myAttr.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return ArrayUtil.EMPTY_OBJECT_ARRAY;

    List<LookupElement> result = new ArrayList<>();
    for (T f : getAll(projectFile)) {
      XmlElement xmlElement = f.getXmlElement();
      if (xmlElement == null)
        continue;

      PsiFile containingFile = xmlElement.getContainingFile();
      result.add(LookupElementBuilder.create(containingFile, FileUtil.getNameWithoutExtension(containingFile.getName()))
              .withTypeText(FileUtil.getNameWithoutExtension(containingFile.getName())));
    }
    return result.toArray();
  }

  protected abstract Iterable<T> getAll(@NotNull ProjectFile projectFile);

  protected boolean isBuiltIn() {
    return false;
  }
}
