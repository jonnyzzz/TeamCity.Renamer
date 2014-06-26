package com.jonnyzzz.teamcity.renamer.model.vcsRoot;

import com.jonnyzzz.teamcity.renamer.model.TeamCityFileDescriptionBase;

public class VcsRootDescription extends TeamCityFileDescriptionBase<VcsRootFile> {
  public VcsRootDescription() {
    super(VcsRootFile.class, "vcs-root");
  }
}
