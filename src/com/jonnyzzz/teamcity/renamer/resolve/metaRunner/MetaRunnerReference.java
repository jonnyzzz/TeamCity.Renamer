package com.jonnyzzz.teamcity.renamer.resolve.metaRunner;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MetaRunnerReference extends TeamCityFileReference<MetaRunnerFile> {

  private static final String[] BUILTIN_RUNNERS = {
          "Ant",
          "Duplicator",
          "FxCop",
          "Inspection",
          "Ipr",
          "JPS",
          "MSBuild",
          "Maven2",
          "NAnt",
          "NUnit",
          "VS.Solution",
          "dotnet-dupfinder",
          "dotnet-tools-dupfinder",
          "dotnet-tools-inspectcode",
          "ftp-deploy-runner",
          "gradle-runner",
          "jb.nuget.installer",
          "jb.nuget.pack",
          "jb.nuget.publish",
          "jetbrains.dotNetGenericRunner",
          "jetbrains_powershell",
          "jonnyzzz.grunt",
          "jonnyzzz.npm",
          "jonnyzzz.nvm",
          "jonnyzzz.vm",
          "python",
          "rake-runner",
          "simpleRunner",
          "ssh-deploy-runner",
          "ssh-exec-runner"};
  private static final Set<String> BUILTIN_RUNNERS_SET = new HashSet<>();
  static {
    Collections.addAll(BUILTIN_RUNNERS_SET, BUILTIN_RUNNERS);

  }

  protected MetaRunnerReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(attr, element);
  }

  @Override
  protected Iterable<MetaRunnerFile> getAll(@NotNull ProjectFile projectFile) {
    return projectFile.getAllMetaRunners();
  }

  @Override
  protected boolean isBuiltIn() {
    return BUILTIN_RUNNERS_SET.contains(myAttr.getValue());
  }
}
