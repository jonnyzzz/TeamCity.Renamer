package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.util.xml.SubTagList;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ParametersBlockElement extends TeamCityElement {

  @SubTagList("param")
  public abstract List<ParameterElement> getParameters();

  @NotNull
  public Iterable<DeclaredProperty> getDeclarations() {
    return FluentIterable
            .from(getParameters())
            .transform(DeclaredProperty.FROM_PARAMETER_ELEMENT)
            .filter(Predicates.notNull());

  }

  @NotNull
  public Map<String, String> toMap() {
    final Map<String, String> map = new TreeMap<>();

    for (DeclaredProperty props : getDeclarations()) {
      map.put(props.getName(), props.getRawValue());
    }

    return map;
  }
}
