package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.collect.Iterables;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnersElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class SettingsElement extends TeamCityElement {

  @Stubbed
  @SubTag("vcs-settings")
  public abstract VcsSettingsElement getVcsSettings();

  @Stubbed
  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

  @SubTag("dependencies")
  public abstract SnapshotDependenciesElement getSnapshotDependencies();

  @SubTag("artifact-dependencies")
  public abstract ArtifactDependenciesElement getArtifactDependencies();

  @SubTag("cleanup")
  public abstract CleanupElement getCleanupElement();

  @SubTag("build-extensions")
  public abstract BuildExtensions getExtensions();

  @SubTag("build-triggers")
  public abstract BuildTriggers getTriggers();

  @Stubbed
  @SubTag("build-runners")
  public abstract BuildRunnersElement getBuildRunners();

  @NotNull
  public final Iterable<? extends DependencyElement> getOwnDependencies() {
    return Iterables.concat(getSnapshotDependencies().getDependencies(), getArtifactDependencies().getDependencies());
  }
}
