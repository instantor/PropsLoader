package com.ferega.props.japi;

import java.io.File;
import java.nio.file.Files;
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

  public static byte[] loadFile(final File file, final boolean autoExt) {
    try {
      final File foundFile;
      if (autoExt) {
        final File parent = file.getParentFile();
        final String name = file.getName();
        final File[] foundFileList = parent.listFiles((p, n) -> n.startsWith(name));

        if (foundFileList.length > 0) {
          foundFile = foundFileList[0];
        } else {
          throw new IllegalArgumentException(String.format("File with prefix \"%s\" not found!", name));
        }
      } else {
        foundFile = file;
      }
      return Files.readAllBytes(foundFile.toPath());
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("An error occured while trying to reading file %s", file.getAbsolutePath()), e);
    }
  }

  public static Map<String, String> propsToMap(Properties props) {
    final Map<String, String> map = new HashMap<>();
    for (final String name: props.stringPropertyNames()) {
      map.put(name, props.getProperty(name));
    }
    return map;
  }
}
