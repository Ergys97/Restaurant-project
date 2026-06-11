package it.restaurant.model;

public class DishOrder {
    private Dish dish;
    private int quantity;

    private DishOrder() {} // Jackson
    public DishOrder(Dish dish, int quantity) { this.dish = dish; this.quantity = quantity; }

    public Dish getDish() { return dish; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double workload() { return dish.workload() * quantity; }
}
