package it.restaurant.model;

public class MenuOrder {
    private ThemedMenu menu;
    private int quantity;

    private MenuOrder() {} // Jackson
    public MenuOrder(ThemedMenu menu, int quantity) { this.menu = menu; this.quantity = quantity; }

    public ThemedMenu getMenu() { return menu; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double workload() { return menu.workload() * quantity; }
}
