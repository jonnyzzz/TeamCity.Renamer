package com.jonnyzzz.teamcity.renamer.resolve.deps;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.*;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class Dependencies {
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
