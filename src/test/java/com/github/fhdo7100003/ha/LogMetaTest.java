package com.github.fhdo7100003.ha;

import org.junit.jupiter.api.Test; // Importing the Test annotation

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogMetaTest {

  @Test
  public void testParseValidLogEntry() {
    String logEntry = "2023-10-19_device1.txt"; // Example log entry
    LogMeta logMeta = LogMeta.parse(logEntry);

    // Using fully qualified names for assertions
    org.junit.jupiter.api.Assertions.assertNotNull(logMeta);
    org.junit.jupiter.api.Assertions.assertEquals(2023, logMeta.date().get(Calendar.YEAR));
    org.junit.jupiter.api.Assertions.assertEquals(10, logMeta.date().get(Calendar.MONTH) + 1); // Month is 0-based
    org.junit.jupiter.api.Assertions.assertEquals("device1", logMeta.deviceName());
  }

  @Test
  public void testParseInvalidLogEntry() {
    String invalidLogEntry = "invalid-log-entry.txt"; // Invalid format
    LogMeta logMeta = LogMeta.parse(invalidLogEntry);

    // Using fully qualified names for assertions
    org.junit.jupiter.api.Assertions.assertNull(logMeta); // Should return null for invalid entry
  }

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Test
  void testParseValidLogString() {
    String logString = "2023-10-18_Device123.txt";
    LogMeta meta = LogMeta.parse(logString);

    assertNotNull(meta, "LogMeta should not be null for valid log string.");
    assertEquals("Device123", meta.deviceName(), "Device name should match.");

    Calendar expectedDate = Calendar.getInstance();
    try {
      expectedDate.setTime(dateFormat.parse("2023-10-18"));
    } catch (ParseException e) {
      fail("Unexpected parse exception.");
    }

    assertEquals(expectedDate.get(Calendar.DAY_OF_YEAR), meta.date().get(Calendar.DAY_OF_YEAR),
        "Date should match the parsed date.");
  }

  @Test
  void testParseInvalidLogString() {
    String invalidLogString = "invalid_log_format.txt";
    LogMeta meta = LogMeta.parse(invalidLogString);
    assertNull(meta, "LogMeta should be null for invalid log string.");
  }

  @Test
  void testLogFilterFromDate() {
    Calendar date = Calendar.getInstance();
    date.set(2023, Calendar.OCTOBER, 18);

    LogMeta.LogFilter filter = LogMeta.LogFilter.any().from(date);

    Calendar anotherDate = Calendar.getInstance();
    anotherDate.set(2023, Calendar.OCTOBER, 18);
    LogMeta meta = new LogMeta(anotherDate, "Device123");

    assertTrue(filter.matches(meta), "Filter should match log with the same date.");
  }

  @Test
  void testLogFilterDeviceName() {
    LogMeta.LogFilter filter = LogMeta.LogFilter.any().name("Device123");

    LogMeta meta = new LogMeta(Calendar.getInstance(), "Device123");
    assertTrue(filter.matches(meta), "Filter should match log with the same device name.");

    LogMeta differentDeviceMeta = new LogMeta(Calendar.getInstance(), "AnotherDevice");
    assertFalse(filter.matches(differentDeviceMeta), "Filter should not match log with different device name.");
  }

  @Test
  void testLogFilterDateAndNameMatch() {
    Calendar date = Calendar.getInstance();
    date.set(2023, Calendar.OCTOBER, 18);

    LogMeta.LogFilter filter = LogMeta.LogFilter.any().from(date).name("Device123");

    Calendar matchingDate = Calendar.getInstance();
    matchingDate.set(2023, Calendar.OCTOBER, 18);
    LogMeta meta = new LogMeta(matchingDate, "Device123");

    assertTrue(filter.matches(meta), "Filter should match log with the same date and device name.");
  }

  @Test
  void testLogFilterDateAndNameMismatch() {
    Calendar date = Calendar.getInstance();
    date.set(2023, Calendar.OCTOBER, 18);

    LogMeta.LogFilter filter = LogMeta.LogFilter.any().from(date).name("Device123");

    Calendar differentDate = Calendar.getInstance();
    differentDate.set(2023, Calendar.OCTOBER, 19);
    LogMeta metaWithDifferentDate = new LogMeta(differentDate, "Device123");

    assertFalse(filter.matches(metaWithDifferentDate), "Filter should not match log with different date.");

    LogMeta metaWithDifferentName = new LogMeta(date, "AnotherDevice");
    assertFalse(filter.matches(metaWithDifferentName), "Filter should not match log with different device name.");
  }
}
