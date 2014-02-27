package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.DomFileDescription;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeDescription extends DomFileDescription<BuildTypeFile> {
  public BuildTypeDescription() {
    super(BuildTypeFile.class, "build-type");
  }
}
