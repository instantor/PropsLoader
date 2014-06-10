package com.ferega.props.japi;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

  public PropsPath(final String base, final String ... partList) {
    this.base     = base;
    this.partList = partList;
  }

  public PropsPath(final PropsPath base, final String ... newPartList) {
    this(base.getBase(),
        Stream.concat(
            Arrays.stream(base.partList),
            Arrays.stream(newPartList)
        ).toArray(String[]::new)
    );
  }

  public String getBase() {
    return this.base;
  }

  public String[] getPartList() {
    return this.partList;
  }

  public File toFile() {
    return Util.createFile(new File(this.base), this.partList);
  }

  public File resolve(final Properties props) {
    final String resolvedBase = resolvePart(props, this.base);
    final String[] resolvedPartList = Arrays.stream(this.partList).map(part -> resolvePart(props, part)).toArray(String[]::new);
    return Util.createFile(new File(resolvedBase), resolvedPartList);
  }
}
