package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VcsRootReference extends PsiReferenceBase<PsiElement> {
  private final GenericDomValue<String> myAttr;
  private final String myRootId;

  public VcsRootReference(@NotNull GenericDomValue<String> attr,
                          @NotNull PsiElement element,
                          @NotNull String rootId) {
    super(element);
    myAttr = attr;
    myRootId = rootId;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    TeamCityFile tcFile = myAttr.getParentOfType(TeamCityFile.class, false);
    for (VcsRootFile f : tcFile.getParentProjectFile().getAllVcsRoots()) {
      if (myRootId.equals(f.getFileId()))
        return f.getXmlElement().getContainingFile();
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
