package it.restaurant.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private String id;
    private LocalDate date;
    private int covers;
    private List<DishOrder> dishOrders = new ArrayList<>();
    private List<MenuOrder> menuOrders = new ArrayList<>();

    private Reservation() {
        this.id = UUID.randomUUID().toString();
    }

    public Reservation(LocalDate date, int covers) {
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.covers = covers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public int getCovers() { return covers; }
    public List<DishOrder> getDishOrders() { return dishOrders; }
    public List<MenuOrder> getMenuOrders() { return menuOrders; }

    public void addDishOrder(Dish dish, int quantity) {
        dishOrders.stream()
                .filter(o -> o.getDish().getName().equalsIgnoreCase(dish.getName()))
                .findFirst()
                .ifPresentOrElse(
                        o -> o.setQuantity(o.getQuantity() + quantity),
                        () -> dishOrders.add(new DishOrder(dish, quantity)));
    }

    public void addMenuOrder(ThemedMenu menu, int quantity) {
        menuOrders.stream()
                .filter(o -> o.getMenu().getName().equalsIgnoreCase(menu.getName()))
                .findFirst()
                .ifPresentOrElse(
                        o -> o.setQuantity(o.getQuantity() + quantity),
                        () -> menuOrders.add(new MenuOrder(menu, quantity)));
    }

    public double workload() {
        double dishes = dishOrders.stream().mapToDouble(DishOrder::workload).sum();
        double menus = menuOrders.stream().mapToDouble(MenuOrder::workload).sum();
        return dishes + menus;
    }
}
