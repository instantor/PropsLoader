package com.instantor.props;

import java.io.File;

import org.slf4j.Logger;

public class PropsLoaderFactory implements PropsFactoryResolver {
    private final Logger logger;
    private final String propsHome;

    private PropsLoaderFactory(final Logger logger) {
        this.logger = logger;
        propsHome = resolvePropsHome();
    }

    public static PropsLoaderFactory init(final Logger logger) {
        logger.trace("Initializing PropsLoaderFactory ...");
        return new PropsLoaderFactory(logger);
    }

    private String resolvePropsHome() {
        final String userHome = resolveProperty("user.home");
        return new File(userHome, ".props").getAbsolutePath();
    }

    private String resolveProperty(final String key) {
        final String value = System.getProperty(key);
        logger.trace("Resolving system property \"{}\": {}", key, value);
        if (value == null) throw new IllegalArgumentException("System property \"" + key + "\" was undefined!");
        return value;
    }

    public PropsResolver loadPure(final String projectName) {
        return loadBranch(projectName, null);
    }

    public PropsResolver loadBranch(final String projectName) {
        return loadBranch(projectName, projectName);
    }

    public PropsResolver loadBranch(final String projectName, final String branch) {
        final File file = findSingleFile(new File(propsHome, branch == null
                ? projectName + "_" + resolveProperty(branch)
                : projectName));

        logger.debug("Resolved file: {}", file);
        final PropsLoader propsResolver = new PropsLoaderImpl(logger, this, file);

        // Eagerly try to resolve all references, and fail early.
        for (final String key : propsResolver.toMap().keySet()) {
            try {
                final PropsLoader resolvedProps = propsResolver.resolve(key);
                resolvedProps.toByteArray();
                logger.info("  {} = {}", key, resolvedProps.toPath());
            } catch (final Exception e) {
                throw new RuntimeException(String.format(
                        "Could not resolve key \"%s\" with value \"%s\" from confing file %s",
                        key, propsResolver.get(key), propsResolver.toPath()));
            }
        }

        return propsResolver;
    }

    private File findSingleFile(final File file) {
        final File parent = file.getParentFile();
        final String name = file.getName();
        final File[] foundFileList = parent.listFiles((p, n) -> n.startsWith(name));
        switch (foundFileList.length) {
            case 0:
                throw new IllegalArgumentException(String.format("File with prefix \"%s\" not found!", name));
            case 1:
                return foundFileList[0];
            default:
                throw new IllegalArgumentException(String.format("Ambiguous resolution, more than one file with prefix \"%s\" was found!", name));
        }
    }

    // resolve callback from PropsLoaderImpl
    @Override
    public PropsLoader resolve(final File base, final String key, final String value) {
        final File resolvedFile = value.equals(".")
                ? findSingleFile(new File(base, key))
                : findSingleFile(new File(propsHome, value + "/" + key));

        return new PropsLoaderImpl(logger, this, resolvedFile);
    }
}
