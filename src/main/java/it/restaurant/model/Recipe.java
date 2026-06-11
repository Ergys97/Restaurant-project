package it.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();
    private double workloadPerPortion;
    private int prepTimeMinutes;

    private Recipe() {} // Jackson

    public Recipe(String name, List<Ingredient> ingredients, double fractionOfPersonWorkload,
                  double workloadPerPerson, int prepTimeMinutes) {
        this.name = name;
        this.ingredients = ingredients;
        this.workloadPerPortion = workloadPerPerson * fractionOfPersonWorkload;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public String getName() { return name; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public double getWorkloadPerPortion() { return workloadPerPortion; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
}
