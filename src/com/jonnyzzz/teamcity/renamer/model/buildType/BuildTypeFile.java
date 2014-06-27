package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.model.ArtifactDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.SettingsElement;
import com.jonnyzzz.teamcity.renamer.model.SnapshotDependencyElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.settings.DeclaredTemplate;
import com.jonnyzzz.teamcity.renamer.resolve.settings.DeclaredTemplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class BuildTypeFile extends TeamCitySettingsBasedFile {

  @NotNull
  @Override
  protected String getFileKind() {
    return "build configuration";
  }

  @Required
  @SubTag("settings")
  public abstract BuildTypeSettingsElement getSettings();

  @NotNull
  @Override
  public final SettingsElement getSettingsElement() {
    return getSettings();
  }

  @NotNull
  public final List<BuildTypeFile> getSnapshotDependencies() {
    List<BuildTypeFile> result = new ArrayList<>();
    for (SnapshotDependencyElement dep : getSettings().getSnapshotDependencies().getDependencies()) {
      String btId = dep.getSourceBuildTypeId().getValue();
      if (btId == null)
        continue;
      BuildTypeFile bt = findBuildTypeById(btId);
      if (bt == null)
        continue;
      result.add(bt);
    }
    return result;
  }

  @NotNull
  public final List<BuildTypeFile> getArtifactDependencies() {
    List<BuildTypeFile> result = new ArrayList<>();
    for (ArtifactDependencyElement dep : getSettings().getArtifactDependencies().getDependencies()) {
      String btId = dep.getSourceBuildTypeId().getValue();
      if (btId == null)
        continue;
      BuildTypeFile bt = findBuildTypeById(btId);
      if (bt == null)
        continue;
      result.add(bt);
    }
    return result;
  }

  @Nullable
  public final BuildTemplateFile getBaseTemplate() {
    final GenericAttributeValue<String> baseTemplateElement = getSettings().getBaseTemplate();
    final String templateId = baseTemplateElement.getStringValue();

    final DeclaredTemplate resolved = DeclaredTemplates.resolve(this, templateId);
    if (resolved == null) return null;

    return resolved.getFile();
  }
}
