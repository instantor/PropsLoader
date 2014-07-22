package com.ferega.props.japi;

import java.io.*;
import java.util.*;

public class PropsLoader {
  private static final String DefaultConfig = ".props";

  private final File location;
  private final byte[] source;
  private Map<String, String> propsMap;

  public static PropsLoader loadGlobal() {
    return PropsLoader.load("global", false);
  }

  public static PropsLoader load(final String projectName) {
      return load(projectName, true);
    }

  public static PropsLoader load(final String projectName, final boolean useBranch) {
    final PropsLoaderFactory propsLoaderFactory = new PropsLoaderFactory(projectName);

    return (useBranch
      ? propsLoaderFactory
      : propsLoaderFactory.setPathPattern("~/" + DefaultConfig + "/%1$s/_")).build();
  }

  public static String getServerAlias() {
    return loadGlobal().resolve("serverAlias").toString();
  }

  public PropsLoader(final ResolvablePath resolvablePath, final boolean autoExt) {
    final File resolvedPath = resolvablePath.resolve();
    this.location = resolvedPath;
    this.source   = Util.loadFile(resolvedPath, autoExt);
    this.propsMap = null;
  }

  public PropsLoader resolve(final String key) {
    final String value = get(key);
    final File baseFolder = this.location.getParentFile();
    final ResolvablePath path;

    if (".".equals(value)) {  // Local
      final File file = new File(baseFolder, key);
      path = ResolvablePath.path(file);
    } else if (value.startsWith("/")) { // Absolute
      path = ResolvablePath.path(value.substring(1) + "/" + key);
    } else { // From .config
      path = new ResolvablePath("~", DefaultConfig, value, key);
    }
    return new PropsLoader(path, true);
  }

  public String get(final String key) {
    try {
      return opt(key).get();
    } catch (final NoSuchElementException e) {
      throw new IllegalArgumentException(String.format("Key \"%s\" not found", key), e);
    }
  }

  public int getInt(final String key) {
    final int result;
    final String value = get(key);
    try {
      result = Integer.parseInt(value);
    }
    catch (final NumberFormatException e) {
      throw new IllegalArgumentException(String.format("Key \"%s\" with value \"%s\" cannot be cast as int!", key, value), e);
    }
    return result;
  }

  public Optional<String> opt(final String key) {
    return Optional.ofNullable(toMap().get(key));
  }

  public OptionalInt optInt(final String key) {
    final OptionalInt result;
    final Optional<String> value = opt(key);
    if (value.isPresent()) {
      try {
        result = OptionalInt.of(Integer.parseInt(value.get()));
      }
      catch (final NumberFormatException e) {
        throw new IllegalArgumentException(String.format("Key \"%s\" with value \"%s\" cannot be cast as int!", key, value.get()), e);
      }
    } else {
      result = OptionalInt.empty();
    }
    return result;
  }

  public byte[] toByteArray() {
    return source;
  }

  public File toFile() {
    return this.location;
  }

  public ByteArrayInputStream toInputStream() {
    return new ByteArrayInputStream(source);
  }

  public Map<String, String> toMap() {
    if (this.propsMap == null) {
      this.propsMap = Util.propsToMap(toProps());
    }
    return this.propsMap;
  }

  public Properties toProps() {
    try {
      final Properties props = new Properties();
      props.load(toInputStream());
      return props;
    } catch (final IOException e) {
      throw new IllegalArgumentException("An error occured while creating props from PropsLoader!", e);
    }
  }

  public SelectableMap toSelectableMap() {
    return new SelectableMap(toMap());
  }

  @Override
  public String toString() {
    return toString("ISO-8859-1");
  }

  public String toString(final String encoding) {
    try {
      return new String(source, encoding).trim();
    }
    catch (final UnsupportedEncodingException e) {
      throw new IllegalArgumentException("Invalid encoding supplied to PropsLoader.toString!", e);
    }
  }
}
