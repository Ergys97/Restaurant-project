package it.restaurant.api.dto;

public class MenuSummary {

    private String name;
    private int dishCount;
    private boolean available;

    private MenuSummary() {}

    public MenuSummary(String name, int dishCount, boolean available) {
        this.name = name;
        this.dishCount = dishCount;
        this.available = available;
    }

    public String getName() { return name; }
    public int getDishCount() { return dishCount; }
    public boolean isAvailable() { return available; }
}
