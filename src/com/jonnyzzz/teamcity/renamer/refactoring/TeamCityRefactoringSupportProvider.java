package com.jonnyzzz.teamcity.renamer.refactoring;

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
import com.intellij.util.PsiNavigateUtil;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class TeamCityRefactoringSupportProvider extends RefactoringSupportProvider {

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
