package com.gtrxone.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utility class for date and time operations.
 * Provides common utility functions for date and time handling.
 */
@Slf4j
public class DateUtils {
    
    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    private static final String ISO_TIME_FORMAT = "HH:mm:ss";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Gets the current date and time in ISO format.
     *
     * @return The current date and time in ISO format
     */
    public static String getCurrentDateTimeIso() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT));
    }
    
    /**
     * Gets the current date in ISO format.
     *
     * @return The current date in ISO format
     */
    public static String getCurrentDateIso() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
    }
    
    /**
     * Gets the current time in ISO format.
     *
     * @return The current time in ISO format
     */
    public static String getCurrentTimeIso() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(ISO_TIME_FORMAT));
    }
    
    /**
     * Formats a date to ISO format.
     *
     * @param date The date to format
     * @return The formatted date
     */
    public static String formatDateToIso(Date date) {
        return formatDateToIso(date.toInstant());
    }
    
    /**
     * Formats an instant to ISO format.
     *
     * @param instant The instant to format
     * @return The formatted date
     */
    public static String formatDateToIso(Instant instant) {
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT));
    }
    
    /**
     * Formats a local date to ISO format.
     *
     * @param localDate The local date to format
     * @return The formatted date
     */
    public static String formatDateToIso(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
    }
    
    /**
     * Formats a local time to ISO format.
     *
     * @param localTime The local time to format
     * @return The formatted time
     */
    public static String formatTimeToIso(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern(ISO_TIME_FORMAT));
    }
    
    /**
     * Formats a local date time to ISO format.
     *
     * @param localDateTime The local date time to format
     * @return The formatted date time
     */
    public static String formatDateTimeToIso(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT));
    }
    
    /**
     * Parses an ISO date string to a local date.
     *
     * @param dateString The date string to parse
     * @return The parsed local date
     * @throws DateTimeParseException if the date string cannot be parsed
     */
    public static LocalDate parseIsoDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(ISO_DATE_FORMAT));
    }
    
    /**
     * Parses an ISO time string to a local time.
     *
     * @param timeString The time string to parse
     * @return The parsed local time
     * @throws DateTimeParseException if the time string cannot be parsed
     */
    public static LocalTime parseIsoTime(String timeString) {
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern(ISO_TIME_FORMAT));
    }
    
    /**
     * Parses an ISO date time string to a zoned date time.
     *
     * @param dateTimeString The date time string to parse
     * @return The parsed zoned date time
     * @throws DateTimeParseException if the date time string cannot be parsed
     */
    public static ZonedDateTime parseIsoDateTime(String dateTimeString) {
        return ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT));
    }
    
    /**
     * Adds days to a local date.
     *
     * @param localDate The local date
     * @param days The number of days to add
     * @return The new local date
     */
    public static LocalDate addDays(LocalDate localDate, int days) {
        return localDate.plusDays(days);
    }
    
    /**
     * Subtracts days from a local date.
     *
     * @param localDate The local date
     * @param days The number of days to subtract
     * @return The new local date
     */
    public static LocalDate subtractDays(LocalDate localDate, int days) {
        return localDate.minusDays(days);
    }
    
    /**
     * Adds hours to a local time.
     *
     * @param localTime The local time
     * @param hours The number of hours to add
     * @return The new local time
     */
    public static LocalTime addHours(LocalTime localTime, int hours) {
        return localTime.plusHours(hours);
    }
    
    /**
     * Subtracts hours from a local time.
     *
     * @param localTime The local time
     * @param hours The number of hours to subtract
     * @return The new local time
     */
    public static LocalTime subtractHours(LocalTime localTime, int hours) {
        return localTime.minusHours(hours);
    }
    
    /**
     * Gets the difference between two local dates in days.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return The difference in days
     */
    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * Gets the difference between two local times in hours.
     *
     * @param startTime The start time
     * @param endTime The end time
     * @return The difference in hours
     */
    public static long getHoursBetween(LocalTime startTime, LocalTime endTime) {
        return ChronoUnit.HOURS.between(startTime, endTime);
    }
    
    /**
     * Gets the difference between two local date times in minutes.
     *
     * @param startDateTime The start date time
     * @param endDateTime The end date time
     * @return The difference in minutes
     */
    public static long getMinutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    /**
     * Checks if a local date is before another local date.
     *
     * @param date The date to check
     * @param otherDate The other date
     * @return True if the date is before the other date, false otherwise
     */
    public static boolean isBefore(LocalDate date, LocalDate otherDate) {
        return date.isBefore(otherDate);
    }
    
    /**
     * Checks if a local date is after another local date.
     *
     * @param date The date to check
     * @param otherDate The other date
     * @return True if the date is after the other date, false otherwise
     */
    public static boolean isAfter(LocalDate date, LocalDate otherDate) {
        return date.isAfter(otherDate);
    }
    
    /**
     * Checks if a local date is between two other local dates.
     *
     * @param date The date to check
     * @param startDate The start date
     * @param endDate The end date
     * @return True if the date is between the start and end dates, false otherwise
     */
    public static boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) && 
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}