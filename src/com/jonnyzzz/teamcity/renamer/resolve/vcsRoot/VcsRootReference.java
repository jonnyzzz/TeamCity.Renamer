package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import com.jonnyzzz.teamcity.renamer.resolve.RenameableTeamCityFileElement;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VcsRootReference extends TeamCityFileReference<VcsRootFile> {

  protected VcsRootReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(attr, element);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    String value = myAttr.getStringValue();
    if (value == null)
      return null;

    VcsRootFile f = VcsRoots.resolve(myAttr, value);
    if (f == null) return null;
    final XmlElement xmlElement = f.getXmlElement();
    if (xmlElement == null)
      return null;

    final PsiFile containingFile = xmlElement.getContainingFile();
    return new RenameableTeamCityFileElement(containingFile);
  }

  @Override
  protected Iterable<VcsRootFile> getAll(@NotNull ProjectFile projectFile) {
    return projectFile.getAllVcsRoots();
  }
}
