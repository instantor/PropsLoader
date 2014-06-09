package com.ferega.props.japi;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

final class Util {
  public static File createFile(final File base, final String ... path) {
    final int len = path.length;
    if (len == 0) {
      return base;
    } else {
      final String head = path[0];
      final String[] tail = Arrays.copyOfRange(path, 1, len);
      return createFile(new File(base, head), tail);
    }
  }

  public static Properties loadPropsFromFile(final File file) {
    try (final FileInputStream fis = new FileInputStream(file)) {
      final Properties props = new Properties();
      props.load(fis);
      return props;
    } catch (Exception e) {
      throw new RuntimeException(String.format("An error occured while trying to read properties from file %s", file.getAbsolutePath()), e);
    }
  }

  @SafeVarargs
  public static Optional<String> orElseOpt(final Optional<String> ... optList) {
    final int len = optList.length;
    if (len == 0) {
      return Optional.empty();
    } else {
      final Optional<String> head = optList[0];
      final Optional<String>[] tail = Arrays.copyOfRange(optList, 1, len);

      if (head.isPresent()) {
        return head;
      } else {
        return orElseOpt(tail);
      }
    }
  }
}
