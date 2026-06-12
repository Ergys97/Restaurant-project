package it.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantConfig {
    private static final double SUSTAINABLE_FACTOR = 1.2;

    private int seats;
    private double workloadPerPerson;
    private boolean initialized;
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Drink> drinks = new ArrayList<>();
    private List<ExtraGood> extraGoods = new ArrayList<>();
    private Map<String, Integer> perCapitaDrinks = new HashMap<>();
    private Map<String, Integer> perCapitaExtraGoods = new HashMap<>();

    public RestaurantConfig() { this.initialized = false; }

    public RestaurantConfig(int seats, double workloadPerPerson,
                            List<Ingredient> ingredients, List<Drink> drinks, List<ExtraGood> extraGoods,
                            Map<String, Integer> perCapitaDrinks, Map<String, Integer> perCapitaExtraGoods) {
        this.seats = seats;
        this.workloadPerPerson = workloadPerPerson;
        this.ingredients = ingredients;
        this.drinks = drinks;
        this.extraGoods = extraGoods;
        this.perCapitaDrinks = perCapitaDrinks;
        this.perCapitaExtraGoods = perCapitaExtraGoods;
        this.initialized = true;
    }

    public void apply(RestaurantConfig other) {
        this.seats = other.seats;
        this.workloadPerPerson = other.workloadPerPerson;
        this.initialized = other.initialized;
        this.ingredients = other.ingredients;
        this.drinks = other.drinks;
        this.extraGoods = other.extraGoods;
        this.perCapitaDrinks = other.perCapitaDrinks;
        this.perCapitaExtraGoods = other.perCapitaExtraGoods;
    }

    @JsonIgnore
    public double getSustainableWorkload() { return seats * workloadPerPerson * SUSTAINABLE_FACTOR; }

    public int getSeats() { return seats; }
    public double getWorkloadPerPerson() { return workloadPerPerson; }
    public boolean isInitialized() { return initialized; }
    public List<Ingredient> getIngredients() { return java.util.Collections.unmodifiableList(ingredients); }
    public List<Drink> getDrinks() { return java.util.Collections.unmodifiableList(drinks); }
    public List<ExtraGood> getExtraGoods() { return java.util.Collections.unmodifiableList(extraGoods); }
    public Map<String, Integer> getPerCapitaDrinks() { return java.util.Collections.unmodifiableMap(perCapitaDrinks); }
    public Map<String, Integer> getPerCapitaExtraGoods() { return java.util.Collections.unmodifiableMap(perCapitaExtraGoods); }
}
