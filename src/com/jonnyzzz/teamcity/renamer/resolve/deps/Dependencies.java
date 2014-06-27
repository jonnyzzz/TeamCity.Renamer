package com.jonnyzzz.teamcity.renamer.resolve.deps;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.*;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class Dependencies {

  @NotNull
  public static Iterable<BuildTypeFile> getDependencies(@NotNull final TeamCitySettingsBasedFile file) {
    final List<BuildTypeFile> items = new ArrayList<>();
    final Set<String> used = new HashSet<>();

    for (BuildTypeFile snapshotDependency : getAllSnapshotDependencies(file)) {
      if (!used.add(snapshotDependency.getFileId())) continue;
      items.add((snapshotDependency));
    }

    for (BuildTypeFile artifactDependency : getAllArtifactDependencies(file)) {
      if (!used.add(artifactDependency.getFileId())) continue;
      items.add((artifactDependency));
    }

    return items;
  }

  @NotNull
  public static Iterable<BuildTypeFile> getAllSnapshotDependencies(@NotNull final TeamCitySettingsBasedFile file) {
    final BuildTemplateFile baseTemplate = (file instanceof BuildTypeFile) ? ((BuildTypeFile) file).getBaseTemplate() : null;
    return Iterables.concat(file.getSnapshotDependencies(), baseTemplate != null ? baseTemplate.getSnapshotDependencies() : ImmutableList.<BuildTypeFile>of());
  }

  @NotNull
  public static Iterable<BuildTypeFile> getAllArtifactDependencies(@NotNull final TeamCitySettingsBasedFile file) {
    final BuildTemplateFile baseTemplate = (file instanceof BuildTypeFile) ? ((BuildTypeFile) file).getBaseTemplate() : null;
    return Iterables.concat(file.getArtifactDependencies(), baseTemplate != null ? baseTemplate.getArtifactDependencies() : ImmutableList.<BuildTypeFile>of());
  }

  @NotNull
  public static Iterable<TeamCitySettingsBasedFile> getDependingOnMe(@Nullable final TeamCitySettingsBasedFile file) {
    if (file == null) return ImmutableList.of();

    final String theId = file.getFileId();
    if (theId == null) return ImmutableList.of();

    return Iterables.filter(Visitors.getAllSettingsBasedFiles(file),
            new Predicate<TeamCitySettingsBasedFile>() {
              @Override
              public boolean apply(TeamCitySettingsBasedFile teamCitySettingsBasedFile) {
                return Iterables.contains(getDependencyIds(teamCitySettingsBasedFile), theId);
              }
            });
  }


  @NotNull
  public static Iterable<String> getDependencyIds(@Nullable final DomElement element) {
    if (element == null) return ImmutableList.of();

    final TeamCitySettingsBasedFile settings = element.getParentOfType(TeamCitySettingsBasedFile.class, false);
    if (settings == null) return ImmutableList.of();

    final SettingsElement el = settings.getSettingsElement();
    final SettingsElement baseEl = getBaseTemplateElement(el);

    return Iterables.concat(
            snapshotDependencyBuildTypeIds(el),
            snapshotDependencyBuildTypeIds(baseEl),
            artifactDependencyBuildTypeIds(el),
            artifactDependencyBuildTypeIds(baseEl)
    );
  }

  @Nullable
  private static SettingsElement getBaseTemplateElement(@Nullable final SettingsElement el) {
    if (el == null) return null;

    final BuildTypeFile buildType = el.getParentOfType(BuildTypeFile.class, false);
    if (buildType == null) return null;

    final BuildTemplateFile base = buildType.getBaseTemplate();
    if (base == null) return null;

    return base.getSettingsElement();
  }

  @NotNull
  private static Iterable<String> snapshotDependencyBuildTypeIds(@Nullable final SettingsElement el) {
    if (el == null) return ImmutableList.of();
    final SnapshotDependenciesElement ds = el.getSnapshotDependencies();
    if (ds == null) return ImmutableList.of();
    return FluentIterable.from(ds.getDependencies())
            .transform(new Function<SnapshotDependencyElement, String>() {
              @Override
              public String apply(SnapshotDependencyElement snapshotDependencyElement) {
                return snapshotDependencyElement.getSourceBuildTypeId().getStringValue();
              }
            })
            .filter(Predicates.notNull());
  }

  @NotNull
  private static Iterable<String> artifactDependencyBuildTypeIds(@Nullable final SettingsElement el) {
    if (el == null) return ImmutableList.of();
    final ArtifactDependenciesElement ds = el.getArtifactDependencies();
    if (ds == null) return ImmutableList.of();
    return FluentIterable.from(ds.getDependencies())
            .transform(new Function<ArtifactDependencyElement, String>() {
              @Override
              public String apply(ArtifactDependencyElement artifactDependenciesElement) {
                return artifactDependenciesElement.getSourceBuildTypeId().getStringValue();
              }
            })
            .filter(Predicates.notNull());
  }
}
