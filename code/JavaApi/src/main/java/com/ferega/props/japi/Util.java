package com.ferega.props.japi;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

final class Util {
  public static File constructFile(final File base, final String ... path) {
    final File result;

    final int len = path.length;
    if (len == 0) {
      result = base;
    } else {
      final String head = path[0];
      final String[] tail = Arrays.copyOfRange(path, 1, len);
      result = constructFile(new File(base, head), tail);
    }

    return result;
  }

  public static String[] deconstructFile(File file) {
    final List<String> partList = new ArrayList<String>();
    File remainingFile = file;
    boolean end = false;

    while (!end) {
      final File parentFile = remainingFile.getParentFile();
      final String name = (parentFile==null) ? remainingFile.toString() : remainingFile.getName();

      if (!name.isEmpty()) {
        partList.add(0, name);
      }
      remainingFile = parentFile;
      end = (parentFile==null);
    }

    return partList.toArray(new String[] {});
  }

  public static Properties loadPropsFromFile(final File file) {
    try (final FileInputStream fis = new FileInputStream(file)) {
      final Properties props = new Properties();
      props.load(fis);
      return props;
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("An error occured while trying to read properties from file %s", file.getAbsolutePath()), e);
    }
  }

  @SafeVarargs
  public static Optional<String> orElseOpt(final Optional<String> ... optList) {
    final Optional<String> result;

    final int len = optList.length;
    if (len == 0) {
      result = Optional.empty();
    } else {
      final Optional<String> head = optList[0];
      final Optional<String>[] tail = Arrays.copyOfRange(optList, 1, len);

      if (head.isPresent()) {
        result = head;
      } else {
        result = orElseOpt(tail);
      }
    }

    return result;
  }

  public static Map<String, String> propsToMap(Properties props) {
    final Map<String, String> map = new HashMap<>();
    for (final String name: props.stringPropertyNames()) {
      map.put(name, props.getProperty(name));
    }
    return map;
  }
}
