package com.ferega.props.japi;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OrElseOptTest {
  private static final Optional<String> optEmpty = Optional.empty();
  private static final Optional<String> optFull1  = Optional.of("optfull1");
  private static final Optional<String> optFull2  = Optional.of("optfull2");

  @Test
  public void zero() {
    final Optional<String> expected = optEmpty;
    final Optional<String> actual   = Util.orElseOpt();
    assertEquals(expected, actual);
  }

  @Test
  public void oneEmpty() {
    final Optional<String> expected = optEmpty;
    final Optional<String> actual   = Util.orElseOpt(optEmpty);
    assertEquals(expected, actual);
  }

  @Test
  public void manyEmpty() {
    final Optional<String> expected = optEmpty;
    final Optional<String> actual   = Util.orElseOpt(optEmpty, optEmpty, optEmpty);
    assertEquals(expected, actual);
  }

  @Test
  public void oneFull() {
    final Optional<String> expected = optFull1;
    final Optional<String> actual   = Util.orElseOpt(optFull1);
    assertEquals(expected, actual);
  }

  @Test
  public void twoFull() {
    final Optional<String> expected = optFull1;
    final Optional<String> actual   = Util.orElseOpt(optFull1, optFull2);
    assertEquals(expected, actual);
  }

  @Test
  public void manyFull() {
    final Optional<String> expected = optFull1;
    final Optional<String> actual   = Util.orElseOpt(optFull1, optFull2, optFull2);
    assertEquals(expected, actual);
  }

  @Test
  public void oneEmptyOneFull() {
    final Optional<String> expected = optFull1;
    final Optional<String> actual   = Util.orElseOpt(optEmpty, optFull1);
    assertEquals(expected, actual);
  }

  @Test
  public void oneEmptyManyFull() {
    final Optional<String> expected = optFull1;
    final Optional<String> actual   = Util.orElseOpt(optEmpty, optFull1, optFull2, optFull2);
    assertEquals(expected, actual);
  }
}
