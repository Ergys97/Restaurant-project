package it.restaurant.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThemedMenu {
    private String name;
    private List<Dish> dishes = new ArrayList<>();
    private LocalDate availableUntil;

    private ThemedMenu() {} // Jackson

    public ThemedMenu(String name, List<Dish> dishes, LocalDate availableUntil) {
        this.name = name;
        this.dishes = dishes;
        this.availableUntil = availableUntil;
    }

    public String getName() { return name; }
    public List<Dish> getDishes() { return dishes; }
    public LocalDate getAvailableUntil() { return availableUntil; }

    public double workload() {
        return dishes.stream().mapToDouble(Dish::workload).sum();
    }

    @Override public String toString() { return name + " " + dishes; }
}
