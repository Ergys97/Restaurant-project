package it.restaurant.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private LocalDate date;
    private int covers;
    private List<DishOrder> dishOrders = new ArrayList<>();
    private List<MenuOrder> menuOrders = new ArrayList<>();

    private Reservation() {} // Jackson

    public Reservation(LocalDate date, int covers) {
        this.date = date;
        this.covers = covers;
    }

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
