package com.instantor.props;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import org.junit.ClassRule;
import org.junit.Before;
import java.io.IOException;
import org.junit.Rule;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsLoaderImplTest {
  @ClassRule
  public static final TemporaryFolder temp = new TemporaryFolder();

  private static final Logger logger = LoggerFactory.getLogger("com.instantor.props.test");

  private static final String projectName = "TestProjectName";
  private static final String globalName  = "global";

  private static final String mainConfigContent =
      "nexus = global\n"
    + "dsl = .\n"
    + "missingReference = nothere\n";
  private static final String nexusConfigContent =
      "host = localhost\n"
    + "port = 9000";
  private static final String dslConfigContent =
      "TestDslConfig";

  private static String propsHome;
  private static File projectFolder;
  private static File globalFolder;
  private static File mainConfigFile;
  private static File nexusConfigFile;
  private static File dslConfigFile;
  private static PropsLoader pl;

  @BeforeClass
  public static void init() throws IOException {
    propsHome       = temp.getRoot().getAbsolutePath();
    projectFolder   = new File(propsHome, projectName);
    globalFolder    = new File(propsHome, globalName);
    mainConfigFile  = TestUtil.writeFile(new File(projectFolder, "_"), mainConfigContent);
    nexusConfigFile = TestUtil.writeFile(new File(globalFolder, "nexus.config"), nexusConfigContent);
    dslConfigFile   = TestUtil.writeFile(new File(projectFolder, "dsl.props"), dslConfigContent);
    pl              = new PropsLoaderImpl(logger, propsHome, mainConfigFile);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingKey() throws IOException {
    pl.resolve("missingKey").toString();
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingReference() throws IOException {
    pl.resolve("missingReference").toString();
  }

  @Test
  public void resolveGlobal() throws IOException {
    final String expected = nexusConfigContent;
    final String actual   = pl.resolve("nexus").toString();
    assertEquals(expected, actual);
  }

  @Test
  public void resolveLocal() throws IOException {
    final String expected = dslConfigContent;
    final String actual   = pl.resolve("dsl").toString();
    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getMissing() {
    pl.resolve("nexus").get("missing");
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNull() {
    pl.resolve("nexus").get(null);
  }

  @Test
  public void get() {
    final String expected = "localhost";
    final String actual   = pl.resolve("nexus").get("host");
    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getIntMissing() {
    pl.resolve("nexus").getInt("missing");
  }

  @Test(expected = IllegalArgumentException.class)
  public void getIntNull() {
    pl.resolve("nexus").getInt(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getIntInvalid() {
    pl.resolve("nexus").getInt("host");
  }

  @Test
  public void getInt() {
    final int expected = 9000;
    final int actual   = pl.resolve("nexus").getInt("port");
    assertEquals(expected, actual);
  }

  @Test
  public void toProps() {
    final Properties expected = new Properties();
    expected.put("host", "localhost");
    expected.put("port", "9000");
    final Properties actual = pl.resolve("nexus").toProps();
    assertEquals(expected, actual);
  }

  @Test
  public void toMap() {
    final Map<String, String> expected = new HashMap<>();
    expected.put("host", "localhost");
    expected.put("port", "9000");
    final Map<String, String> actual = pl.resolve("nexus").toMap();
    assertEquals(expected, actual);
  }

  @Test
  public void toFile() {
    final File expected = new File(temp.getRoot(), projectName + File.separator + "_");
    final File actual   = pl.toFile();
    assertEquals(expected, actual);
  }

  @Test
  public void toPath() {
    final String expected = temp.getRoot().toString() + File.separator + projectName + File.separator + "_";
    final String actual   = pl.toPath();
    assertEquals(expected, actual);
  }

  @Test
  public void toByteArray() throws UnsupportedEncodingException {
    final byte[] expected = nexusConfigContent.getBytes("ISO-8859-1");
    final byte[] actual   = pl.resolve("nexus").toByteArray();
    assertArrayEquals(expected, actual);
  }
  
  @Test
  public void toStringTest() {
    final String expected = nexusConfigContent;
    final String actual   = pl.resolve("nexus").toString();
    assertEquals(expected, actual);
  }
}







/*
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

public static final File GlobalGlobalFile   = new File(GlobalFolder, "_");
public static final File AliasFile          = new File(GlobalFolder, "serverAlias.txt");
public static final File MainConfigFile     = new File(ProjectFolder, "_");
public static final File GlobalConfigFile   = new File(GlobalFolder, GlobalName + ".config");
public static final File LocalConfigFile    = new File(ProjectFolder, LocalName + ".props");
public static final File AbsoluteConfigFile = new File(AbsoluteFolder, AbsoluteName + ".bilosto");

public static final String GlobalGlobalContent   = "serverAlias = .";
public static final String AliasContent          = "TestAliasContent";
public static final String MainConfigContent     = String.format(GlobalName + " = global\n"
                                                               + LocalName + " = .\n"
                                                               + AbsoluteName + " = /" + AbsoluteFolder.toString().replace("\\", "\\\\") + "\n");
public static final String GlobalConfigContent   = "TestNexusContent";
public static final String LocalConfigContent    = "TestDslContent";
public static final String AbsoluteConfigContent = "TestAbsoluteConfig";

public static final File GlobalGlobalMarker = new File(AbsoluteFolder, "GlobalGlobalMarker");
public static final File AliasMarker        = new File(AbsoluteFolder, "AliasMarker");

@BeforeClass
public static void prepare() {
  // Global global
  if (!GlobalGlobalFile.exists()) {
    TestUtil.writeFile(GlobalGlobalFile, GlobalGlobalContent);
    TestUtil.writeFile(GlobalGlobalMarker, "");
  }

  // Alias
  if (!AliasFile.exists()) {
    TestUtil.writeFile(AliasFile, AliasContent);
    TestUtil.writeFile(AliasMarker, "");
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
  // Global global
  if (GlobalGlobalMarker.exists()) {
    GlobalGlobalFile.delete();
  }

  // Alias
  if (AliasMarker.exists()) {
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
  final String actual   = PropsLoaderImpl.load(ProjectName).toString();
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
*/
