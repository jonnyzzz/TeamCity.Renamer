package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import org.jetbrains.annotations.Nullable;

/**
* @author Eugene Petrenko (eugene.petrenko@gmail.com)
*/
public abstract class BuildExtension extends TeamCityElement implements AutoFoldableElement {

  @Attribute("id")
  public abstract GenericAttributeValue<String> getExtensionId();

  @Attribute("type")
  public abstract GenericAttributeValue<String> getExtensionType();

  @Stubbed
  @SubTag("parameters")
  public abstract ParametersBlockElement getParameters();

  @Nullable
  @Override
  public String getFoldedText() {
    GenericAttributeValue<String> type = getExtensionType();
    if (type == null) return null;
    String value = type.getStringValue();
    if (value == null) return null;
    switch (value) {
      case "BuildFailureOnMetric": return "Build Metric condition";
      case "BuildFailureOnMessage":return "Build Output condition";
      case "vcsTrigger":return "VCS Trigger";
      case "swabra":return "Swabra";
    }

    return value;
  }
}
