package it.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Drink> drinks = new ArrayList<>();
    private List<ExtraGood> extraGoods = new ArrayList<>();

    public ShoppingList() {}

    public ShoppingList(List<Ingredient> ingredients, List<Drink> drinks, List<ExtraGood> extraGoods) {
        this.ingredients = ingredients;
        this.drinks = drinks;
        this.extraGoods = extraGoods;
    }

    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Drink> getDrinks() { return drinks; }
    public List<ExtraGood> getExtraGoods() { return extraGoods; }

    public void merge(ShoppingList other) {
        other.getIngredients().forEach(i -> FoodItems.mergeQuantity(ingredients, i));
        other.getDrinks().forEach(d -> FoodItems.mergeQuantity(drinks, d));
        other.getExtraGoods().forEach(e -> FoodItems.mergeQuantity(extraGoods, e));
    }

    @JsonIgnore
    public boolean isEmpty() {
        return ingredients.isEmpty() && drinks.isEmpty() && extraGoods.isEmpty();
    }
}
