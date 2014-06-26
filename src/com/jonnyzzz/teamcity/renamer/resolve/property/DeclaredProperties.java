package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.intellij.util.xml.DomElement;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
            fromParentParameters(file)
    );
  }

  @NotNull
  private static Iterable<DeclaredProperty> fromParentParameters(@NotNull final TeamCityFile file) {
    return Iterables.concat(FluentIterable
                    .from(getProjectFiles(file.getParentProjectFile()))
                    .transformAndConcat(FILE_TO_DECLARATIONS),
            getParametersFromTemplate(file)
    );
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
                        .from(Iterables.concat(project.getSubProjects(), project.getBuildTypes(), project.getTemplates(), project.getOwnVcsRoots()))
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
      if (overrideElement.equals(declarationElement)) continue;
      if (overrideElement.getParent().equals(declarationElement.getParent())) continue; // Duplicated parameter case
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

  /**
   * @return map from given parameter to detected override
   */
  @NotNull
  public static Multimap<ParameterElement, ParameterElement> findOverriddenParametersFromParents(@NotNull final Iterable<ParameterElement> elements) {
    final Map<String, ParameterElement> nameToGiven = indexElements(elements);
    TeamCityFile containingFile = sameContainingFile(elements);

    if (containingFile == null) return ImmutableListMultimap.of();

    final Multimap<ParameterElement, ParameterElement> map = ArrayListMultimap.create();
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
    if (file instanceof BuildTypeFile) {
      final BuildTemplateFile baseTemplate = ((BuildTypeFile) file).getBaseTemplate();
      if (baseTemplate != null) {
        return baseTemplate.getDeclaredParameters();
      }
    }

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
