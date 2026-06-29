package it.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public abstract class FoodItem {

    @NotBlank
    private String name;

    @Min(0)
    private int quantity;

    @NotNull
    private LocalDate expiryDate;

    protected FoodItem() {} // Jackson

    protected FoodItem(String name, int quantity, LocalDate expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    @JsonIgnore
    public abstract String getUnit();

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpired(LocalDate today) { return expiryDate.isBefore(today); }
}
