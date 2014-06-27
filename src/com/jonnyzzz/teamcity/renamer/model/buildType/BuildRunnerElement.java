package com.jonnyzzz.teamcity.renamer.model.buildType;

import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityElement;
import com.jonnyzzz.teamcity.renamer.resolve.metaRunner.MetaRunnerReferenceConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BuildRunnerElement extends TeamCityElement implements AutoFoldableElement {

  @Attribute("id")
  public abstract GenericAttributeValue<String> getBuildRunnerId();

  @Required
  @Attribute("type")
  @Convert(value = MetaRunnerReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getBuildRunnerType();

  @Attribute("name")
  public abstract GenericAttributeValue<String> getBuildRunnerName();

  @Stubbed
  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();


  @Nullable
  @Override
  public final String getFoldedText() {
    final GenericAttributeValue<String> typeValue = getBuildRunnerType();
    if (typeValue == null) return null;
    final String type = typeValue.getStringValue();
    if (type == null) return null;

    final String extras = PredefinedRunners.extras(type, getParametersBlock());
    final GenericAttributeValue<String> name = getBuildRunnerName();
    final String nameValue = name == null ? null : name.getStringValue();

    return PredefinedRunners.describeType(type) + " " + (nameValue == null ? "" : nameValue) + (extras != null ? " (" + extras + ")" : "");
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
