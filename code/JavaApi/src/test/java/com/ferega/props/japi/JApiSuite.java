package com.ferega.props.japi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  FileConstructionTest.class,
  OrElseOptTest.class,
  PropsLoaderTest.class,
  PropsLoadingTest.class,
  PropsPathTest.class,
  PropsToMapTest.class
})

public class JApiSuite {
}
