package it.restaurant.util;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public final class ExpiryDates {
    private ExpiryDates() {}

    public static LocalDate random(LocalDate today) {
        return today.plusDays(ThreadLocalRandom.current().nextInt(5, 11));
    }

    public static LocalDate inDays(LocalDate today, int days) {
        return today.plusDays(days);
    }
}
