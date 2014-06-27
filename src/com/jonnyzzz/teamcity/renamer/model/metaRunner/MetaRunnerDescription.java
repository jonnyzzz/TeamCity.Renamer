package com.jonnyzzz.teamcity.renamer.model.metaRunner;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ivan Chirkov
 */
public class MetaRunnerDescription extends DomFileDescription<MetaRunnerFile> {
  public MetaRunnerDescription() {
    super(MetaRunnerFile.class, "meta-runner", "build-type", "template");
  }

  @Override
  public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
    return super.isMyFile(file, module) && file.getParent() != null && "metaRunners".equals(file.getParent().getName());
  }
}
