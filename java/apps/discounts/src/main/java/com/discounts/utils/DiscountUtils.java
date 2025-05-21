package com.discounts.utils;

import java.time.LocalDateTime;

public class DiscountUtils {

    /**
     * Validates that:
     * - fromDate is not in the past
     * - toDate is not before fromDate
     *
     * @param fromDate the starting date/time
     * @param toDate the ending date/time
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime now = LocalDateTime.now();

        return !fromDate.isBefore(now) && !toDate.isBefore(fromDate);
    }


}
