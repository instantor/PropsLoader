package com.ferega.props.japi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  FileConstructionTest.class,
  PropsLoaderFactoryTest.class,
  PropsLoaderTest.class,
  PropsPathTest.class,
  PropsToMapTest.class
})

public class JApiSuite {
}
