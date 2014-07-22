package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsLoaderTest {
  public static final TemporaryFolder Temp = TestUtil.createTemp();

  public static final String Home         = System.getProperty("user.home");
  public static final String ProjectName  = "TestProject";
  public static final String BranchName   = "tb";
  public static final String GlobalName   = "testNexus";
  public static final String LocalName    = "dsl";
  public static final String AbsoluteName = "nesto";

  public static final File GlobalFolder   = new File(String.format("%s/.props/global", Home));
  public static final File ProjectFolder  = new File(String.format("%s/.props/%s_%s", Home, ProjectName, BranchName));
  public static final File AbsoluteFolder = Temp.getRoot();

  public static final File AliasFile          = new File(GlobalFolder, "serverAlias.txt");
  public static final File MainConfigFile     = new File(ProjectFolder, "_");
  public static final File GlobalConfigFile   = new File(GlobalFolder, GlobalName + ".config");
  public static final File LocalConfigFile    = new File(ProjectFolder, LocalName + ".props");
  public static final File AbsoluteConfigFile = new File(AbsoluteFolder, AbsoluteName + ".bilosto");

  public static final String AliasContent     = TestUtil.getHostName();

  public static final String MainConfigContent     = String.format(GlobalName + " = global\n"
                                                                 + LocalName + " = .\n"
                                                                 + AbsoluteName + " = /" + AbsoluteFolder.toString().replace("\\", "\\\\") + "\n");
  public static final String GlobalConfigContent   = "TestNexusContent";
  public static final String LocalConfigContent    = "TestDslContent";
  public static final String AbsoluteConfigContent = "TestAbsoluteConfig";

  @BeforeClass
  public static void prepare() {
    // Alias
    if (!AliasFile.exists()) {
      TestUtil.writeFile(AliasFile, AliasContent);
    }

    // Main config
    final String branchKey = ProjectName + ".branch";
    final String branchVal = BranchName;
    System.getProperties().put(branchKey, branchVal);
    TestUtil.writeFile(MainConfigFile, MainConfigContent);

    // Global config
    TestUtil.writeFile(GlobalConfigFile, GlobalConfigContent);

    // Local config
    TestUtil.writeFile(LocalConfigFile, LocalConfigContent);

    // Absolute config
    TestUtil.writeFile(AbsoluteConfigFile, AbsoluteConfigContent);
  }

  @AfterClass
  public static void clean() {
    // Alias
    final String gc = TestUtil.readFile(AliasFile);
    if (AliasContent.equals(gc)) {
      AliasFile.delete();
    }

    // Main config
    MainConfigFile.delete();
    MainConfigFile.getParentFile().delete();

    // Global config
    GlobalConfigFile.delete();

    // Local config
    LocalConfigFile.delete();

    // Folders
    GlobalFolder.delete();
    ProjectFolder.delete();
    Temp.delete();
  }

  @Test
  public void load() {
    final String expected = MainConfigContent.trim();
    final String actual   = PropsLoader.load(ProjectName).toString();
    assertEquals(expected, actual);
  }

  @Test
  public void serverAlias() {
    final String expected = TestUtil.readFile(AliasFile).trim();
    final String actual   = PropsLoader.getServerAlias();
    assertEquals(expected, actual);
  }

  @Test()
  public void resolveGlobal() {
    final String expected = TestUtil.readFile(GlobalConfigFile);
    final String actual   = PropsLoader.load(ProjectName).resolve(GlobalName).toString();
    assertEquals(expected, actual);
  }

  @Test
  public void resolveLocal() {
    final String expected = TestUtil.readFile(LocalConfigFile);
    final String actual   = PropsLoader.load(ProjectName).resolve(LocalName).toString();
    assertEquals(expected, actual);
  }

  @Test
  public void resolveAbsolute() {
    final String expected = TestUtil.readFile(AbsoluteConfigFile);
    final String actual   = PropsLoader.load(ProjectName).resolve(AbsoluteName).toString();
    assertEquals(expected, actual);
  }
}
