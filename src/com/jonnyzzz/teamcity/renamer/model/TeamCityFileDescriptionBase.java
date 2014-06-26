package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TeamCityFileDescriptionBase<T extends TeamCityFile> extends DomFileDescription<T> {
  public TeamCityFileDescriptionBase(@NotNull final Class<T> rootElementClass,
                                     @NotNull final @NonNls String rootTagName,
                                     @NotNull @NonNls final String... allPossibleRootTagNamespaces) {
    super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);
  }
}
