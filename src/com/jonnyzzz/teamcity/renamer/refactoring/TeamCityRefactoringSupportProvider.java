package com.jonnyzzz.teamcity.renamer.refactoring;

import com.google.common.collect.Iterables;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.PsiNavigateUtil;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnerElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildRunnersElement;
import com.jonnyzzz.teamcity.renamer.model.metaRunner.MetaRunnerFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TeamCityRefactoringSupportProvider extends RefactoringSupportProvider {

  @Nullable
  @Override
  public RefactoringActionHandler getPullUpHandler() {
    return new RefactoringActionHandler() {
      @Override
      public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        final PsiElement startElement = file.findElementAt(editor.getCaretModel().getOffset());
        final ParameterElement parameterElement = ParameterElement.fromPsiElement(startElement);
        if (parameterElement == null) return;
        if (!parameterElement.isFromSettings()) return;

        if (startElement == null) return;
        final TeamCityFile theFile = TeamCityFile.toTeamCityFile(TeamCityFile.class, file);
        if (theFile == null) return;

        final ProjectFile parent = theFile.getParentProjectFile();
        if (parent == null) return;

        if (Iterables.contains(parent.getDeclaredParameters(), parameterElement.getParameterNameString())) {
          CommonRefactoringUtil.showErrorHint(file.getProject(), editor, "Parameter already defined in the parent project",
                  "Pull parameter up", null);
          return;
        }

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
          @Override
          public void run() {
            final ParameterElement parameterCopy = parent.getParametersBlock().addParameter();
            parameterCopy.copyFrom(parameterElement);
            parameterElement.undefine();

            PsiNavigateUtil.navigate(parameterCopy.getParameterName().getXmlAttributeValue());
          }
        });
      }

      @Override
      public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {

      }
    };
  }

  @Nullable
  @Override
  public RefactoringActionHandler getIntroduceVariableHandler() {
    return new RefactoringActionHandler() {
      @Override
      public void invoke(@NotNull Project project, final Editor editor, PsiFile file, DataContext dataContext) {
        final SelectionModel selection = editor.getSelectionModel();
        if (!selection.hasSelection()) return;


        final int start = selection.getSelectionStart();
        final int end = selection.getSelectionEnd();

        final PsiElement startElement = file.findElementAt(start);
        final PsiElement endElement = file.findElementAt(start);
        introduceParameter(project, editor, selection, start, end, startElement, endElement);
        introduceMetaRunner(project, editor, selection, start, end, startElement, endElement);
      }

      private void introduceMetaRunner(final Project project, final Editor editor, final SelectionModel selection, final int start, final int end, PsiElement startElement, PsiElement endElement) {
        final BuildRunnersElement holder = TeamCityFile.toTeamCityElement(BuildRunnersElement.class, startElement);
        if (holder == null) return;
        if (!holder.getXmlTag().getTextRange().containsRange(start, end)) return;

        TeamCityFile file = holder.getParentOfType(TeamCityFile.class, false);
        if (file == null) return;
        final ProjectFile projectFile = file.getParentProjectFile();
        if (projectFile == null) return;

        boolean found = false;
        for (BuildRunnerElement runnerElement : holder.getRunners()) {
          if (runnerElement.getXmlTag().getTextRange().intersectsStrict(start, end)) {
            found = true;
            break;
          }
        }

        if (!found) return;


        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
          @Override
          public void run() {

            PsiDirectory dir = projectFile.getOrCreateMetaRunnersDirectory();
            if (dir == null) return;

            PsiFile aFile = dir.createFile("extracted-meta-runner_" + System.currentTimeMillis() + ".xml");

            Document document = PsiDocumentManager.getInstance(project).getDocument(aFile);
            if (document == null) return;

            document.setText(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<meta-runner name=\"generated\">\n" +
                            "<description>test</description>\n" +
                            "<settings></settings>\n" +
                            "</meta-runner>"
            );
            PsiDocumentManager.getInstance(project).commitDocument(document);

            PsiFile newFile = dir.findFile(aFile.getName());
            if (newFile == null) return;
            CodeStyleManager.getInstance(project).reformat(newFile);

            MetaRunnerFile metaFile = TeamCityFile.toTeamCityElement(MetaRunnerFile.class, newFile);
            if (metaFile == null) return;

            int i = 0;
            for (BuildRunnerElement startRunner : holder.getRunners()) {
              if (startRunner.getXmlTag().getTextRange().intersectsStrict(start, end)) {
                final BuildRunnerElement runner = metaFile.getSettings().getBuildRunners().addRunner();
                runner.copyFrom(startRunner);
                startRunner.undefine();
              } else {
                i++;
              }
            }

            BuildRunnerElement meta = holder.addRunner(i);
            meta.getBuildRunnerType().setStringValue(metaFile.getFileId());
          }
        });

      }

      private void introduceParameter(Project project, final Editor editor, final SelectionModel selection, final int start, final int end, PsiElement startElement, PsiElement endElement) {
        final ParameterElement parameter = ParameterElement.fromPsiElement(startElement);
        if (parameter == null) return;

        final XmlAttributeValue parameterValue = parameter.getParameterValue().getXmlAttributeValue();
        if (parameterValue == null) return;

        if (!parameterValue.getTextRange().containsRange(start, end)) return;

        final TeamCitySettingsBasedFile base = parameter.getParentOfType(TeamCitySettingsBasedFile.class, false);
        if (base == null) return;

        final ParametersBlockElement block = base.getSettingsElement().getParametersBlock();
        if (block == null) return;

        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
          @Override
          public void run() {
            ParameterElement param = block.addParameter();
            param.getParameterName().setStringValue("new_parameter");
            param.getParameterValue().setStringValue(selection.getSelectedText());

            editor.getDocument().replaceString(start, end, "%new_parameter%");

            PsiNavigateUtil.navigate(param.getParameterName().getXmlAttributeValue());
          }
        });
      }

      @Override
      public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
      }
    };
  }
}
