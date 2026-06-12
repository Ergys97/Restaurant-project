package it.restaurant.api.dto;

public class DishSummary {

    private String name;
    private boolean available;

    private DishSummary() {}

    public DishSummary(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

    public String getName() { return name; }
    public boolean isAvailable() { return available; }
}
