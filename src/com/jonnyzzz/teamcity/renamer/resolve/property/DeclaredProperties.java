package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

  @NotNull
  public static Iterable<DeclaredProperty> childrenDeclarations(@NotNull final TeamCityFile file) {
    if (!(file instanceof ProjectFile)) {
      return file.getDeclaredParameters();
    }

    final ProjectFile project = (ProjectFile) file;
    return Iterables.concat(
            project.getDeclaredParameters(),
            new Iterable<DeclaredProperty>() {
              @Override
              public Iterator<DeclaredProperty> iterator() {
                return FluentIterable
                        .from(Iterables.concat(project.getSubProjects(), project.getBuildTypes(), project.getTemplates()))
                        .transformAndConcat(CHILDREN_DECLARATIONS)
                        .iterator();
              }
            }
    );
  }

  private static final Function<TeamCityFile, Iterable<DeclaredProperty>> CHILDREN_DECLARATIONS = new Function<TeamCityFile, Iterable<DeclaredProperty>>() {
    @Override
    public Iterable<DeclaredProperty> apply(TeamCityFile teamCityFile) {
      return childrenDeclarations(teamCityFile);
    }
  };

  /**
   * @return map for parameter form the file to parameters where it was overridden
   */
  @NotNull
  public static Multimap<ParameterElement, ParameterElement> findOverriddenByChildrenParameters(@NotNull final List<ParameterElement> file) {
    final Multimap<ParameterElement, ParameterElement> result = ArrayListMultimap.create();
    if (file.isEmpty()) return result;

    final Map<String, ParameterElement> index = indexElements(file);

    final TeamCityFile containingFile = sameContainingFile(file);
    if (containingFile == null) return result;


    for (DeclaredProperty property : childrenDeclarations(containingFile)) {
      final ParameterElement declarationElement = index.get(property.getName());
      if (declarationElement == null) continue;

      final ParameterElement overrideElement = property.getParameterElement();
      if (overrideElement == declarationElement) continue;
      result.put(declarationElement, overrideElement);
    }

    return result;
  }

  @NotNull
  private static Map<String, ParameterElement> indexElements(@NotNull Iterable<ParameterElement> elements) {
    final Map<String, ParameterElement> nameToGiven = new HashMap<>();
    for (ParameterElement element : elements) {
      final String name = element.getParameterNameString();
      if (name == null) continue;
      nameToGiven.put(name, element);
    }

    return nameToGiven;
  }

  @NotNull
  private static Map<String, DeclaredProperty> indexProperties(@NotNull Iterable<DeclaredProperty> elements) {
    final Map<String, DeclaredProperty> nameToGiven = new HashMap<>();
    for (DeclaredProperty element : elements) {
      final String name = element.getName();
      nameToGiven.put(name, element);
    }
    return nameToGiven;
  }

  /**
   * @return map from given parameter to detected override
   */
  @NotNull
  public static Map<ParameterElement, ParameterElement> findOverriddenParametersFromParents(@NotNull final Iterable<ParameterElement> elements) {
    final Map<ParameterElement, ParameterElement> map = new HashMap<>();
    final Map<String, ParameterElement> nameToGiven = indexElements(elements);
    TeamCityFile containingFile = sameContainingFile(elements);

    if (containingFile == null) return Collections.emptyMap();
    for (DeclaredProperty dp : fromParentParameters(containingFile)) {
      final ParameterElement originalElement = nameToGiven.get(dp.getName());
      if (originalElement != null) map.put(originalElement, dp.getParameterElement());
    }

    return map;
  }

  @Nullable
  private static TeamCityFile sameContainingFile(@NotNull final Iterable<ParameterElement> elements) {
    TeamCityFile containingFile = null;
    for (ParameterElement element : elements) {
      final String name = element.getParameterNameString();
      if (name == null) continue;

      final TeamCityFile aFile = element.getParentOfType(TeamCityFile.class, false);
      if (containingFile == null) {
        containingFile = aFile;
      } else if (!containingFile.equals(aFile)){
        throw new RuntimeException("All elements are expected to have same file");
      }
    }

    return containingFile;
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
