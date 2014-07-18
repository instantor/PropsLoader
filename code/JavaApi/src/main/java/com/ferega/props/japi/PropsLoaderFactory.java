package com.ferega.props.japi;

public class PropsLoaderFactory {
  public static final String DefaultPathPattern = "$user.home$/.config/%1$s_$%1$s$/%1$s";
  public static final boolean DefaultAutoExt    = true;

  private final String projectName;
  private final String pathPattern;
  private final boolean autoExt;

  public PropsLoaderFactory(final String projectName) {
    this.projectName = projectName;
    this.pathPattern = DefaultPathPattern;
    this.autoExt     = DefaultAutoExt;
  }

  public PropsLoaderFactory(
      final String projectName,
      final String pathPattern,
      final boolean autoExt) {
    this.projectName = projectName;
    this.pathPattern = pathPattern;
    this.autoExt     = autoExt;
  }

  public String getPathPattern() {
    return pathPattern;
  }

  public boolean getAutoExt() {
    return autoExt;
  }

  public PropsLoader build() {
    final String resolvablePath = (projectName != null) ? String.format(pathPattern, projectName) : pathPattern;
    final ResolvablePath path = new ResolvablePath(resolvablePath);
    return new PropsLoader(path, this.autoExt);
  }

  public PropsLoaderFactory setProjectName(final String projectName) {
    return new PropsLoaderFactory(projectName, this.pathPattern, this.autoExt);
  }

  public PropsLoaderFactory setPathPattern(final String pathPattern) {
    return new PropsLoaderFactory(this.projectName, pathPattern, this.autoExt);
  }

  public PropsLoaderFactory setAutoExt(final boolean autoExt) {
    return new PropsLoaderFactory(this.projectName, this.pathPattern, autoExt);
  }
}
