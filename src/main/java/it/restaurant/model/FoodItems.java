package it.restaurant.model;

import java.util.List;
import java.util.Optional;

public final class FoodItems {
    private FoodItems() {}

    public static <T extends FoodItem> Optional<T> findByName(List<T> items, String name) {
        return items.stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public static <T extends FoodItem> boolean containsName(List<T> items, String name) {
        return findByName(items, name).isPresent();
    }

    public static <T extends FoodItem> void mergeQuantity(List<T> target, T toAdd) {
        findByName(target, toAdd.getName()).ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + toAdd.getQuantity()),
                () -> target.add(toAdd));
    }
}
