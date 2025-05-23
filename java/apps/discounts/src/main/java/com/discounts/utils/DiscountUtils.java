package com.discounts.utils;

import java.time.LocalDate;

public class DiscountUtils {

    /**
     * Validates that:
     * - fromDate is not in the past
     * - toDate is not before fromDate
     *
     * @param fromDate the starting date/time
     * @param toDate   the ending date/time
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDate today = LocalDate.now();

        return !fromDate.isBefore(today) && !toDate.isBefore(fromDate);
    }


}
