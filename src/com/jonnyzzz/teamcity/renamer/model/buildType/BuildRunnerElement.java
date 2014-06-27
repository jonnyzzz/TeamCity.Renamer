package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BuildRunnerElement extends TeamCityElement implements AutoFoldableElement {

  @Attribute("id")
  public abstract GenericAttributeValue<String> getId();

  @Required
  @Attribute("type")
  public abstract GenericAttributeValue<String> getType();

  @Attribute("name")
  public abstract GenericAttributeValue<String> getName();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();


  @NotNull
  @Override
  public final String getFoldedText() {
    final String type = getType().getStringValue();
    final String extras = PredefinedRunners.extras(type, getParametersBlock());
    return PredefinedRunners.describeType(type) + " " + getName() + (extras != null ? " (" + extras + ")" : "");
  }

  private static enum PredefinedRunners {
    CMD("simpleRunner", "Command Line"),
    IDEA("JPS", "IntelliJ IDEA"),
    ANT("Ant", "Ant"),
    MAVEN("Maven2", "Maven"),
    MSBuild("MSBuild", "MSBuild"),
    Dups("Duplicator", "IntelliJ Duplicates"),
    Inspections("Inspection", "IntelliJ Inspections"),
    ;


    private final String myId;
    private final String myName;

    PredefinedRunners(String id, String name) {
      myId = id;
      myName = name;
    }

    @Nullable
    protected String extra(@NotNull final Map<String, String> params) {
      return null;
    }

    @Nullable
    public static String describeType(@Nullable final String type) {
      for (PredefinedRunners val : values()) {
        if (val.myId.equals(type)) return val.myName;
      }
      return type;
    }

    @Nullable
    public static String extras(@Nullable final String type,
                                @Nullable final ParametersBlockElement params) {
      if (type == null || params == null) return null;

      for (PredefinedRunners val : values()) {
        if (val.myId.equals(type)) return val.extra(params.toMap());
      }

      return null;
    }
  }
}
