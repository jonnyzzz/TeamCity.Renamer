package com.jonnyzzz.teamcity.renamer.inspection;

import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ResolvedInspection extends BasicDomElementsInspection<BuildTypeFile> {
  public ResolvedInspection() {
    //noinspection unchecked
    super(BuildTypeFile.class);
  }
}
