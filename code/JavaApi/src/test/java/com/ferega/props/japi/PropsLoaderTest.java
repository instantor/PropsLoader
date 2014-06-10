package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsLoaderTest {
  private static final String prefix    = "props-test";
  private static final String suffix    = ".config";
  private static final String homeKey   = "user.home";
  private static final String homeValue = System.getProperty("user.home");
  private static final String fileKey   = "file.key";
  private static final String fileValue = "filevalue";
  private static final Optional<String> optEmpty = Optional.empty();
  private static final Properties props = new Properties(); static {
    props.setProperty(fileKey, fileValue);
  }

  @Test(expected = NoSuchElementException.class)
  public void getMissing() throws Throwable {
    try {
      new PropsLoader(false).get(homeKey);
    } catch (Exception e) {
      throw e.getCause();
    }
  }

  @Test
  public void optMissing() throws IOException {
    final PropsLoader pl = new PropsLoader(false);
    final Optional<String> expected = optEmpty;
    final Optional<String> actual   = pl.opt(homeKey);
    assertEquals(expected, actual);
  }

  @Test
  public void getExistingSystem() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final String expected = homeValue;
    final String actual   = pl.get(homeKey);
    assertEquals(expected, actual);
  }

  @Test
  public void optExistingSystem() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final Optional<String> expected = Optional.of(homeValue);
    final Optional<String> actual   = pl.opt(homeKey);
    assertEquals(expected, actual);
  }

  @Test
  public void getExistingFile() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
    fos.close();

    final PropsLoader pl = new PropsLoader(true, new PropsPath(file.getCanonicalPath()));
    final String expected = fileValue;
    final String actual   = pl.get(fileKey);
    assertEquals(expected, actual);

    file.delete();
  }

  @Test
  public void optExistingFile() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
    fos.close();

    final PropsLoader pl = new PropsLoader(true, new PropsPath(file.getCanonicalPath()));
    final Optional<String> expected = Optional.of(fileValue);
    final Optional<String> actual   = pl.opt(fileKey);
    assertEquals(expected, actual);

    file.delete();
  }
}
