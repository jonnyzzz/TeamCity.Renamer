package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.jonnyzzz.teamcity.renamer.resolve.Visitors.getProjectFiles;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DeclaredProperties {

  @NotNull
  public static Iterable<DeclaredProperty> fromContext(@NotNull final DomElement element) {
    final TeamCityFile file = element.getParentOfType(TeamCityFile.class, false);
    if (file == null) return ImmutableList.of();

    return Iterables.concat(
            file.getDeclaredParameters(),
            fromParentParameters(file),
            getParametersFromTemplate(file)
    );
  }

  @NotNull
  private static Iterable<DeclaredProperty> fromParentParameters(@NotNull final TeamCityFile file) {
    return FluentIterable
            .from(getProjectFiles(file.getParentProjectFile()))
            .transformAndConcat(FILE_TO_DECLARATIONS);
  }

  /**
   * @return map from given parameter to detected override
   */
  @NotNull
  public static Map<ParameterElement, ParameterElement> findParametersOverride(@NotNull final Iterable<ParameterElement> elements) {
    final Map<ParameterElement, ParameterElement> map = new HashMap<>();
    final Map<String, ParameterElement> nameToGiven = new HashMap<>();
    TeamCityFile containingFile = null;
    for (ParameterElement element : elements) {
      nameToGiven.put(element.getParameterName().getStringValue(), element);

      final TeamCityFile aFile = element.getParentOfType(TeamCityFile.class, false);
      if (containingFile == null) {
        containingFile = aFile;
      } else if (!containingFile.equals(aFile)){
        throw new RuntimeException("All elements are expected to have same file");
      }
    }

    if (containingFile == null) return Collections.emptyMap();
    for (DeclaredProperty dp : fromParentParameters(containingFile)) {
      final ParameterElement originalElement = nameToGiven.get(dp.getName());
      if (originalElement != null) map.put(originalElement, dp.getParameterElement());
    }

    return map;
  }

  @NotNull
  private static Iterable<DeclaredProperty> getParametersFromTemplate(@NotNull final TeamCityFile file) {
    //TODO: fixme
    return ImmutableList.of();
  }


  private static final Function<ProjectFile, Iterable<DeclaredProperty>> FILE_TO_DECLARATIONS
          = new Function<ProjectFile, Iterable<DeclaredProperty>>() {
    @Override
    public Iterable<DeclaredProperty> apply(ProjectFile projectFile) {
      return projectFile.getDeclaredParameters();
    }
  };


}
