package com.jonnyzzz.teamcity.renamer.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
* @author Ivan Chirkov
*/
public class TeamCityPredefined extends FakePsiElement {
  private final String myName;

  public TeamCityPredefined(@NotNull final String name) {
    myName = name;
  }

  @Override
  public boolean isWritable() {
    return false;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public PsiElement getParent() {
    return null;
  }

  @Override
  public String getPresentableText() {
    return getLocationString() + " :: " + getName();
  }

  @Nullable
  @Override
  public String getLocationString() {
    return "[TeamCity Predefined]";
  }

  @Override
  public boolean canNavigate() {
    System.out.println("canNavigate");
    return false;
  }
}
