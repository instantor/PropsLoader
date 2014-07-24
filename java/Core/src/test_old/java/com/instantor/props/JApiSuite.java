package com.instantor.props;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  FileConstructionTest.class,
  LoadFileTest.class,
  PropsLoaderFactoryTest.class,
  PropsLoaderTest.class,
  PropsToMapTest.class,
  ResolvablePathTest.class,
  SimpleSelectableMapTest.class
})

public class JApiSuite {
}
