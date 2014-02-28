package com.jonnyzzz.teamcity.renamer.model;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.intellij.util.xml.SubTagList;
import com.jonnyzzz.teamcity.renamer.resolve.DeclaredProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
}
