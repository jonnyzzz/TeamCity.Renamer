package com.jonnyzzz.teamcity.renamer.diagram;

import com.intellij.diagram.DiagramAction;
import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.actions.DiagramAddElementAction;
import com.intellij.diagram.actions.PopupCallback;
import com.intellij.diagram.util.DiagramUtils;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.gotoByName.SimpleChooseByNameModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.UIUtil;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.resolve.buildTypes.TeamCityFileNamedReference;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class AddChildrenAction extends DiagramAddElementAction {
  public void gotoActionPerformed(final AnActionEvent e) {
    final Project project = e.getData(CommonDataKeys.PROJECT);
    final PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
    if (!(element instanceof TeamCityFileNamedReference)) return;
    final TeamCityFileNamedReference ref = (TeamCityFileNamedReference) element;

    final DiagramBuilder builder = DiagramAction.getBuilder(e);

    if (project == null || builder == null) return;

    PsiDocumentManager.getInstance(project).commitAllDocuments();

    final ChooseByNamePopup popup = ChooseByNamePopup.createPopup(project, new ChooseByNameModel(project, ref), getPsiContext(e));
    popup.setSearchInAnyPlace(true);
    popup.invoke(new ChooseByNamePopupComponent.Callback() {
      public void onClose() {
        if (AddChildrenAction.class.equals(myInAction)) myInAction = null;
      }

      public void elementChosen(final Object element) {
        DiagramAction.createCallback(builder, new PopupCallback.Adapter(new Runnable() {
          @Override
          public void run() {
            final DiagramDataModel dataModel = builder.getDataModel();
            final DiagramNode node = dataModel.addElement(new TCElement((com.jonnyzzz.teamcity.renamer.model.TeamCitySettingsBasedFile) element));
            if (node != null) {
              final Point point = DiagramUtils.getBestPositionForNode(builder);
              builder.createDraggedNode(node, node.getTooltip(), point);
              builder.updateGraph();
            }
            builder.requestFocus();
          }
        }, getText())).run();
      }
    }, ModalityState.current(), true);
  }

  @Override
  public String getText() {
    return "Add dependencies";
  }

  private static class ChooseByNameModel extends SimpleChooseByNameModel {
    private final Map<String, BuildTypeFile> modules = new HashMap<>();

    public ChooseByNameModel(@NotNull final Project project,
                             @NotNull final TeamCityFileNamedReference ref) {
      super(project, "Enter name", null);

      for (BuildTypeFile file : ref.getDependencies()) {
        final String name = file.getFileName().getStringValue();
        if (name != null) {
          modules.put(name, file);
        }
      }
    }

    @Override
    public String[] getNames() {
      return ArrayUtil.toStringArray(modules.keySet());
    }

    @Override
    protected Object[] getElementsByName(String name, String pattern) {
      final BuildTypeFile module = modules.get(name);
      return module == null ? AnAction.EMPTY_ARRAY : new Object[] {module};
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
      return new ModulesListCellRenderer();
    }

    @Override
    public String getElementName(Object element) {
      return element instanceof BuildTypeFile ? ((BuildTypeFile) element).getFileName().getStringValue() : "";
    }

    public static class ModulesListCellRenderer extends DefaultListCellRenderer {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        final Color bg = isSelected ? UIUtil.getListSelectionBackground() : UIUtil.getListBackground();
        final Color fg = isSelected ? UIUtil.getListSelectionForeground() : UIUtil.getListForeground();
        panel.setBackground(bg);

        if (value instanceof BuildTypeFile) {
          final BuildTypeFile module = (BuildTypeFile) value;
          final JLabel label = new JLabel(module.getFilePresentableNameText(), LEFT);
          label.setBackground(bg);
          label.setForeground(fg);
          panel.add(label, BorderLayout.WEST);
          return panel;
        } else {
          return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
      }
    }
  }
}
