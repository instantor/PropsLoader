package com.instantor.props;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleSelectableMapTest {
  public static final SelectableMap EmptySM = new SimpleSelectableMap();
  public static final SelectableMap SingleSM = new SimpleSelectableMap(); static {
    SingleSM.put("a", "val 1");
  }
  public static final SelectableMap MultiSM = new SimpleSelectableMap(); static {
    MultiSM.put("a",     "val 1");
    MultiSM.put("b",     "val 2");
    MultiSM.put("b.a",   "val 3");
    MultiSM.put("c.a",   "val 4");
    MultiSM.put("c.b.a", "val 5");
    MultiSM.put("c.b.b", "val 6");
    MultiSM.put("c.b.c", "val 7");
  }

  @Test
  public void prefixesEmpty() {
    final Set<String> expected = new HashSet<>();
    final Set<String> actual   = EmptySM.prefixes();
    assertEquals(expected, actual);
  }

  @Test
  public void prefixesSingle() {
    final Set<String> expected = new HashSet<>();
    final Set<String> actual   = SingleSM.prefixes();
    assertEquals(expected, actual);
  }

  @Test
  public void prefixesMulti() {
    final Set<String> expected = new HashSet<>();
    expected.add("b");
    expected.add("c");
    final Set<String> actual   = MultiSM.prefixes();
    assertEquals(expected, actual);
  }

  @Test
  public void selectEmpty() {
    final SelectableMap expected  = new SimpleSelectableMap();
    final SelectableMap actualRaw = MultiSM.select("a");
    final SelectableMap actualDot = MultiSM.select("a.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectSingleMissing() {
    final SelectableMap expected  = new SimpleSelectableMap();
    final SelectableMap actualRaw = SingleSM.select("missing");
    final SelectableMap actualDot = SingleSM.select("missing.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectSingleExisting() {
    final SelectableMap expected  = new SimpleSelectableMap();
    final SelectableMap actualRaw = SingleSM.select("a");
    final SelectableMap actualDot = SingleSM.select("a.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectMultiMissing() {
    final SelectableMap expected = new SimpleSelectableMap();
    final SelectableMap actualRaw = MultiSM.select("missing");
    final SelectableMap actualDot = MultiSM.select("missing.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectMultiExistingEmpty() {
    final SelectableMap expected = new SimpleSelectableMap();
    final SelectableMap actualRaw = MultiSM.select("a");
    final SelectableMap actualDot = MultiSM.select("a.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectMultiExistingSingle() {
    final SelectableMap expected = new SimpleSelectableMap();
    expected.put("a", "val 3");
    final SelectableMap actualRaw = MultiSM.select("b");
    final SelectableMap actualDot = MultiSM.select("b.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectMultiExistingMulti() {
    final SelectableMap expected = new SimpleSelectableMap();
    expected.put("a",   "val 4");
    expected.put("b.a", "val 5");
    expected.put("b.b", "val 6");
    expected.put("b.c", "val 7");
    final SelectableMap actualRaw = MultiSM.select("c");
    final SelectableMap actualDot = MultiSM.select("c.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectMultiSecondLevel() {
    final SelectableMap expected = new SimpleSelectableMap();
    expected.put("a", "val 5");
    expected.put("b", "val 6");
    expected.put("c", "val 7");
    final SelectableMap actualRaw = MultiSM.select("c.b");
    final SelectableMap actualDot = MultiSM.select("c.b.");
    assertEquals(expected, actualRaw);
    assertEquals(expected, actualDot);
  }

  @Test
  public void selectAllEmpty() {
    final Map<String, SelectableMap> expected = new HashMap<>();
    final Map<String, SelectableMap> actual   = EmptySM.selectAll();
    assertEquals(expected, actual);
  }

  @Test
  public void selectAllSingle() {
    final Map<String, SelectableMap> expected = new HashMap<>();
    final Map<String, SelectableMap> actual   = SingleSM.selectAll();
    assertEquals(expected, actual);
  }

  @Test
  @SuppressWarnings("serial")
  public void selectAllMulti() {
    final Map<String, SelectableMap> expected = new HashMap<>();
    expected.put("b", new SimpleSelectableMap() {{ put("a", "val 3"); }});
    expected.put("c", new SimpleSelectableMap() {{
      put("a", "val 4");
      put("b.a", "val 5");
      put("b.b", "val 6");
      put("b.c", "val 7"); }});
    final Map<String, SelectableMap> actual = MultiSM.selectAll();
    assertEquals(expected, actual);
  }
}
