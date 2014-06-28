package com.jonnyzzz.teamcity.renamer.resolve.metaRunner;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.RenameableTeamCityFileElement;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityFileReference;
import com.jonnyzzz.teamcity.renamer.resolve.TeamCityPredefined;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @Nullable
  public static MetaRunnerFile resolveReference(@Nullable final BuildRunnerElement runner) {
    if (runner == null) return null;

    final XmlElement xmlElement = runner.getXmlElement();
    if (xmlElement == null) return null;

    PsiElement resolve = new MetaRunnerReference(runner.getBuildRunnerType(), xmlElement).resolve();
    if (resolve == null) return null;
    if (resolve instanceof TeamCityPredefined) return null;

    if (!(resolve instanceof RenameableTeamCityFileElement)) return null;

    return TeamCityFile.toTeamCityFile(MetaRunnerFile.class, resolve.getContainingFile());
  }

  @Override
  protected boolean isBuiltIn() {
    return BUILTIN_RUNNERS_SET.contains(myAttr.getValue());
  }
}
