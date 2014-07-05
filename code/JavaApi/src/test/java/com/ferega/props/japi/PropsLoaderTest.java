package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;
import java.util.Map;
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
  private static final String sysKey1   = "user.home"; private static final String sysVal1 = System.getProperty(sysKey1);
  private static final String sysKey2   = "os.name";   private static final String sysVal2 = System.getProperty(sysKey2);
  private static final String fileKey1  = "file.key";  private static final String fileVal1 = "filevalue";
  private static final String fileKey2  = "user.home"; private static final String fileVal2 = "fileuserhome";
  private static final Optional<String> optEmpty = Optional.empty();
  private static final Properties props = new Properties(); static {
    props.setProperty(fileKey1, fileVal1);
    props.setProperty(fileKey2, fileVal2);
  }

  @Test(expected = NoSuchElementException.class)
  public void getMissing() throws Throwable {
    try {
      new PropsLoader(false).get(sysKey1);
    } catch (Exception e) {
      throw e.getCause();
    }
  }

  @Test
  public void optMissing() throws IOException {
    final PropsLoader pl = new PropsLoader(false);
    final Optional<String> expected = optEmpty;
    final Optional<String> actual   = pl.opt(sysKey1);
    assertEquals(expected, actual);
  }

  @Test
  public void getExistingSystem() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final String expected = sysVal1;
    final String actual   = pl.get(sysKey1);
    assertEquals(expected, actual);
  }

  @Test
  public void optExistingSystem() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final Optional<String> expected = Optional.of(sysVal1);
    final Optional<String> actual   = pl.opt(sysKey1);
    assertEquals(expected, actual);
  }

  @Test
  public void getExistingFile() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
    fos.close();

    final PropsLoader pl = new PropsLoader(true, new PropsPath(file.getCanonicalPath()));
    final String expected = fileVal1;
    final String actual   = pl.get(fileKey1);
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
    final Optional<String> expected = Optional.of(fileVal1);
    final Optional<String> actual   = pl.opt(fileKey1);
    assertEquals(expected, actual);

    file.delete();
  }

  @Test
  public void getMultiple() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
    fos.close();

    final PropsLoader pl = new PropsLoader(true, new PropsPath(file.getCanonicalPath()));

    final String expected1 = sysVal1;
    final String actual1   = pl.get(sysKey1);
    assertEquals(expected1, actual1);

    final String expected2 = fileVal1;
    final String actual2   = pl.get(fileKey1);
    assertEquals(expected2, actual2);

    final String expected3 = sysVal2;
    final String actual3   = pl.get(sysKey2);
    assertEquals(expected3, actual3);

    final String expected4 = sysVal1;
    final String actual4   = pl.get(fileKey2);
    assertEquals(expected4, actual4);

    file.delete();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void mapModification() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final Map<String, String> map = pl.toMap();
    map.put("asdf", "zxcv");
  }

  @Test
  public void getMap() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    props.store(fos, null);
    fos.close();

    final PropsLoader pl = new PropsLoader(true, new PropsPath(file.getCanonicalPath()));
    final Map<String, String> map = pl.toMap();

    final String expected1 = sysVal1;
    final String actual1   = map.get(sysKey1);
    assertEquals(expected1, actual1);

    final String expected2 = fileVal1;
    final String actual2   = map.get(fileKey1);
    assertEquals(expected2, actual2);

    final String expected3 = sysVal2;
    final String actual3   = map.get(sysKey2);
    assertEquals(expected3, actual3);

    final String expected4 = sysVal1;
    final String actual4   = map.get(fileKey2);
    assertEquals(expected4, actual4);

    file.delete();
  }

  @Test
  public final void selection() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final Map<String, String> map = pl.select("user");

    final String expected = sysVal1;
    final String actual   = map.get("home");
    assertEquals(expected, actual);
  }

  @Test
  public final void selectionWithDot() throws IOException {
    final PropsLoader pl = new PropsLoader(true);
    final Map<String, String> map = pl.select("user.");

    final String expected = sysVal1;
    final String actual   = map.get("home");
    assertEquals(expected, actual);
  }
}
