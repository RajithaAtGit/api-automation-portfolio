package com.gtrxone.utils;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Test class for DateUtils.
 */
public class DateUtilsTest {

    /**
     * Test that the private constructor throws an exception.
     */
    @Test
    public void testPrivateConstructor() {
        try {
            // Use reflection to access the private constructor
            java.lang.reflect.Constructor<DateUtils> constructor = DateUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            Assert.fail("Expected IllegalStateException was not thrown");
        } catch (Exception e) {
            // Verify that the cause is IllegalStateException
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof IllegalStateException, "Expected IllegalStateException, but got " + cause.getClass().getName());
            Assert.assertEquals("Utility class", cause.getMessage(), "Unexpected exception message");
        }
    }

    /**
     * Test that getCurrentDateTimeIso returns a string in the correct format.
     */
    @Test
    public void testGetCurrentDateTimeIso() {
        String dateTime = DateUtils.getCurrentDateTimeIso();
        Assert.assertNotNull(dateTime, "Current date time should not be null");
        
        // Verify the format
        try {
            ZonedDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        } catch (DateTimeParseException e) {
            Assert.fail("Current date time is not in the expected format: " + dateTime);
        }
    }

    /**
     * Test that getCurrentDateIso returns a string in the correct format.
     */
    @Test
    public void testGetCurrentDateIso() {
        String date = DateUtils.getCurrentDateIso();
        Assert.assertNotNull(date, "Current date should not be null");
        
        // Verify the format
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            Assert.fail("Current date is not in the expected format: " + date);
        }
    }

    /**
     * Test that getCurrentTimeIso returns a string in the correct format.
     */
    @Test
    public void testGetCurrentTimeIso() {
        String time = DateUtils.getCurrentTimeIso();
        Assert.assertNotNull(time, "Current time should not be null");
        
        // Verify the format
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            Assert.fail("Current time is not in the expected format: " + time);
        }
    }

    /**
     * Test that formatDateToIso correctly formats a Date.
     */
    @Test
    public void testFormatDateToIso_Date() {
        Date date = new Date();
        String formattedDate = DateUtils.formatDateToIso(date);
        Assert.assertNotNull(formattedDate, "Formatted date should not be null");
        
        // Verify the format
        try {
            ZonedDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        } catch (DateTimeParseException e) {
            Assert.fail("Formatted date is not in the expected format: " + formattedDate);
        }
    }

    /**
     * Test that formatDateToIso correctly formats an Instant.
     */
    @Test
    public void testFormatDateToIso_Instant() {
        Instant instant = Instant.now();
        String formattedDate = DateUtils.formatDateToIso(instant);
        Assert.assertNotNull(formattedDate, "Formatted date should not be null");
        
        // Verify the format
        try {
            ZonedDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        } catch (DateTimeParseException e) {
            Assert.fail("Formatted date is not in the expected format: " + formattedDate);
        }
    }

    /**
     * Test that formatDateToIso correctly formats a LocalDate.
     */
    @Test
    public void testFormatDateToIso_LocalDate() {
        LocalDate localDate = LocalDate.now();
        String formattedDate = DateUtils.formatDateToIso(localDate);
        Assert.assertNotNull(formattedDate, "Formatted date should not be null");
        
        // Verify the format
        try {
            LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            Assert.fail("Formatted date is not in the expected format: " + formattedDate);
        }
    }

    /**
     * Test that formatTimeToIso correctly formats a LocalTime.
     */
    @Test
    public void testFormatTimeToIso() {
        LocalTime localTime = LocalTime.now();
        String formattedTime = DateUtils.formatTimeToIso(localTime);
        Assert.assertNotNull(formattedTime, "Formatted time should not be null");
        
        // Verify the format
        try {
            LocalTime.parse(formattedTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            Assert.fail("Formatted time is not in the expected format: " + formattedTime);
        }
    }

    /**
     * Test that formatDateTimeToIso correctly formats a LocalDateTime.
     */
    @Test
    public void testFormatDateTimeToIso() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String formattedDateTime = DateUtils.formatDateTimeToIso(localDateTime);
        Assert.assertNotNull(formattedDateTime, "Formatted date time should not be null");
        
        // Verify the format
        try {
            ZonedDateTime.parse(formattedDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        } catch (DateTimeParseException e) {
            Assert.fail("Formatted date time is not in the expected format: " + formattedDateTime);
        }
    }

    /**
     * Test that parseIsoDate correctly parses a date string.
     */
    @Test
    public void testParseIsoDate() {
        String dateString = "2023-01-15";
        LocalDate localDate = DateUtils.parseIsoDate(dateString);
        Assert.assertNotNull(localDate, "Parsed date should not be null");
        Assert.assertEquals(localDate.getYear(), 2023, "Year should be 2023");
        Assert.assertEquals(localDate.getMonthValue(), 1, "Month should be 1");
        Assert.assertEquals(localDate.getDayOfMonth(), 15, "Day should be 15");
    }

    /**
     * Test that parseIsoTime correctly parses a time string.
     */
    @Test
    public void testParseIsoTime() {
        String timeString = "14:30:45";
        LocalTime localTime = DateUtils.parseIsoTime(timeString);
        Assert.assertNotNull(localTime, "Parsed time should not be null");
        Assert.assertEquals(localTime.getHour(), 14, "Hour should be 14");
        Assert.assertEquals(localTime.getMinute(), 30, "Minute should be 30");
        Assert.assertEquals(localTime.getSecond(), 45, "Second should be 45");
    }

    /**
     * Test that parseIsoDateTime correctly parses a date time string.
     */
    @Test
    public void testParseIsoDateTime() {
        String dateTimeString = "2023-01-15T14:30:45.123+00:00";
        ZonedDateTime zonedDateTime = DateUtils.parseIsoDateTime(dateTimeString);
        Assert.assertNotNull(zonedDateTime, "Parsed date time should not be null");
        Assert.assertEquals(zonedDateTime.getYear(), 2023, "Year should be 2023");
        Assert.assertEquals(zonedDateTime.getMonthValue(), 1, "Month should be 1");
        Assert.assertEquals(zonedDateTime.getDayOfMonth(), 15, "Day should be 15");
        Assert.assertEquals(zonedDateTime.getHour(), 14, "Hour should be 14");
        Assert.assertEquals(zonedDateTime.getMinute(), 30, "Minute should be 30");
        Assert.assertEquals(zonedDateTime.getSecond(), 45, "Second should be 45");
    }

    /**
     * Test that addDays correctly adds days to a LocalDate.
     */
    @Test
    public void testAddDays() {
        LocalDate localDate = LocalDate.of(2023, 1, 15);
        LocalDate newDate = DateUtils.addDays(localDate, 5);
        Assert.assertNotNull(newDate, "New date should not be null");
        Assert.assertEquals(newDate.getYear(), 2023, "Year should be 2023");
        Assert.assertEquals(newDate.getMonthValue(), 1, "Month should be 1");
        Assert.assertEquals(newDate.getDayOfMonth(), 20, "Day should be 20");
    }

    /**
     * Test that subtractDays correctly subtracts days from a LocalDate.
     */
    @Test
    public void testSubtractDays() {
        LocalDate localDate = LocalDate.of(2023, 1, 15);
        LocalDate newDate = DateUtils.subtractDays(localDate, 5);
        Assert.assertNotNull(newDate, "New date should not be null");
        Assert.assertEquals(newDate.getYear(), 2023, "Year should be 2023");
        Assert.assertEquals(newDate.getMonthValue(), 1, "Month should be 1");
        Assert.assertEquals(newDate.getDayOfMonth(), 10, "Day should be 10");
    }

    /**
     * Test that addHours correctly adds hours to a LocalTime.
     */
    @Test
    public void testAddHours() {
        LocalTime localTime = LocalTime.of(14, 30, 45);
        LocalTime newTime = DateUtils.addHours(localTime, 5);
        Assert.assertNotNull(newTime, "New time should not be null");
        Assert.assertEquals(newTime.getHour(), 19, "Hour should be 19");
        Assert.assertEquals(newTime.getMinute(), 30, "Minute should be 30");
        Assert.assertEquals(newTime.getSecond(), 45, "Second should be 45");
    }

    /**
     * Test that subtractHours correctly subtracts hours from a LocalTime.
     */
    @Test
    public void testSubtractHours() {
        LocalTime localTime = LocalTime.of(14, 30, 45);
        LocalTime newTime = DateUtils.subtractHours(localTime, 5);
        Assert.assertNotNull(newTime, "New time should not be null");
        Assert.assertEquals(newTime.getHour(), 9, "Hour should be 9");
        Assert.assertEquals(newTime.getMinute(), 30, "Minute should be 30");
        Assert.assertEquals(newTime.getSecond(), 45, "Second should be 45");
    }

    /**
     * Test that getDaysBetween correctly calculates the days between two LocalDates.
     */
    @Test
    public void testGetDaysBetween() {
        LocalDate startDate = LocalDate.of(2023, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 1, 20);
        long days = DateUtils.getDaysBetween(startDate, endDate);
        Assert.assertEquals(days, 5, "Days between should be 5");
    }

    /**
     * Test that getHoursBetween correctly calculates the hours between two LocalTimes.
     */
    @Test
    public void testGetHoursBetween() {
        LocalTime startTime = LocalTime.of(9, 30, 45);
        LocalTime endTime = LocalTime.of(14, 30, 45);
        long hours = DateUtils.getHoursBetween(startTime, endTime);
        Assert.assertEquals(hours, 5, "Hours between should be 5");
    }

    /**
     * Test that getMinutesBetween correctly calculates the minutes between two LocalDateTimes.
     */
    @Test
    public void testGetMinutesBetween() {
        LocalDateTime startDateTime = LocalDateTime.of(2023, 1, 15, 9, 30, 45);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 1, 15, 10, 0, 45);
        long minutes = DateUtils.getMinutesBetween(startDateTime, endDateTime);
        Assert.assertEquals(minutes, 30, "Minutes between should be 30");
    }

    /**
     * Test that isBefore correctly determines if a LocalDate is before another LocalDate.
     */
    @Test
    public void testIsBefore() {
        LocalDate date1 = LocalDate.of(2023, 1, 15);
        LocalDate date2 = LocalDate.of(2023, 1, 20);
        Assert.assertTrue(DateUtils.isBefore(date1, date2), "date1 should be before date2");
        Assert.assertFalse(DateUtils.isBefore(date2, date1), "date2 should not be before date1");
    }

    /**
     * Test that isAfter correctly determines if a LocalDate is after another LocalDate.
     */
    @Test
    public void testIsAfter() {
        LocalDate date1 = LocalDate.of(2023, 1, 15);
        LocalDate date2 = LocalDate.of(2023, 1, 20);
        Assert.assertTrue(DateUtils.isAfter(date2, date1), "date2 should be after date1");
        Assert.assertFalse(DateUtils.isAfter(date1, date2), "date1 should not be after date2");
    }

    /**
     * Test that isBetween correctly determines if a LocalDate is between two other LocalDates.
     */
    @Test
    public void testIsBetween() {
        LocalDate startDate = LocalDate.of(2023, 1, 15);
        LocalDate middleDate = LocalDate.of(2023, 1, 20);
        LocalDate endDate = LocalDate.of(2023, 1, 25);
        
        Assert.assertTrue(DateUtils.isBetween(middleDate, startDate, endDate), "middleDate should be between startDate and endDate");
        Assert.assertTrue(DateUtils.isBetween(startDate, startDate, endDate), "startDate should be between startDate and endDate");
        Assert.assertTrue(DateUtils.isBetween(endDate, startDate, endDate), "endDate should be between startDate and endDate");
        Assert.assertFalse(DateUtils.isBetween(LocalDate.of(2023, 1, 10), startDate, endDate), "2023-01-10 should not be between startDate and endDate");
        Assert.assertFalse(DateUtils.isBetween(LocalDate.of(2023, 1, 30), startDate, endDate), "2023-01-30 should not be between startDate and endDate");
    }
}