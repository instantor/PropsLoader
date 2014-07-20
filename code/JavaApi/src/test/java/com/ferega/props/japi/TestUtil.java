package com.ferega.props.japi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import org.junit.rules.TemporaryFolder;

public class TestUtil {
  static File writeFile(final File file, final String content) {
    try {
      if (file.exists()) {
        file.delete();
      }

      final File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }

      final FileWriter fw = new FileWriter(file);
      fw.write(content);
      fw.close();
      return file;
    } catch (final IOException e) {
      throw new RuntimeException("Error in tests!", e);
    }
  }

  static String readFile(final File file) {
    try {
      final byte[] body = Files.readAllBytes(file.toPath());
      return new String(body);
    } catch (final IOException e) {
      throw new RuntimeException("Error in tests!", e);
    }
  }

  static String randomString(final int length) {
    final Random r = new Random();
    final StringBuilder sb = new StringBuilder();
    for (int n = 1; n <= length; n++) {
      final char c = (char)(r.nextInt(26) + 'A');
      sb.append(c);
    }
    return sb.toString();
  }

  static TemporaryFolder createTemp() {
    try {
      final TemporaryFolder temp = new TemporaryFolder();
      temp.create();
      return temp;
    } catch (final IOException e) {
      throw new RuntimeException("Error in tests!", e);
    }
  }
}
