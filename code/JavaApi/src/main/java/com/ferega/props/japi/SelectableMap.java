package com.ferega.props.japi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectableMap extends HashMap<String, String> {
  private static final long serialVersionUID = 1L;

  public SelectableMap() {
    super();
  }

  public SelectableMap(int initialCapacity) {
    super(initialCapacity);
  }

  public SelectableMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  public SelectableMap(Map<? extends String,? extends String> m) {
    super(m);
  }

  public Set<String> prefixes() {
    return this.keySet().stream()
      .filter(k ->  k.contains("."))
      .map(k -> k.split("\\.")[0])
      .collect(Collectors.toSet());
  }

  public SelectableMap select(final String prefix) {
    final String fullPrefix = prefix.endsWith(".") ? prefix : prefix + ".";

    final SelectableMap selection = new SelectableMap();
    for (final Map.Entry<String, String> entry: this.entrySet()) {
      final String key = entry.getKey();
      if (key.startsWith(fullPrefix)) {
        final String val = entry.getValue();
        selection.put(key.substring(fullPrefix.length()), val);
      }
    }
    return selection;
  }

  public Map<String, SelectableMap> selectAll() {
    final Map<String, SelectableMap> map = new HashMap<String, SelectableMap>();
    prefixes().stream()
      .forEach(prefix -> map.put(prefix, select(prefix)));
    return map;
  }}
