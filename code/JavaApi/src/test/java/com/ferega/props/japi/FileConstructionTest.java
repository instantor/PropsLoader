package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FileConstructionTest {
  private static final String root = File.listRoots()[0].toString();
  private static final String baseName = "baseName";
  private static final String path1 = "path1";
  private static final String path2 = "path2";

  @Test
  public void createBasicFile() throws IOException {
    final File expected = new File(baseName);
    final File actual   = Util.constructFile(new File(baseName));
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void constructFileWithSinglePath() throws IOException {
    final File expected = new File(new File(baseName), path1);
    final File actual   = Util.constructFile(new File(baseName), path1);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void constructFileWithLongPath() throws IOException {
    final File expected = new File(new File(new File(baseName), path1), path2);
    final File actual   = Util.constructFile(new File(baseName), path1, path2);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void deconstructFileWithEmptyPath() {
    final String[] expected = new String[] {};
    final String[] actual   = Util.deconstructFile(new File(""));
  assertArrayEquals(expected, actual);
  }

  @Test
  public void deconstructFileWithSinglePath() {
    final String[] expected = new String[] { baseName };
    final String[] actual   = Util.deconstructFile(new File(baseName));
    assertArrayEquals(expected, actual);
  }

  @Test
  public void deconstructFileWithLongPath() {
    final String[] expected = new String[] { baseName, path1, path2 };
    final String[] actual   = Util.deconstructFile(new File(new File(new File(baseName), path1), path2));
    assertArrayEquals(expected, actual);
  }

  @Test
  public void deconstructFileWithLongRoot() {
    final String[] expected = new String[] { root, path1, path2 };
    final String[] actual   = Util.deconstructFile(new File(new File(new File(root), path1), path2));
    assertArrayEquals(expected, actual);
  }
}
