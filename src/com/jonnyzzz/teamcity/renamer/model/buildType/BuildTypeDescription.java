package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeDescription extends DomFileDescription<BuildTypeFile> {
  public BuildTypeDescription() {
    super(BuildTypeFile.class, "build-type");
  }
}
