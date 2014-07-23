package com.ferega.props.japi;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ResolvablePath {
  private final String base;
  private final String[] partList;

  public final static String UserHome = "~";
  private final static Pattern ResolvablePattern = Pattern.compile("^(.*)\\$([-\\.\\w]+)\\$(.*)$");
  private static String resolvePart(final Properties props, final String part) {
    final String result;

    final Matcher partMatcher = ResolvablePattern.matcher(part);
    if (partMatcher.matches() && partMatcher.groupCount() == 3) {
      final String prefix = partMatcher.group(1);
      final String key    = partMatcher.group(2);
      final String suffix = partMatcher.group(3);
      final Optional<String> valueOpt = Optional.ofNullable(props.getProperty(key));
      final String value = valueOpt.orElseThrow(() -> new IllegalArgumentException(String.format(
              "An error occured while resolving path part \"%s\": key \"%s\" not found in properties", part, key)));
      result = prefix + value + suffix;
    } else {
      result = part;
    }

    return result;
  }

  public ResolvablePath(final String base, final String ... partList) {
    this.base     = base;
    this.partList = partList;
  }

  public ResolvablePath(final ResolvablePath base, final String ... newPartList) {
    this(base.getBase(),
        Stream.concat(
            Arrays.stream(base.partList),
            Arrays.stream(newPartList)
        ).toArray(String[]::new)
    );
  }

  public static ResolvablePath concatenate(final Object... elements) {
    final int len = elements.length;
    if (len == 0) {
      throw new IllegalArgumentException("At least one path element must be specified!");
    }

    final List<String> partList = new ArrayList<>();
    for (final Object element : elements) {
      final File file;
      if (element instanceof String) {
        file = new File((String)element);
      } else if (element instanceof File) {
        file = (File)element;
      } else {
        throw new IllegalArgumentException(String.format("Elements must be instances of either String of File. %s found!", element.getClass().getName()));
      }
      final List<String> newPartList = Arrays.asList(Util.deconstructFile(file));
      partList.addAll(newPartList);
    }

    final String[] partArray = partList.toArray(new String[0]);
    final String head = partArray[0];
    final String[] tail = Arrays.copyOfRange(partArray, 1, partArray.length);
    return new ResolvablePath(head, tail);
  }

  public String getBase() {
    return this.base;
  }

  public String[] getPartList() {
    return this.partList;
  }

  public File toFile() {
    return Util.constructFile(new File(this.base), this.partList);
  }

  public File resolve(final Properties props) {
    final String expandedBase = (UserHome.equals(this.base))
            ? "$user.home$"
            : this.base;

    final String resolvedBase = resolvePart(props, expandedBase);
    final String[] resolvedPartList = Arrays.stream(this.partList)
        .map(part -> resolvePart(props, part))
        .toArray(String[]::new);
    return Util.constructFile(new File(resolvedBase), resolvedPartList);
  }

  public File resolve() {
    return resolve(System.getProperties());
  }
}
