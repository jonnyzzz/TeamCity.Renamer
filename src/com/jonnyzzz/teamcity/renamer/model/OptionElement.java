package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public abstract class OptionElement {
  @Attribute("name")
  public abstract GenericAttributeValue<String> getOptionName();

  @Attribute("value")
  public abstract GenericAttributeValue<String> getOptionValue();

  public void setOptionName(@NotNull String newName) {
    getOptionName().setStringValue(newName);
  }

  public void setOptionValue(@NotNull String newValue) {
    getOptionValue().setStringValue(newValue);
  }
}
