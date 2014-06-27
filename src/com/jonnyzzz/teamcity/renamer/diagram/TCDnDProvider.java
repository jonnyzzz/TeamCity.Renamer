package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.extras.providers.DiagramDnDProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TCDnDProvider implements DiagramDnDProvider<TCElement> {
  @Override
  public boolean isAcceptedForDnD(Object o, Project project) {
    return o instanceof PsiElement;
  }

  @Nullable
  @Override
  public TCElement[] wrapToModelObject(Object o, Project project) {
    if (o instanceof PsiDirectory) {
      PsiDirectory dir = ((PsiDirectory) o);
      final List<PsiFile> files = new ArrayList<>();
      dir.acceptChildren(new PsiElementVisitor() {
        @Override
        public void visitFile(PsiFile file) {
          files.add(file);
        }

        @Override
        public void visitDirectory(PsiDirectory dir) {
          dir.acceptChildren(this);
        }
      });
      List<TCElement> result = new ArrayList<>();
      for (PsiFile f : files) {
        TeamCitySettingsBasedFile tcFile = TeamCityFile.toTeamCityElement(TeamCitySettingsBasedFile.class, f);
        if (tcFile != null)
          result.add(new TCElement(tcFile));
      }
      return result.toArray(new TCElement[result.size()]);
    }
    if (o instanceof PsiElement) {
      TeamCitySettingsBasedFile file = TeamCityFile.toTeamCityElement(TeamCitySettingsBasedFile.class, (PsiElement) o);
      if (file != null)
        return new TCElement[]{new TCElement(file)};
    }
    return new TCElement[0];
  }
}
