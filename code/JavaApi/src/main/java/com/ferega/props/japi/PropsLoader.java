package com.ferega.props.japi;

import java.io.*;
import java.util.*;

public class PropsLoader {
  private final byte[] source;
  private Map<String, String> propsMap;

  public PropsLoader(final ResolvablePath resolvablePath, final boolean autoExt) {
    final File resolvedPath = resolvablePath.resolve();
    this.source = Util.loadFile(resolvedPath, autoExt);
    this.propsMap = null;
  }

  public String get(final String key) {
    try {
      return opt(key).get();
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException(String.format("Key \"%s\" not found in any of the properties collection.", key), e);
    }
  }

  public Optional<String> opt(final String key) {
    return Optional.ofNullable(toMap().get(key));
  }

  public Map<String, String> toMap() {
    if (propsMap == null) {
      final Map<String, String> map = Util.propsToMap(toProps());
      this.propsMap = map;
      return map;
    } else {
      return propsMap;
    }
  }

  public Properties toProps() {
    try {
      final Properties props = new Properties();
      props.load(toInputStream());
      return props;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public byte[] toByteArray() {
    return source;
  }

  public ByteArrayInputStream toInputStream() {
    return new ByteArrayInputStream(source);
  }

  public String toString(final String encoding) {
    try {
      return new String(source, encoding);
    }
    catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return toString("ISO-8859-1");
  }
}
