package com.ferega.props.japi;

public class PropsLoaderFactory {
  public static final String ConfigFolder = ".props";
  public static final String ConfigFile   = "_";

  public static final String BasicPathPattern = ResolvablePath.UserHome + "/" + ConfigFolder + "/%1$s/" + ConfigFile;
  public static final String BranchPathPattern  = ResolvablePath.UserHome + "/" + ConfigFolder + "/%1$s_$%1$s.branch$/" + ConfigFile;
  public static final boolean DefaultAutoExt    = true;

  private final String projectName;
  private final String pathPattern;
  private final boolean autoExt;

  public PropsLoaderFactory(final String projectName) {
    this.projectName = projectName;
    this.pathPattern = BranchPathPattern;
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
    final ResolvablePath path = ResolvablePath.concatenate(resolvablePath);
    return new PropsLoader(path, this.autoExt);
  }

  public PropsLoaderFactory setProjectName(final String projectName) {
    return new PropsLoaderFactory(projectName, this.pathPattern, this.autoExt);
  }

  public PropsLoaderFactory setPathPattern(final String pathPattern) {
    return new PropsLoaderFactory(this.projectName, pathPattern, this.autoExt);
  }

  public PropsLoaderFactory setBasicPathPattern() {
    return new PropsLoaderFactory(this.projectName, BasicPathPattern, this.autoExt);
  }

  public PropsLoaderFactory setBranchPathPattern() {
    return new PropsLoaderFactory(this.projectName, BranchPathPattern, this.autoExt);
  }

  public PropsLoaderFactory setAutoExt(final boolean autoExt) {
    return new PropsLoaderFactory(this.projectName, this.pathPattern, autoExt);
  }
}
