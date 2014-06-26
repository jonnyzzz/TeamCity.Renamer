package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.collect.ImmutableSet;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class TeamCityFileDescriptionBase<T extends TeamCityFile> extends DomFileDescription<T> {
  public TeamCityFileDescriptionBase(@NotNull final Class<T> rootElementClass,
                                     @NotNull final @NonNls String rootTagName,
                                     @NotNull @NonNls final String... allPossibleRootTagNamespaces) {
    super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);
  }

  @Override
  public boolean hasStubs() {
    return true;
  }

  @Override
  public int getStubVersion() {
    return getClass().getName().hashCode() & 0xffffff + 42;
  }

  @NotNull
  @Override
  public Set<TeamCityFile> getDependencyItems(XmlFile file) {
    if (file == null) return Collections.emptySet();

    final TeamCityFile tc = TeamCityFile.toTeamCityFile(TeamCityFile.class, file);
    if (tc == null) return Collections.emptySet();

    return ImmutableSet.<TeamCityFile>copyOf(Visitors.getProjectFiles(tc.getParentProjectFile()));
  }

  @NotNull
  @Override
  public DomElement getResolveScope(GenericDomValue<?> reference) {
    return super.getResolveScope(reference);
  }

  @NotNull
  @Override
  public DomElement getIdentityScope(DomElement element) {
    return super.getIdentityScope(element);
  }
}
