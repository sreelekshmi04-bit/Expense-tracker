package com.expensetracker.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Utility for date formatting and month-year extraction.
 */
public class DateUtil {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
    private static final DateTimeFormatter MONTH_YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter FRIENDLY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter FRIENDLY_MONTH = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Returns the current month in "YYYY-MM" format.
     */
    public static String getCurrentMonthYear() {
        return YearMonth.now().format(MONTH_YEAR_FORMAT);
    }

    /**
     * Extracts "YYYY-MM" from a given LocalDate.
     */
    public static String getMonthYear(LocalDate date) {
        if (date == null) return getCurrentMonthYear();
        return date.format(MONTH_YEAR_FORMAT);
    }

    /**
     * Formats LocalDate to friendly display string (e.g. 15 Oct 2026).
     */
    public static String formatFriendly(LocalDate date) {
        if (date == null) return "";
        return date.format(FRIENDLY_DATE);
    }

    /**
     * Formats a "YYYY-MM" string to friendly name (e.g. "October 2026").
     */
    public static String formatMonthFriendly(String monthYear) {
        if (monthYear == null || monthYear.isBlank()) return "";
        try {
            YearMonth ym = YearMonth.parse(monthYear);
            return ym.format(FRIENDLY_MONTH);
        } catch (Exception e) {
            return monthYear;
        }
    }
}
