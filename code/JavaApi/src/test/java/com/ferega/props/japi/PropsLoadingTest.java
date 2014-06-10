package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsLoadingTest {
  private static final String prefix = "props-test";
  private static final String suffix = ".config";
  private static final Properties validProps = new Properties(); static {
    validProps.setProperty("name", "Brunhilda Schlöndorff");
    validProps.setProperty("occupation", "actress");
  }

  private static final byte[] malformedProps = ("malformed = " + '\\' + "uFE\n").getBytes();

  @Test
  public void load() throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    validProps.store(fos, null);
    fos.close();

    final Properties expected = validProps;
    final Properties actual   = Util.loadPropsFromFile(file);
    assertEquals(expected, actual);

    file.delete();
  }

  @Test(expected = FileNotFoundException.class)
  public void loadMissing() throws Throwable {
    final File file = new File(UUID.randomUUID().toString());
    try {
      Util.loadPropsFromFile(file);
    } catch (Exception e) {
      throw e.getCause();  // We want to see the inner exception
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void loadMalformed() throws Throwable {
    final File file = File.createTempFile(prefix, suffix);
    final OutputStream fos = new FileOutputStream(file);
    fos.write(malformedProps);
    fos.close();

    try {
      Util.loadPropsFromFile(file);
    } catch (Exception e) {
      throw e.getCause();  // We want to see the inner exception
    } finally {
      file.delete();
    }
  }
}
