package com.jonnyzzz.teamcity.renamer.model.metaRunner;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Ivan Chirkov
 */
public class MetaRunnerDescription extends DomFileDescription<MetaRunnerFile> {
  public MetaRunnerDescription() {
    super(MetaRunnerFile.class, "meta-runner");
  }
}
