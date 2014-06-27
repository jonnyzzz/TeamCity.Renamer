package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import org.jetbrains.annotations.Nullable;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*/
public abstract class BuildTrigger extends TeamCityElement implements AutoFoldableElement {
  @SubTag("parameters")
  public abstract ParametersBlockElement getParameters();

  @Attribute("type")
  public abstract GenericAttributeValue<String> getTriggerType();

  @Nullable
  @Override
  public String getFoldedText() {
    GenericAttributeValue<String> type = getTriggerType();
    if (type == null) return null;
    String value = type.getStringValue();
    if (value == null) return null;
    switch (value) {
      case "buildDependencyTrigger": return "Depends on ..."; //TODO: resolve BuildConfiguration
      case "vcsTrigger":return "VCS Trigger";
    }

    return value;
  }

}
