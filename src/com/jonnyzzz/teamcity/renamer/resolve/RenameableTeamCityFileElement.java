package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RenameableTeamCityFileElement extends RenameableFakePsiElement {
  private final PsiFile myContainingFile;

  public RenameableTeamCityFileElement(@NotNull final PsiFile containingFile) {
    super(containingFile);
    myContainingFile = containingFile;
  }

  @Override
  public String getName() {
    return FileUtil.getNameWithoutExtension(myContainingFile.getName());
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    myContainingFile.setName(name + ".xml");
    return this;
  }

  @Override
  public String getTypeName() {
    return "VcsRoot";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getPresentableText() {
    return getLocationString() + " :: " + getName();
  }

  @Nullable
  @Override
  public String getLocationString() {
    final TeamCityFile file =TeamCityFile.toTeamCityFile(TeamCityFile.class, myContainingFile);

    if (file == null) return super.getPresentableText();
    return file.getFilePresentableNameText();
  }

  @Override
  public boolean isEquivalentTo(PsiElement another) {
    if (another instanceof RenameableTeamCityFileElement) {
      return myContainingFile == ((RenameableTeamCityFileElement)another).myContainingFile;
    }
    return myContainingFile == another;
  }
}
