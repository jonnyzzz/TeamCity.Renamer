package com.jonnyzzz.teamcity.renamer.model;

import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import org.jetbrains.annotations.Nullable;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*/
public abstract class CleanupElement extends TeamCityElement implements AutoFoldableElement {
  @Nullable
  @Override
  public String getFoldedText() {
    return "";
  }
}
