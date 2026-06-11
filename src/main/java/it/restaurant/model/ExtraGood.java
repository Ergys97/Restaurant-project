package it.restaurant.model;

import java.time.LocalDate;

public class ExtraGood extends FoodItem {
    private ExtraGood() {} // Jackson
    public ExtraGood(String name, int quantity, LocalDate expiryDate) { super(name, quantity, expiryDate); }
    @Override public String getUnit() { return "HG"; }
}
