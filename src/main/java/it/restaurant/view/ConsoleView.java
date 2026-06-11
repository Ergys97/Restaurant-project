package it.restaurant.view;

import it.restaurant.model.*;
import java.util.List;

public class ConsoleView {

    public void showLine(String message) { System.out.println(message); }

    public void showFoodItems(List<? extends FoodItem> items) {
        if (items.isEmpty()) {
            System.out.println(Messages.NO_FOOD_ITEMS);
            return;
        }
        int i = 0;
        for (FoodItem item : items) {
            System.out.println(Messages.PROMPT_PREFIX + i++ + " - " + item.getName()
                    + " - " + item.getQuantity() + " - " + item.getUnit() + " - " + item.getExpiryDate());
        }
    }

    public void showShoppingList(ShoppingList list) {
        System.out.println(Messages.PROMPT_PREFIX + "Ingredienti");
        showFoodItems(list.getIngredients());
        System.out.println(Messages.PROMPT_PREFIX + "Bevande");
        showFoodItems(list.getDrinks());
        System.out.println(Messages.PROMPT_PREFIX + "Generi Alimentari Extra");
        showFoodItems(list.getExtraGoods());
    }

    public void showReservations(List<Reservation> reservations) {
        if (reservations.isEmpty()) {
            System.out.println(Messages.NO_RESERVATIONS);
            return;
        }
        int i = 0;
        for (Reservation r : reservations) {
            System.out.println(Messages.PROMPT_PREFIX + i++ + " - Data: " + r.getDate()
                    + " Persone: " + r.getCovers());
            r.getMenuOrders().forEach(o -> System.out.println(
                    "\t" + Messages.PROMPT_PREFIX + o.getMenu().getName() + " x" + o.getQuantity()));
            r.getDishOrders().forEach(o -> System.out.println(
                    "\t" + Messages.PROMPT_PREFIX + o.getDish().getName() + " x" + o.getQuantity()));
        }
    }

    public void showRestaurantConfig(RestaurantConfig config) {
        if (!config.isInitialized()) {
            System.out.println(Messages.RESTAURANT_NOT_INITIALIZED);
            return;
        }
        System.out.println(Messages.RESTAURANT_FEATURES);
        System.out.println(Messages.RESTAURANT_SEATS + config.getSeats());
        System.out.println(Messages.WORKLOAD_PER_PERSON + config.getWorkloadPerPerson());
        System.out.println(Messages.SUSTAINABLE_WORKLOAD + config.getSustainableWorkload());
    }

    public void showNamedList(List<String> names, String emptyMessage) {
        if (names.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        int i = 0;
        for (String name : names) {
            System.out.println(Messages.PROMPT_PREFIX + i++ + " - " + name);
        }
    }
}
