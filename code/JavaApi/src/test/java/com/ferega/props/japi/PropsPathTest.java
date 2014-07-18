package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsPathTest {
  private static final String prefix = "prefix_";
  private static final String suffix = "_suffix";
  private static final String path1 = "path1";
  private static final String path2 = "path2";
  private static final String path3 = "path3";
  private static final String path1Resolved = "path1Resolved";
  private static final Properties props = new Properties(); static {
    props.setProperty(path1, path1Resolved);
  }

  @Test
  public void fileOne() throws IOException {
    final File expected    = new File(path1);
    final ResolvablePath actual = new ResolvablePath(path1);
    assertEquals(expected.getCanonicalPath(), actual.toFile().getCanonicalPath());
  }

  @Test
  public void fileMany() throws IOException {
    final File expected    = new File(new File(new File(path1), path2), path3);
    final ResolvablePath actual = new ResolvablePath(path1, path2, path3);
    assertEquals(expected.getCanonicalPath(), actual.toFile().getCanonicalPath());
  }

  @Test
  public void resolveSuccess() throws IOException {
    final File expected    = new File(new File(new File(path1Resolved), path2), path3);
    final ResolvablePath actual = new ResolvablePath("$"+path1+"$", path2, path3);
    assertEquals(expected.getCanonicalPath(), actual.resolve(props).getCanonicalPath());
  }

  @Test
  public void resolveNothing() throws IOException {
    final File expected    = new File(new File(new File(path1), path2), path3);
    final ResolvablePath actual = new ResolvablePath(path1, path2, path3);
    assertEquals(expected.getCanonicalPath(), actual.resolve(props).getCanonicalPath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveFail() throws IOException {
    new ResolvablePath(path1, "$"+path2+"$", path3).resolve(props);
  }

  @Test
  public void resolvePrefix() throws IOException {
    final File expected    = new File(prefix + path1Resolved);
    final ResolvablePath actual = new ResolvablePath(prefix + "$" + path1 + "$");
    assertEquals(expected.getCanonicalPath(), actual.resolve(props).getCanonicalPath());
  }

  @Test
  public void resolveSuffix() throws IOException {
    final File expected    = new File(path1Resolved + suffix);
    final ResolvablePath actual = new ResolvablePath("$" + path1 + "$" + suffix);
    assertEquals(expected.getCanonicalPath(), actual.resolve(props).getCanonicalPath());
  }

  @Test
  public void resolvePrefixAndSuffix() throws IOException {
    final File expected    = new File(prefix + path1Resolved + suffix);
    final ResolvablePath actual = new ResolvablePath(prefix + "$" + path1 + "$" + suffix);
    assertEquals(expected.getCanonicalPath(), actual.resolve(props).getCanonicalPath());
  }
}
