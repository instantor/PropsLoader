package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.Rule;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoadFileTest {
  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  public static final String Ext = ".txt";
  public static final String MissingFilename = "missing";
  public static final String ExistingFilename = "existing";
  public static final String ExistingContent = "Existing content!";

  private File getFile(final String name) {
    return new File(testFolder.getRoot(), name);
  }

  @Test(expected = NoSuchFileException.class)
  public void loadMissingExplicit() throws Throwable {
    final File file = getFile(MissingFilename);
    try {
      Util.loadFile(file, false);
    } catch (Exception e) {
      throw e.getCause();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadMissingAuto() throws Throwable {
    final File file = getFile(MissingFilename + Ext);
    try {
      Util.loadFile(file, true);
    } catch (Exception e) {
      throw e.getCause();
    }
  }

  @Test
  public void loadExistingExplicit() throws IOException {
    final File file = getFile(ExistingFilename + Ext);
    final FileWriter fw = new FileWriter(file);
    fw.write(ExistingContent);
    fw.close();

    final File actualFile = getFile(ExistingFilename);

    final String expected = ExistingContent;
    final String actual   = new String(Util.loadFile(actualFile, false));
    assertEquals(expected, actual);
  }

  @Test
  public void loadExistingAuto() throws IOException {
//    final File file = getFile(ExistingFilename + Ext);
//    final FileWriter fw = new FileWriter(file);
//    fw.write(ExistingContent);
//    fw.close();
//
//    final byte[] body = Util.loadFile(getFile(ExistingFilename), true);
//    final String expected = ;
//    final String actual   = new String(body);
//    assertEquals(expected, actual);
  }
}
