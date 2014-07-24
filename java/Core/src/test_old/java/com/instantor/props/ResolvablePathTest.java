package com.instantor.props;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResolvablePathTest {
  private static final String UserHomeChar   = "~";
  private static final String UserHomeKey   = "user.home";
  private static final String UserHomeValue = "my user home";
  private static final String prefix = "prefix_";
  private static final String suffix = "_suffix";
  private static final String path1  = "path1";
  private static final String path2  = "path2";
  private static final String path3  = "path3";
  private static final String path1Resolved = "path1Resolved";
  private static final Properties props = new Properties(); static {
    props.setProperty(UserHomeKey, UserHomeValue);
    props.setProperty(path1, path1Resolved);
  }

  @Test
  public void fileOne() throws IOException {
    final ResolvablePath path = new ResolvablePath(path1);
    final File expected = new File(path1);
    final File actual   = path.toFile();
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void fileMany() throws IOException {
    final ResolvablePath path = new ResolvablePath(path1, path2, path3);
    final File expected = new File(new File(new File(path1), path2), path3);
    final File actual   = path.toFile();
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolveSuccess() throws IOException {
    final ResolvablePath path = new ResolvablePath("$" + path1 + "$", path2, path3);
    final File expected = new File(new File(new File(path1Resolved), path2), path3);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolveNothing() throws IOException {
    final ResolvablePath path = new ResolvablePath(path1, path2, path3);
    final File expected = new File(new File(new File(path1), path2), path3);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveFail() throws IOException {
    new ResolvablePath(path1, "$" + path2 + "$", path3).resolve(props);
  }

  @Test
  public void resolvePrefix() throws IOException {
    final ResolvablePath path = new ResolvablePath(prefix + "$" + path1 + "$");
    final File expected = new File(prefix + path1Resolved);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolveSuffix() throws IOException {
    final ResolvablePath path = new ResolvablePath("$" + path1 + "$" + suffix);
    final File expected = new File(path1Resolved + suffix);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolvePrefixAndSuffix() throws IOException {
    final ResolvablePath path = new ResolvablePath(prefix + "$" + path1 + "$" + suffix);
    final File expected = new File(prefix + path1Resolved + suffix);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolveTildeAtFirstPosition() throws IOException {
    final ResolvablePath path = new ResolvablePath(UserHomeChar, path1, path2);
    final File expected = new File(new File(new File(UserHomeValue), path1), path2);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test
  public void resolveTildeAtNonfirstPosition() throws IOException {
    final ResolvablePath path = new ResolvablePath(path1, UserHomeChar, path2);
    final File expected = new File(new File(new File(path1), UserHomeChar), path2);
    final File actual   = path.resolve(props);
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }

  @Test(expected = IllegalArgumentException.class)
  public void concatenateNothing() {
    ResolvablePath.concatenate();
  }

  @Test
  public void concatenateCombinations() throws IOException {
    for (char c1 = '0'; c1 <= '4'; c1++) {
      for (char c2 = '0'; c2 <= '4'; c2++) {
        for (char c3 = '0'; c3 <= '4'; c3++) {
          for (char c4 = '1'; c4 <= '4'; c4++) {
            final String command =
                String.valueOf(c1) +
                String.valueOf(c2) +
                String.valueOf(c3) +
                String.valueOf(c4);
            final File expected = getConcatinateExpected(command);
            final File actual   = getConcatinateActual(command).toFile();
            assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
          }
        }
      }
    }
  }

  private File getConcatinateExpected(final String command) {
    File f = null;
    for (int n = 0; n < 4; n++) {
      switch(command.charAt(n)) {
        case '1': case '3':
          if (f == null) {
            f = new File(path1);
          } else {
            f = new File(f, path1);
          }
          break;
        case '2': case '4':
          if (f == null) {
            f = new File(new File(path1), path2);
          } else {
            f = new File(new File(f, path1), path2);
          }
          break;
        default:
          break;
      }
    }
    return f;
  }

  private ResolvablePath getConcatinateActual(final String command) {
    final List<Object> elements = new ArrayList<>();
    for (int n = 0; n < 4; n++) {
      switch(command.charAt(n)) {
        case '1':
          elements.add(path1);
          break;
        case '2':
          elements.add(path1 + "/" + path2);
          break;
        case '3':
          elements.add(new File(path1));
          break;
        case '4':
          elements.add(new File(new File(path1), path2));
          break;
        default:
          break;
      }
    }
    final Object[] elemArr = elements.toArray();
    return ResolvablePath.concatenate(elemArr);
  }

  @Test
  public void concatenateSingleFile() throws IOException {
    final ResolvablePath path = ResolvablePath.concatenate(new File(path1), new File(path2));
    final File expected = new File(new File(path1), path2);
    final File actual   = path.toFile();
    assertEquals(expected.getCanonicalPath(), actual.getCanonicalPath());
  }
}
