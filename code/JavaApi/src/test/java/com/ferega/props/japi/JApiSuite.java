package com.ferega.props.japi;

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
  SelectableMapTest.class
})

public class JApiSuite {
}
