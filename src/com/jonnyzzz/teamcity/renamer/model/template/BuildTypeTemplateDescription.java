package com.jonnyzzz.teamcity.renamer.model.template;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeTemplateDescription extends TeamCityFileDescriptionBase<BuildTemplateFile> {
  public BuildTypeTemplateDescription() {
    super(BuildTemplateFile.class, "template");
  }

  @Override
  public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
    return super.isMyFile(file, module) && file.getParent() != null && "buildTypes".equals(file.getParent().getName());
  }
}
