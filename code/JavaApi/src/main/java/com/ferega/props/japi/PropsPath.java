package com.ferega.props.japi;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropsPath {
  private final String base;
  private final String[] partList;

  private final static Pattern ResolvablePattern = Pattern.compile("^\\$([a-zA-Z0-9_.]+)\\$$");
  private final static String UnresolvableErrMsg = "An error occured while resolving path part \"%s\": key \"%s\" not found in properties";
  private static String resolvePart(final Properties props, final String part) {
    final String result;

    final Matcher partMatcher = ResolvablePattern.matcher(part);
    if (partMatcher.matches() && partMatcher.groupCount() == 1) {
      final String key = partMatcher.group(1);
      final Optional<String> valueOpt = Optional.ofNullable(props.getProperty(key));
      result = valueOpt.orElseThrow(() -> new IllegalArgumentException(String.format(UnresolvableErrMsg, part, key)));
    } else {
      result = part;
    }

    return result;
  }

  public PropsPath(final String ... path) {
    final int len = path.length;
    if (len < 1) {
      throw new IllegalArgumentException("path must have at least one element");
    } else {
      this.base = path[0];
      this.partList = Arrays.copyOfRange(path, 1, len);
    }
  }

  public File toFile() {
    return Util.createFile(new File(base), partList);
  }

  public File resolve(Properties props) {
    final String resolvedBase = resolvePart(props, base);
    final String[] resolvedPartList = Arrays.stream(partList).map(part -> resolvePart(props, part)).toArray(String[]::new);
    return Util.createFile(new File(resolvedBase), resolvedPartList);
  }
}
