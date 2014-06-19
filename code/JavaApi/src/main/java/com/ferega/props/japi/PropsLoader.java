package com.ferega.props.japi;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropsLoader {
  private final Optional<Properties> sysProps;
  private final List<Properties> filePropsList;
  private Map<String, String> memoizedMap = null;

  private static Optional<String> resolve(final Optional<Properties> pOpt, final String key) {
    return pOpt.flatMap(p -> Optional.ofNullable(p.getProperty(key)));
  }

  private static Optional<String> resolve(final List<Properties> sOpt, final String key) {
    return sOpt.stream()
      .map(s -> Optional.ofNullable(s.getProperty(key)))
      .filter(o -> o.isPresent())
      .map(o -> o.get())
      .findFirst();
  }

  private Map<String, String> calculateMap() {
    final Map<String, String> map = new HashMap<>();
    final ListIterator<Properties> li = filePropsList.listIterator(filePropsList.size());
    while (li.hasPrevious()) {
      map.putAll(Util.propsToMap(li.previous()));
    }
    sysProps.ifPresent(props -> map.putAll(Util.propsToMap(props)));
    return Collections.unmodifiableMap(map);
  }

  public PropsLoader(
      final boolean useSystemProps,
      final PropsPath ... resolvablePathList) throws IOException {
    final Stream<PropsPath> pathStream = Arrays.stream(resolvablePathList);
    final Stream<File> fileStream;

    if (useSystemProps) {
      this.sysProps = Optional.of(System.getProperties());
      fileStream = pathStream.map(resolvablePath -> resolvablePath.resolve(this.sysProps.get()));
    } else {
      this.sysProps = Optional.empty();
      fileStream = pathStream.map(resolvablePath -> resolvablePath.toFile());
    }

    this.filePropsList = fileStream
        .map(file -> Util.loadPropsFromFile(file))
        .collect(Collectors.toList());
  }

  public String get(final String key) {
    try {
      return opt(key).get();
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(String.format("Key \"%s\" not found in any of the properties collection.", key), e);
    }
  }

  public Optional<String> opt(final String key) {
    final Optional<String> sysVal = resolve(this.sysProps, key);
    final Optional<String> fileVal = resolve(this.filePropsList, key);
    return Util.orElseOpt(sysVal, fileVal);
  }

  public Map<String, String> getMap() {
    if (memoizedMap != null) {
      return memoizedMap;
    } else {
      memoizedMap = calculateMap();
      return memoizedMap;
    }
  }

  public Map<String, String> select(final String prefix) {
    final String fullPrefix = prefix.endsWith(".") ? prefix : prefix + ".";

    final Map<String, String> selection = new HashMap<>();
    for (final Map.Entry<String, String> entry: getMap().entrySet()) {
      final String key = entry.getKey();
      if (key.startsWith(fullPrefix)) {
        final String val = entry.getValue();
        selection.put(key.substring(fullPrefix.length()), val);
      }
    }
    return selection;
  }
}
