package com.jonnyzzz.teamcity.renamer.folding;

import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * Marker interface to force folding of the elements
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public interface AutoFoldableElement extends DomElement {
  @NotNull
  String getFoldedText();
}
