package com.jonnyzzz.teamcity.renamer.model.project;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.template.BuildTemplateFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import com.jonnyzzz.teamcity.renamer.resolve.settings.DeclaredTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityFile {

  @Attribute("parent-id")
  public abstract GenericAttributeValue<String> getParentProjectIdElement();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();

  @Nullable
  public String getParentProjectId() {
    final GenericAttributeValue<String> parentProjectAttribute = getParentProjectIdElement();
    if (parentProjectAttribute != null) {
      final String parentProjectId = parentProjectAttribute.getStringValue();
      if (parentProjectId != null && parentProjectId.trim().length() > 0) {
        return parentProjectId;
      }
    }
    if ("_Root".equals(getFileId())) return null;
    return "_Root";
  }

  @Nullable
  public String getFileId() {
    final PsiDirectory containingDirectory = getContainingDirectory();
    if (containingDirectory == null) return null;

    return containingDirectory.getName();
  }

  @Nullable
  protected PsiDirectory getContainingDirectory() {
    final XmlElement xmlElement = getXmlElement();
    if (xmlElement == null) return null;

    final PsiFile containingFile = xmlElement.getContainingFile();
    if (containingFile == null) return null;

    final PsiDirectory containingDirectory = containingFile.getContainingDirectory();
    if (containingDirectory == null) return null;
    return containingDirectory;
  }


  @NotNull
  @Override
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    return getParametersBlock().getDeclarations();
  }

  @Nullable
  @Override
  public ProjectFile getParentProjectFile() {
    final GenericAttributeValue<String> parentProjectAttribute = getParentProjectIdElement();
    if (parentProjectAttribute == null) return null;

    final String parentProjectId = getParentProjectId();
    if (parentProjectId == null) return null;

    final XmlElement xmlElement = getXmlElement();
    if (xmlElement == null) return null;

    final PsiFile containingFile = xmlElement.getContainingFile();
    if (containingFile == null) return null;

    final PsiDirectory containingDir = containingFile.getParent();
    if (containingDir == null) return null;

    final PsiDirectory projectsDir = containingDir.getParent();
    if (projectsDir == null) return null;

    final PsiDirectory parentDir = projectsDir.findSubdirectory(parentProjectId);
    if (parentDir == null) return null;

    final PsiFile projectFile = parentDir.findFile("project-config.xml");
    if (projectFile == null) return null;
    if (!(projectFile instanceof XmlFile)) return null;

    final XmlFile xmlFile = (XmlFile) projectFile;
    final DomElement projectXml = DomManager.getDomManager(xmlElement.getProject()).getDomElement(xmlFile.getRootTag());

    if (projectXml == null) return null;
    if (!(projectXml instanceof ProjectFile)) return null;

    return (ProjectFile) projectXml;
  }


  @NotNull
  public Iterable<BuildTypeFile> getBuildTypes() {
    return getBuildOrTemplates(BuildTypeFile.class);
  }

  @NotNull
  public Iterable<BuildTemplateFile> getTemplates() {
    return getBuildOrTemplates(BuildTemplateFile.class);
  }

  @NotNull
  public Iterable<DeclaredTemplate> getDeclaredTemplates() {
    return FluentIterable
            .from(getTemplates())
            .transform(new Function<BuildTemplateFile, DeclaredTemplate>() {
              @Override
              public DeclaredTemplate apply(BuildTemplateFile buildTemplateFile) {
                final String fileId = buildTemplateFile.getFileId();
                if (fileId == null) return null;
                return new DeclaredTemplate(fileId, buildTemplateFile);
              }
            }).filter(Predicates.notNull());
  }

  @NotNull
  private <T extends TeamCityFile> Iterable<T> getBuildOrTemplates(@NotNull final Class<T> type) {
    final PsiDirectory dir = getContainingDirectory();
    if (dir == null) return ImmutableList.of();

    final PsiDirectory buildTypesDir = dir.findSubdirectory("buildTypes");
    if (buildTypesDir == null) return ImmutableList.of();

    return FluentIterable
            .from(ImmutableList.copyOf(buildTypesDir.getFiles()))
            .filter(XmlFile.class)
            .filter(new Predicate<XmlFile>() {
              @Override
              public boolean apply(XmlFile xmlFile) {
                return xmlFile.getName().endsWith(".xml");
              }
            }).transform(new Function<XmlFile, DomElement>() {
              @Override
              public DomElement apply(XmlFile xmlFile) {
                return DomManager.getDomManager(xmlFile.getProject()).getDomElement(xmlFile.getRootTag());
              }
            }).filter(type);
  }
}
