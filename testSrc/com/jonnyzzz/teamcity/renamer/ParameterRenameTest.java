package com.jonnyzzz.teamcity.renamer;

import com.intellij.usageView.UsageInfo;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import junit.framework.Assert;

import java.util.Collection;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class ParameterRenameTest extends BaseTest {

  protected String getTestDataPath() {
    return "testData/rename/";
  }


  public void test_01() throws Throwable {
    myFixture.configureByFile("BuildTypePropertyRename_001.xml");

    Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
    for (UsageInfo usage : usages) {
      System.out.printf("  " + usage);
    }
    BuildTypeFile file = TeamCityFile.toTeamCityFile(BuildTypeFile.class, myFixture.getElementAtCaret().getContainingFile());
    Assert.assertNotNull(file);

    myFixture.testRename("BuildTypePropertyRename_001-after.xml", "ggg-after");
  }

  public void test_02() throws Throwable {
    myFixture.configureByFiles(
            "PropertyRename-002/project-config.xml",
            "PropertyRename-002/buildTypes/BuildTypePropertyRename_002-1.xml",
            "PropertyRename-002/buildTypes/BuildTypePropertyRename_002-2.xml");

    myFixture.testRename(
            "PropertyRename-002/project-config-after.xml",
            "ppp002-after");

    myFixture.checkResultByFile("PropertyRename-002/buildTypes/BuildTypePropertyRename_002-1.xml", "PropertyRename-002/buildTypes/BuildTypePropertyRename_002-1-after.xml", true);
    myFixture.checkResultByFile("PropertyRename-002/buildTypes/BuildTypePropertyRename_002-2.xml", "PropertyRename-002/buildTypes/BuildTypePropertyRename_002-2-after.xml", true);
  }

  public void test_03() throws Exception {
    myFixture.configureByFiles(
            "DepRename-003/project-config.xml",
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003-A.xml",
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003.xml");

    myFixture.renameElementAtCaret("QpR");

    myFixture.checkResultByFile(
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003-A.xml",
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003-A-After.xml", true);
    myFixture.checkResultByFile(
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003.xml",
            "DepRename-003/buildTypes/BuildTypePropertyResolve_003-After.xml", true);
  }


}
