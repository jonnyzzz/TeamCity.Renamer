package com.jonnyzzz.teamcity.renamer.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.XmlCodeFoldingBuilder;
import com.intellij.lang.XmlCodeFoldingSettings;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.UnfairTextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import com.intellij.xml.util.XmlTagUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class RunnersFoldingBuilder extends XmlCodeFoldingBuilder {
  @Override
  protected XmlCodeFoldingSettings getFoldingSettings() {
    return new XmlCodeFoldingSettings() {
      @Override
      public boolean isCollapseXmlTags() {
        return false;
      }

      @Override
      public boolean isCollapseHtmlStyleAttribute() {
        return false;
      }
    };
  }

  @Nullable
  @Override
  public String getPlaceholderText(@NotNull ASTNode node) {
    final String element = findBuildRunnerElement(node);
    if (element != null) return " " + element;
    return null;
  }

  @Override
  public boolean isCollapsedByDefault(@NotNull ASTNode node) {
    return findBuildRunnerElement(node) != null;
  }

  @Nullable
  private String findBuildRunnerElement(@NotNull final ASTNode node) {
    final PsiElement psi = node.getPsi();
    if (psi == null) return null;
    if (!(psi instanceof XmlTag))return null;

    final DomElement dom = DomManager.getDomManager(psi.getProject()).getDomElement((XmlTag) psi);
    if (dom == null) return null;
    
    if (!(dom instanceof AutoFoldableElement)) return null;
    final AutoFoldableElement fold = (AutoFoldableElement) dom;

    return fold.getFoldedText();
  }

  @Nullable
  public TextRange getRangeToFold(PsiElement element) {
    if (element instanceof XmlTag) {
      final ASTNode tagNode = element.getNode();
      final XmlToken tagNameElement = XmlTagUtil.getStartTagNameElement((XmlTag) element);
      if (tagNameElement == null) return null;

      int nameEnd = tagNameElement.getTextRange().getEndOffset();
      int end = tagNode.getLastChildNode().getTextRange().getEndOffset() - 1;  // last child node can be another tag in unbalanced tree

      return new UnfairTextRange(nameEnd, end);
    }

    return null;
  }
}
