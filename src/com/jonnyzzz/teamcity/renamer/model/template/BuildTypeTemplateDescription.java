package com.jonnyzzz.teamcity.renamer.model.template;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class BuildTypeTemplateDescription extends DomFileDescription<BuildTemplateFile> {
  public BuildTypeTemplateDescription() {
    super(BuildTemplateFile.class, "template");
  }
}
