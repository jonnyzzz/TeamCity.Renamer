package com.jonnyzzz.teamcity.renamer.model.vcsRoot;

import com.intellij.util.xml.DomFileDescription;

public class VcsRootDescription extends DomFileDescription<VcsRootFile> {
  public VcsRootDescription() {
    super(VcsRootFile.class, "vcs-root");
  }
}
