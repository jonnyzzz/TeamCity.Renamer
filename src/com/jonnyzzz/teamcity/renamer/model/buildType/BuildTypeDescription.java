package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.psi.xml.XmlFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeDescription extends TeamCityFileDescriptionBase<BuildTypeFile> {
  public BuildTypeDescription() {
    super(BuildTypeFile.class, "build-type");
  }

  @NotNull
  @Override
  public Set<TeamCityFile> getDependencyItems(XmlFile file) {
    final Set<TeamCityFile> base = super.getDependencyItems(file);

    final BuildTypeFile bt = TeamCityFile.toTeamCityFile(BuildTypeFile.class, file);

    if (bt == null) return base;

    final BuildTemplateFile baseTemplate = bt.getBaseTemplate();

    if (baseTemplate == null) return base;
    return Sets.union(ImmutableSet.of(baseTemplate), base);
  }
}
