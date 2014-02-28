package com.jonnyzzz.teamcity.renamer;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.testFramework.ResolveTestCase;
import org.junit.Assert;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParametersResolveTest extends ResolveTestCase {

  @Override
  protected String getTestDataPath() {
    return "testData/resolve/";
  }

  public void test_propertyResolve_001() throws Exception {
    final PsiReference ref = configureByFile("BuildTypePropertyResolve_001.xml");
    PsiElement resolved = ref.resolve();

    Assert.assertTrue(resolved instanceof XmlAttributeValue && ((XmlAttributeValue) resolved).getValue().equals("ggg"));
  }

  public void test_propertyResolve_002() throws Exception {
    final PsiReference ref = configureByFile("BuildTypePropertyResolve_002.xml");
    PsiElement resolved = ref.resolve();

    Assert.assertTrue(resolved instanceof XmlAttributeValue && ((XmlAttributeValue) resolved).getValue().equals("ggg"));
  }





}
