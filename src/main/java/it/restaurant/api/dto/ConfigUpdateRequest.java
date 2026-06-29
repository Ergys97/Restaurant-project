package it.restaurant.api.dto;

import it.restaurant.model.Drink;
import it.restaurant.model.ExtraGood;
import it.restaurant.model.Ingredient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class ConfigUpdateRequest {

    @NotNull
    @Min(1)
    private Integer seats;

    @NotNull
    @DecimalMin("0.1")
    private Double workloadPerPerson;

    @Valid
    private List<Ingredient> ingredients;

    @Valid
    private List<Drink> drinks;

    @Valid
    private List<ExtraGood> extraGoods;

    private Map<@NotBlank String, @Min(0) Integer> perCapitaDrinks;
    private Map<@NotBlank String, @Min(0) Integer> perCapitaExtraGoods;

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public Double getWorkloadPerPerson() { return workloadPerPerson; }
    public void setWorkloadPerPerson(Double workloadPerPerson) { this.workloadPerPerson = workloadPerPerson; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public List<Drink> getDrinks() { return drinks; }
    public void setDrinks(List<Drink> drinks) { this.drinks = drinks; }

    public List<ExtraGood> getExtraGoods() { return extraGoods; }
    public void setExtraGoods(List<ExtraGood> extraGoods) { this.extraGoods = extraGoods; }

    public Map<String, Integer> getPerCapitaDrinks() { return perCapitaDrinks; }
    public void setPerCapitaDrinks(Map<String, Integer> perCapitaDrinks) { this.perCapitaDrinks = perCapitaDrinks; }

    public Map<String, Integer> getPerCapitaExtraGoods() { return perCapitaExtraGoods; }
    public void setPerCapitaExtraGoods(Map<String, Integer> perCapitaExtraGoods) { this.perCapitaExtraGoods = perCapitaExtraGoods; }
}
