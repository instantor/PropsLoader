package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FileConstructionTest {
  private static final String baseName = "baseName";
  private static final String path1 = "path1";
  private static final String path2 = "path2";

  @Test
  public void createBasicFile() throws IOException {
    final File expected = new File(baseName);
    final File actual   = Util.createFile(new File(baseName));
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void createFileWithSinglePath() throws IOException {
    final File expected = new File(new File(baseName), path1);
    final File actual   = Util.createFile(new File(baseName), path1);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void createFileWithLongPath() throws IOException {
    final File expected = new File(new File(new File(baseName), path1), path2);
    final File actual   = Util.createFile(new File(baseName), path1, path2);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }
}
