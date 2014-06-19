package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PropsToMapTest {
  private static final String ele1 = "ele1"; private static final String val1 = "val1";
  private static final String ele2 = "ele2"; private static final String val2 = "val2";
  private static final String ele3 = "ele3"; private static final String val3 = "val3";

  private static final Map<String, String> emptyMap = new HashMap<>();
  private static final Map<String, String> oneMap = new HashMap<>(); static {
    oneMap.put(ele1, val1);
  }
  private static final Map<String, String> manyMap = new HashMap<>(); static {
    manyMap.put(ele1, val1);
    manyMap.put(ele2, val2);
    manyMap.put(ele3, val3);
  }


  private static final Properties emptyProps = new Properties();
  private static final Properties oneProps  = new Properties(); static {
    oneProps.setProperty(ele1, val1);
  }
  private static final Properties manyProps  = new Properties(); static {
    manyProps.setProperty(ele1, val1);
    manyProps.setProperty(ele2, val2);
    manyProps.setProperty(ele3, val3);
  }

  @Test
  public void empty() throws IOException {
    final Map<String, String> expected = emptyMap;
    final Map<String, String> actual   = Util.propsToMap(emptyProps);
    assertEquals(expected, actual);
  }

  @Test
  public void one() throws IOException {
    final Map<String, String> expected = oneMap;
    final Map<String, String> actual   = Util.propsToMap(oneProps);
    assertEquals(expected, actual);
  }

  @Test
  public void many() throws IOException {
    final Map<String, String> expected = manyMap;
    final Map<String, String> actual   = Util.propsToMap(manyProps);
    assertEquals(expected, actual);
  }
}
