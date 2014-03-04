package com.jonnyzzz.teamcity.renamer;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import com.intellij.testFramework.LightCodeInsightTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParametersRenameTest extends LightCodeInsightTestCase {

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "testData/rename/";
  }


  public void test_propertyRename_001() throws Exception {
    doTest("BuildTypePropertyRename_001");
  }


  public void test_property_rename_should_include_overridden() {
    fail();
  }


  private void doTest(@NotNull final String filePath) throws Exception {

    final String fullPath = getTestDataPath() + filePath + ".xml";
    final VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(fullPath.replace(File.separatorChar, '/'));
    assertNotNull("file " + filePath + " not found", vFile);

    String text = StringUtil.convertLineSeparators(VfsUtil.loadText(vFile));

    final int off = text.indexOf("<ren>");
    text = text.replace("<ren>", "");

    configureFromFileText(vFile.getName(), text);


    assertNotNull(myFile);
    PsiElement element = TargetElementUtilBase.getInstance().findTargetElement(
            getEditor(),
            TargetElementUtilBase.REFERENCED_ELEMENT_ACCEPTED | TargetElementUtilBase.ELEMENT_NAME_ACCEPTED,
            off);
    assertNotNull(element);
    assertTrue(element instanceof PsiNamedElement);
    final RenameRefactoring rename =
            RefactoringFactory.getInstance(getProject()).createRename(element, ((PsiNamedElement)element).getName() + "-after");
    rename.setSearchInComments(false);
    rename.setSearchInNonJavaFiles(false);
    rename.run();
    checkResultByFile(filePath + "-after.xml");
  }




}
