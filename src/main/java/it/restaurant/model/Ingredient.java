package it.restaurant.model;

import java.time.LocalDate;

public class Ingredient extends FoodItem {
    private Ingredient() {} // Jackson
    public Ingredient(String name, int quantity, LocalDate expiryDate) { super(name, quantity, expiryDate); }
    @Override public String getUnit() { return "KG"; }
}
