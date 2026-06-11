package it.restaurant.model;

import java.time.LocalDate;
import java.util.List;

public class Dish {
    private String name;
    private Recipe recipe;
    private LocalDate availableUntil;

    private Dish() {} // Jackson

    public Dish(Recipe recipe, LocalDate availableUntil) {
        this.recipe = recipe;
        this.availableUntil = availableUntil;
        this.name = recipe.getName();
    }

    public String getName() { return name; }
    public Recipe getRecipe() { return recipe; }
    public LocalDate getAvailableUntil() { return availableUntil; }
    public List<Ingredient> getIngredients() { return recipe.getIngredients(); }
    public double workload() { return recipe.getWorkloadPerPortion(); }

    public boolean isAvailable(LocalDate today) { return !availableUntil.isBefore(today); }

    @Override public String toString() { return "[Dish: " + name + "]"; }
}
