package it.restaurant.controller;

import it.restaurant.model.*;
import it.restaurant.repository.*;
import it.restaurant.service.KitchenService;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import it.restaurant.view.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerController {

    private static final String[] OPTIONS = {
        "Visualizza dati del ristorante",
        "Aggiungi ingredienti",
        "Crea ricetta",
        "Crea piatto",
        "Crea menu tematico",
        "Menu alla carta",
        "Azzera dati"
    };

    private final DataStore store;
    private final ConsoleView view;
    private final KitchenService kitchenService;
    private RestaurantConfig config;
    private final Menu menu = new Menu("Menu gestore", OPTIONS);

    public ManagerController(DataStore store, ConsoleView view) {
        this.store = store;
        this.view = view;
        this.config = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class).orElseGet(RestaurantConfig::new);
        this.kitchenService = new KitchenService(store, this.config, new DataStoreTransaction());
    }

    public RestaurantConfig ensureConfig() {
        config = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class).orElse(null);
        if (config == null || !config.isInitialized()) {
            setupRestaurant();
        }
        return config;
    }

    private void setupRestaurant() {
        view.showLine(Messages.SETUP_WELCOME);
        int seats = ConsoleInput.readIntAtLeast(Messages.ASK_SEATS, 1);
        double workload = ConsoleInput.readDouble(Messages.WORKLOAD_PER_PERSON_PROMPT, 1.0, 100.0);

        List<Ingredient> ingredients = new ArrayList<>();
        List<Drink> drinks = new ArrayList<>();
        List<ExtraGood> extraGoods = new ArrayList<>();
        Map<String, Integer> perCapitaDrinks = new HashMap<>();
        Map<String, Integer> perCapitaExtraGoods = new HashMap<>();

        LocalDate today = LocalDate.now();
        do {
            String name = ConsoleInput.readNonEmptyString(Messages.INGREDIENT_NAME_PROMPT);
            int qty = ConsoleInput.readIntAtLeast(Messages.INGREDIENT_QUANTITY_PROMPT, 1);
            ingredients.add(new Ingredient(name, qty, ExpiryDates.random(today)));
        } while (ConsoleInput.readYesNo(Messages.ADD_ANOTHER_INGREDIENT_PROMPT));

        do {
            String name = ConsoleInput.readNonEmptyString(Messages.DRINK_NAME_PROMPT);
            int qty = ConsoleInput.readIntAtLeast(Messages.DRINK_QUANTITY_PROMPT, 1);
            int perCapita = ConsoleInput.readIntAtLeast(Messages.PER_CAPITA_DRINK_QUANTITY_PROMPT, 0);
            drinks.add(new Drink(name, qty, ExpiryDates.random(today)));
            if (perCapita > 0) perCapitaDrinks.put(name, perCapita);
        } while (ConsoleInput.readYesNo(Messages.ADD_ANOTHER_DRINK_PROMPT));

        do {
            String name = ConsoleInput.readNonEmptyString(Messages.EXTRA_GOOD_NAME_PROMPT);
            int qty = ConsoleInput.readIntAtLeast(Messages.EXTRA_GOOD_QUANTITY_PROMPT, 1);
            int perCapita = ConsoleInput.readIntAtLeast(Messages.PER_CAPITA_EXTRA_GOOD_QUANTITY_PROMPT, 0);
            extraGoods.add(new ExtraGood(name, qty, ExpiryDates.random(today)));
            if (perCapita > 0) perCapitaExtraGoods.put(name, perCapita);
        } while (ConsoleInput.readYesNo(Messages.ADD_ANOTHER_EXTRA_GOOD_PROMPT));

        config = new RestaurantConfig(seats, workload, ingredients, drinks, extraGoods,
                perCapitaDrinks, perCapitaExtraGoods);
        store.save(StorageKeys.RESTAURANT_CONFIG, config);
        store.saveList(StorageKeys.INGREDIENTS, ingredients);
        store.saveList(StorageKeys.DRINKS, drinks);
        store.saveList(StorageKeys.EXTRA_GOODS, extraGoods);

        kitchenService.setConfig(config);
    }

    public void run() {
        if (config == null) ensureConfig();
        int choice;
        do {
            choice = menu.choose();
            switch (choice) {
                case 1 -> view.showRestaurantConfig(config);
                case 2 -> addIngredientsFlow();
                case 3 -> createRecipe();
                case 4 -> createDish();
                case 5 -> createThemedMenu();
                case 6 -> showMenu();
                case 7 -> resetData();
                default -> { }
            }
        } while (choice != 0);
    }

    private void addIngredientsFlow() {
        LocalDate today = LocalDate.now();
        do {
            String name = ConsoleInput.readNonEmptyString(Messages.INGREDIENT_NAME_PROMPT);
            int qty = ConsoleInput.readIntAtLeast(Messages.INGREDIENT_QUANTITY_PROMPT, 1);
            List<Ingredient> all = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);
            FoodItems.mergeQuantity(all, new Ingredient(name, qty, ExpiryDates.random(today)));
            store.saveList(StorageKeys.INGREDIENTS, all);
        } while (ConsoleInput.readYesNo(Messages.ADD_ANOTHER_INGREDIENT_PROMPT));
    }

    private void createRecipe() {
        List<Ingredient> allIngredients = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);
        if (allIngredients.isEmpty()) {
            view.showLine(Messages.NO_INGREDIENTS);
            return;
        }
        String name = ConsoleInput.readNonEmptyString(Messages.RECIPE_NAME_PROMPT);
        List<Ingredient> selected = ConsoleInput.selectItems(allIngredients, Ingredient::getName,
                Messages.SELECT_INGREDIENTS_SET_PROMPT, Messages.ADD_ANOTHER_INGREDIENT_PROMPT);
        double fraction = ConsoleInput.readDouble(Messages.RECIPE_FRACTION_PROMPT, 0.0, 1.0);
        int prepTime = ConsoleInput.readIntAtLeast(Messages.RECIPE_TIME_PROMPT, 1);
        Recipe recipe = new Recipe(name, selected, fraction, config.getWorkloadPerPerson(), prepTime);
        if (kitchenService.addRecipe(recipe)) {
            view.showLine(Messages.RECIPE_ADDED);
        } else {
            view.showLine(Messages.RECIPE_NOT_ADDED);
        }
    }

    private void createDish() {
        List<Recipe> recipes = kitchenService.recipes();
        if (recipes.isEmpty()) {
            view.showLine(Messages.NO_RECIPES_ERROR);
            return;
        }
        Recipe chosen = ConsoleInput.selectOne(recipes, Recipe::getName, Messages.SELECT_RECIPE_PROMPT);
        int days = ConsoleInput.readIntAtLeast(Messages.DISH_VALIDITY_DAYS_PROMPT, 0);
        Dish dish = new Dish(chosen, ExpiryDates.inDays(LocalDate.now(), days));
        if (kitchenService.addDish(dish)) {
            view.showLine(Messages.DISH_ADDED);
        } else {
            view.showLine(Messages.DISH_DUPLICATE);
        }
    }

    private void createThemedMenu() {
        List<Dish> dishes = kitchenService.availableDishes(LocalDate.now());
        if (dishes.isEmpty()) {
            view.showLine(Messages.NO_DISHES_AVAILABLE);
            return;
        }
        String name = ConsoleInput.readNonEmptyString(Messages.MENU_NAME_PROMPT);
        List<Dish> selected = ConsoleInput.selectItems(dishes, Dish::getName,
                Messages.SELECT_DISH_FOR_MENU_PROMPT, Messages.ADD_DISH_TO_MENU_PROMPT);
        int days = ConsoleInput.readIntAtLeast(Messages.MENU_AVAILABLE_DAYS_PROMPT, 0);
        ThemedMenu menu = new ThemedMenu(name, selected, ExpiryDates.inDays(LocalDate.now(), days));
        if (kitchenService.addThemedMenu(menu)) {
            view.showLine(Messages.THEMATIC_MENU_ADDED);
        } else {
            view.showLine(Messages.THEMATIC_MENU_NOT_ADDED);
        }
    }

    private void showMenu() {
        List<String> dishNames = kitchenService.availableDishes(LocalDate.now())
                .stream().map(Dish::getName).toList();
        view.showNamedList(dishNames, Messages.NO_DISHES_AVAILABLE);
    }

    private void resetData() {
        config = new RestaurantConfig(config.getSeats(), config.getWorkloadPerPerson(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new HashMap<>(), new HashMap<>());
        store.save(StorageKeys.RESTAURANT_CONFIG, config);
        store.saveList(StorageKeys.INGREDIENTS, new ArrayList<>());
        store.saveList(StorageKeys.DRINKS, new ArrayList<>());
        store.saveList(StorageKeys.EXTRA_GOODS, new ArrayList<>());
        store.saveList(StorageKeys.RECIPES, new ArrayList<>());
        store.saveList(StorageKeys.DISHES, new ArrayList<>());
        store.saveList(StorageKeys.THEMED_MENUS, new ArrayList<>());
        store.saveList(StorageKeys.RESERVATIONS, new ArrayList<>());
        store.save(StorageKeys.SHOPPING_LIST, new ShoppingList());
        view.showLine(Messages.DATA_RESET);
    }
}
