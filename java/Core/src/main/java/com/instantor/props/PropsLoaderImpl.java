package com.instantor.props;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;

public class PropsLoaderImpl implements PropsLoader {
    private final PropsFactoryResolver propsFactoryResolver;
    private final Logger logger;
    private final File file;

    PropsLoaderImpl(final Logger logger, final PropsLoaderFactory propsLoaderFactory, final File file) {
        this.logger = logger;
        this.propsFactoryResolver = propsLoaderFactory;
        this.file = file;
    }

    @Override
    public PropsLoader resolve(final String key) {
        return propsFactoryResolver.resolve(file.getParentFile(), key, get(key));
    }

    @Override
    public String get(final String key) {
        try {
            return toMap().get(key);
        } catch (final NoSuchElementException e) {
            throw new IllegalArgumentException(String.format("Key \"%s\" not found", key), e);
        }
    }

    @Override
    public int getInt(final String key) {
        final String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Key \"%s\" with value \"%s\" cannot be cast to int!", key, value), e);
        }
    }

    @Override
    public Properties toProps() {
        try {
            final Properties props = new Properties();
            props.load(toInputStream());
            return props;
        } catch (final IOException e) {
            throw new IllegalArgumentException("An error occured while parsing properties from: " + file, e);
        }
    }

    private final Object propsMapLock = new Object();
    private Map<String, String> propsMap;

    @Override
    public Map<String, String> toMap() {
        synchronized (propsMapLock) {
            if (propsMap == null) {
                propsMap = new LinkedHashMap<>();
                final Properties props = toProps();
                for (final String name : props.stringPropertyNames()) {
                    propsMap.put(name, props.getProperty(name));
                }
            }
        }

        return propsMap;
    }

    @Override
    public File toFile() {
        return file;
    }

    @Override
    public String toPath() {
        return file.getPath();
    }

    private final Object sourceLock = new Object();
    private byte[] source;

    private byte[] loadSource() {
        synchronized (sourceLock) {
            if (source == null) {
                try {
                    logger.trace("About to load: {}", file);
                    source = Files.readAllBytes(file.toPath());
                    logger.debug("Loaded: {} ({} bytes)", file, source.length);
                } catch (final IOException e) {
                    throw new RuntimeException("An error occured while trying to reading file: " + file);
                }
            }
        }

        return source;
    }

    @Override
    public byte[] toByteArray() {
        return loadSource().clone();
    }

    @Override
    public InputStream toInputStream() {
        return new ByteArrayInputStream(loadSource());
    }

    @Override
    public String toString() {
        return toString("ISO-8859-1");
    }

    @Override
    public String toString(final String encoding) {
        try {
            return new String(toByteArray(), encoding).trim();
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid encoding supplied to PropsLoader.toString!", e);
        }
    }
}
