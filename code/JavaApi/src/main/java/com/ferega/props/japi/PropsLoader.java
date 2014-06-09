package com.ferega.props.japi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class PropsLoader {
  private final Optional<Properties> sysProps;
  private final Stream<Properties> filePropsList;

  private static Optional<String> resolve(final Optional<Properties> pOpt, final String key) {
    return pOpt.flatMap(p -> Optional.ofNullable(p.getProperty(key)));
  }

  private static Optional<String> resolve(final Stream<Properties> sOpt, final String key) {
    return sOpt
      .map(s -> Optional.ofNullable(s.getProperty(key)))
      .filter(o -> o.isPresent())
      .map(o -> o.get())
      .findFirst();
  }

  public PropsLoader(
      final boolean useSystemProps,
      final PropsPath ... resolvablePathList) throws IOException {
    final Stream<PropsPath> pathList = Arrays.stream(resolvablePathList);
    final Stream<File> fileList;

    if (useSystemProps) {
      this.sysProps = Optional.of(System.getProperties());
      fileList = pathList.map(resolvablePath -> resolvablePath.resolve(this.sysProps.get()));
    } else {
      this.sysProps = Optional.empty();
      fileList = pathList.map(resolvablePath -> resolvablePath.toFile());
    }

    this.filePropsList = fileList.map(file -> Util.loadPropsFromFile(file));
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
}
