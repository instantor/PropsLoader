package com.ferega.props.japi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PropsLoader {
  private final Map<String, String> propsMap;

  private static Map<String, String> collapseMap(final List<Properties> propsList) {
    final Map<String, String> map = new HashMap<>();
    final ListIterator<Properties> li = propsList.listIterator();
    while (li.hasNext()) {
      map.putAll(Util.propsToMap(li.next()));
    }
    return map;
  }

  public PropsLoader(final Map<String, String> propsMap) {
    this.propsMap = Collections.unmodifiableMap(propsMap);
  }

  public PropsLoader(
      final boolean useSystemProps,
      final PropsPath ... resolvablePathList) throws IOException {
    final Properties sysProps = System.getProperties();
    final List<Properties> filePropsList =
        Arrays.stream(resolvablePathList)
            .map(resolvablePath -> resolvablePath.resolve(sysProps))
            .map(file -> Util.loadPropsFromFile(file))
            .collect(Collectors.toList());

    final List<Properties> allPropsList = new ArrayList<Properties>();
    allPropsList.addAll(filePropsList);
    if (useSystemProps) {
      allPropsList.add(sysProps);
    }

    this.propsMap = Collections.unmodifiableMap(collapseMap(allPropsList));
  }

  public String get(final String key) {
    try {
      return opt(key).get();
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(String.format("Key \"%s\" not found in any of the properties collection.", key), e);
    }
  }

  public int getAsInt(final String key) {
    return Integer.parseInt(get(key));
  }

  public long getAsLong(final String key) {
    return Long.parseLong(get(key));
  }

  public boolean getAsBoolean(final String key) {
    return Boolean.parseBoolean(get(key));
  }

  public double getAsDouble(final String key) {
    return Double.parseDouble(get(key));
  }

  public Optional<String> opt(final String key) {
    return Optional.ofNullable(propsMap.get(key));
  }

  public Optional<Integer> optAsInt(final String key) {
    return opt(key).map(Integer::parseInt);
  }

  public Optional<Long> optAsLong(final String key) {
    return opt(key).map(Long::parseLong);
  }

  public Optional<Boolean> optAsBoolean(final String key) {
    return opt(key).map(Boolean::parseBoolean);
  }

  public Optional<Double> optAsDouble(final String key) {
    return opt(key).map(Double::parseDouble);
  }

  public Map<String, String> toMap() {
    return propsMap;
  }

  public Properties toProps() {
    final Properties props = new Properties();
    props.putAll(toMap());
    return props;
  }

  public ByteArrayInputStream toInputStream() throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    toProps().store(bos, "Stored by PropsLoader");
    final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    return bis;
  }

  public PropsLoader select(final String prefix) {
    final String fullPrefix = prefix.endsWith(".") ? prefix : prefix + ".";

    final Map<String, String> selection = new HashMap<>();
    for (final Map.Entry<String, String> entry: toMap().entrySet()) {
      final String key = entry.getKey();
      if (key.startsWith(fullPrefix)) {
        final String val = entry.getValue();
        selection.put(key.substring(fullPrefix.length()), val);
      }
    }
    return new PropsLoader(selection);
  }
}
