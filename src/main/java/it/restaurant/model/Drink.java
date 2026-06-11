package it.restaurant.model;

import java.time.LocalDate;

public class Drink extends FoodItem {
    private Drink() {} // Jackson
    public Drink(String name, int quantity, LocalDate expiryDate) { super(name, quantity, expiryDate); }
    @Override public String getUnit() { return "L"; }
}
