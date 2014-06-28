package com.jonnyzzz.teamcity.renamer.refactoring;

import com.google.common.collect.Iterables;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.util.PsiNavigateUtil;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
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
