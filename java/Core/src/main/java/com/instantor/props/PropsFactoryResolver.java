package com.instantor.props;

import java.io.File;

interface PropsFactoryResolver {
    public PropsLoader resolve(final File base, final String key, final String value);
}
