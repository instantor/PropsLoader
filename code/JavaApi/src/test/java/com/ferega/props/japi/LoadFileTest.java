package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoadFileTest {
  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  public static final String Ext1 = ".txt";
  public static final String Ext2 = ".rtf";
  public static final String MissingFilename = "missing";
  public static final String ExistingFilename = "existing";
  public static final String ExistingContent = "Existing content!";

  private File getFile(final String name) {
    return new File(testFolder.getRoot(), name);
  }

  @Test(expected = NoSuchFileException.class)
  public void loadMissingExplicit() throws Throwable {
    try {
      final File actualFile = getFile(MissingFilename + Ext1);
      Util.loadFile(actualFile, false);
    } catch (final Exception e) {
      throw e.getCause();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadMissingAuto() throws Throwable {
    try {
      final File actualFile = getFile(MissingFilename);
      Util.loadFile(actualFile, true);
    } catch (final Exception e) {
      throw e.getCause();
    }
  }

  @Test
  public void loadExistingExplicit() throws IOException {
    TestUtil.writeFile(getFile(ExistingFilename + Ext1), ExistingContent);
    final File actualFile = getFile(ExistingFilename + Ext1);

    final String expected = ExistingContent;
    final String actual   = new String(Util.loadFile(actualFile, false));
    assertEquals(expected, actual);
  }

  @Test
  public void loadExistingAuto() throws IOException {
    TestUtil.writeFile(getFile(ExistingFilename + Ext1), ExistingContent);
    final File actualFile = getFile(ExistingFilename);

    final String expected = ExistingContent;
    final String actual   = new String(Util.loadFile(actualFile, true));
    assertEquals(expected, actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadAmbiguous() throws Throwable {
    TestUtil.writeFile(getFile(ExistingFilename + Ext1), ExistingContent);
    TestUtil.writeFile(getFile(ExistingFilename + Ext2), ExistingContent);
    try {
      final File actualFile = getFile(ExistingFilename);
      Util.loadFile(actualFile, true);
    } catch (final Exception e) {
      throw e.getCause();
    }
  }
}
