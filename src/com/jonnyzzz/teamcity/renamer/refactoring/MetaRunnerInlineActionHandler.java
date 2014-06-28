package com.jonnyzzz.teamcity.renamer.refactoring;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineActionHandler;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnersElement;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MetaRunnerInlineActionHandler extends InlineActionHandler {
  @Override
  public boolean isEnabledForLanguage(Language l) {
    return XMLLanguage.INSTANCE == l;
  }

  @Override
  public boolean canInlineElement(PsiElement element) {
    MetaRunnerFile metaRunnerFile = TeamCityFile.toTeamCityFile(MetaRunnerFile.class, element.getContainingFile());
    return metaRunnerFile != null;
  }

  @Override
  public void inlineElement(final Project project, Editor editor, PsiElement element) {
    final MetaRunnerFile metaRunnerFile = TeamCityFile.toTeamCityFile(MetaRunnerFile.class, element.getContainingFile());
    if (metaRunnerFile == null) {
      return;
    }

    final BuildRunnerElement buildRunnerElement = getBuildRunnerElement(editor);
    if (buildRunnerElement == null) {
      return;
    }

    Map<String, String> params = new HashMap<>();
    for (ParameterElement parameterElement : buildRunnerElement.getParametersBlock().getParameters()) {
      String name = parameterElement.getParameterNameString();
      String value = parameterElement.getParameterValue().getStringValue();
      params.put(name, value);
    }

    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
      @Override
      public void run() {
        FileDocumentManager.getInstance().saveAllDocuments();
        PsiDocumentManager.getInstance(project).commitAllDocuments();

        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
          @Override
          public void run() {
            BuildRunnersElement buildRunnersElement = buildRunnerElement.getParentOfType(BuildRunnersElement.class, false);
            if (buildRunnersElement == null) {
              return;
            }

            int i = buildRunnersElement.getRunners().indexOf(buildRunnerElement);
            if (i == -1) {
              return;
            }

            for (BuildRunnerElement runnerElement : metaRunnerFile.getSettings().getBuildRunners().getRunners()) {
              BuildRunnerElement newRunner = buildRunnersElement.addRunner(i++);
              newRunner.copyFrom(runnerElement);
              for (ParameterElement parameterElement : newRunner.getParametersBlock().getParameters()) {
                String oldValue = parameterElement.getParameterValue().getStringValue();
                String newValue = oldValue;
                parameterElement.getParameterValue().setStringValue(newValue);
              }

              buildRunnerElement.undefine();
            }

          }
        }, "Inline meta runner", "TeamCity");

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        FileDocumentManager.getInstance().saveAllDocuments();
      }
    });

    for (BuildRunnerElement runnerElement : metaRunnerFile.getSettings().getBuildRunners().getRunners()) {
      runnerElement.getXmlElement().getText();
    }

  }

  @Nullable
  private static BuildRunnerElement getBuildRunnerElement(Editor editor) {
    PsiReference reference = TargetElementUtilBase.findReference(editor);
    if (reference == null) {
      return null;
    }
    DomElement domElement = DomUtil.getDomElement(reference.getElement());
    if (domElement == null) {
      return null;
    }
    return domElement.getParentOfType(BuildRunnerElement.class, false);
  }
}
