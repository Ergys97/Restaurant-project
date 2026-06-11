# Restaurant Project Modernization — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Trasformare l'app console Java in un progetto Maven con architettura a layer (model/repository/service/controller/view), codice in inglese, persistenza JSON, test JUnit 5, CI e fat-jar eseguibile.

**Architecture:** Approccio bottom-up incrementale: prima la rete di sicurezza (Maven + CI col codice attuale invariato), poi i nuovi package costruiti accanto ai vecchi (`it.restaurant.*` convive con `reservationSystem.*` finché compila tutto), infine lo swap di `Main` e la cancellazione dei vecchi package. Il progetto compila a ogni task.

**Tech Stack:** Java 21, Maven, Jackson (databind + jsr310), JUnit 5, maven-shade-plugin, GitHub Actions.

**Spec:** `docs/superpowers/specs/2026-06-12-restaurant-modernization-design.md`

> ⚠️ **REGOLA GIT (override del formato standard):** l'utente ha chiesto esplicitamente di NON committare. Nessun task esegue `git commit`. Ogni task termina con un **Checkpoint** che indica il punto di commit consigliato: è l'utente a committare. `git rm --cached` (solo staging, nessun commit) è consentito nel Task 1 perché necessario per l'untracking.

> **Nota su `LocalDate.now()`:** il codice originale la usa ovunque. Nei service nuovi viene iniettata via costruttore (`Clock` non serve: basta passare `LocalDate today` ai metodi o un `Supplier<LocalDate>`); qui si usa la forma semplice: i metodi che dipendono dalla data la ricevono come parametro, così i test non dipendono dal giorno di esecuzione.

---

## Mappa di traduzione (riferimento per tutti i task)

| Vecchio | Nuovo | Note |
|---|---|---|
| `Alimento` | `model.FoodItem` | astratta |
| `Ingrediente` | `model.Ingredient` | unit "KG" |
| `Bevanda` | `model.Drink` | unit "L" |
| `GeneriAlimentari` | `model.ExtraGood` | unit "HG" |
| `Ricetta` | `model.Recipe` | |
| `Piatto` | `model.Dish` | |
| `MenuTematico` | `model.ThemedMenu` | |
| `Prenotazione` | `model.Reservation` | snellita: niente posti max/carico sostenibile |
| — | `model.DishOrder`, `model.MenuOrder` | sostituiscono `HashMap<Piatto,Integer>` / `HashMap<MenuTematico,Integer>` |
| `ListaSpesa` | `model.ShoppingList` | |
| `StrutturaRistorante` | `model.RestaurantConfig` | mappe per-capita diventano `Map<String,Integer>` (chiave = nome) |
| `UtilityElenchi` | `model.FoodItems` (helper statico) | uniqueness/merge per nome |
| `FileOperation` | `repository.DataStore` | type-safe, `Optional` |
| `SerializableFileOperation` + `ServizioFile` | `repository.JsonDataStore` | Jackson |
| `Osservatori`/`Osservabile` | `service.event.ReservationObserver` | observable = `ReservationService` |
| `GestionePrenotazione` + logica di `Prenotazione` | `service.ReservationService` | |
| `EliminaAlimentiPrenotazione` | `service.event.StockUpdater` | |
| `PrenotazioneToListaSpesa` | `service.event.ShoppingListUpdater` | |
| `GestioneElencoPrenotazioni` | `service.event.ReservationRegistry` | fix bug remove-durante-iterazione |
| `NotificaIngredientiPrenotazione` | `service.event.ReservationNotifier` (flag) + logica in `WarehouseService`/`WarehouseController` | |
| `RegistroMagazzino` | `service.WarehouseService` | |
| `UtilityElenchi` (filtro piatti) | `service.KitchenService` | |
| `UtilityTime` + `NumeriCasuali` | `util.ExpiryDates` | |
| `IterazioneUtente` (stampe) | `view.ConsoleView` | |
| `IterazioneUtente` (letture) + `InputDati` | `view.ConsoleInput` | |
| `MyMenu` | `view.Menu` | |
| `MessaggiApplicazione` | `view.Messages` | contenuti restano in italiano |
| `Main.gestioneRistorante` | `controller.ManagerController` | |
| `Main.gestionePrenotazioni` | `controller.ReservationController` | |
| `Main.gestioneMagazzino` | `controller.WarehouseController` | |
| `Utility`, `EstrazioniCasuali` | eliminati (verificare che siano inutilizzati con grep) | |

Glossario termini di dominio: carico di lavoro → *workload*; posti a sedere → *seats*; coperti → *covers*; data di scadenza → *expiry date*; lista della spesa → *shopping list*; consumo pro capite → *per-capita consumption*.

---

## Task 0: Prerequisiti

**Files:** nessuno.

- [ ] **Step 0.1: Verifica toolchain**

Run: `java -version` → atteso `openjdk version "21.x"`.
Run: `mvn -version` → atteso Maven ≥ 3.9. Se assente: `winget install Apache.Maven` (o scaricare da maven.apache.org e aggiungere `bin` al PATH), poi riaprire il terminale e riverificare.

---

## Task 1: Pulizia git (.gitignore + untrack dei file generati)

**Files:**
- Create: `.gitignore`
- Untrack (restano su disco): `bin/**`, `.classpath`, `.project`, `.settings/**`

- [ ] **Step 1.1: Crea `.gitignore`**

```gitignore
# Build
target/
bin/

# Dati runtime dell'applicazione
data/
*.txt

# IDE
.classpath
.project
.settings/
.idea/
*.iml
.vscode/
```

Nota: `*.txt` ignora i vecchi file di serializzazione (`Prenotazioni.txt`, ecc.) eventualmente presenti nella working directory. Non esistono `.txt` legittimi nel repo.

- [ ] **Step 1.2: Untrack dei file generati (staging only, NESSUN commit)**

```powershell
git rm -r --cached bin .classpath .project .settings
git add .gitignore
git status
```

Expected: i file compaiono come `deleted:` in staging e `.gitignore` come `new file:`. I file restano su disco.

- [ ] **Checkpoint:** commit consigliato (utente): `chore: remove generated/IDE files from tracking, add .gitignore`

---

## Task 2: Migrazione al layout Maven (codice attuale invariato)

**Files:**
- Create: `pom.xml`
- Move: `src/reservationSystem/**` → `src/main/java/reservationSystem/**`
- Move: `src/reservationUtility/**` → `src/main/java/reservationUtility/**`
- Move: `src/reservationTest/**` → `src/test/java/reservationTest/**`

- [ ] **Step 2.1: Crea `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>it.restaurant</groupId>
  <artifactId>restaurant</artifactId>
  <version>1.0.0</version>
  <packaging>jar</packaging>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jackson.version>2.17.1</jackson.version>
    <junit.version>5.10.2</junit.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>${jackson.version}</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-jsr310</artifactId>
      <version>${jackson.version}</version>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${junit.version}</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <finalName>restaurant</finalName>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.2.5</version>
      </plugin>
    </plugins>
  </build>
</project>
```

- [ ] **Step 2.2: Sposta i sorgenti nel layout standard**

```powershell
New-Item -ItemType Directory -Force src/main/java, src/test/java
git mv src/reservationSystem src/main/java/reservationSystem
git mv src/reservationUtility src/main/java/reservationUtility
git mv src/reservationTest src/test/java/reservationTest
```

- [ ] **Step 2.3: Compila ed esegui i test**

Run: `mvn test`
Expected: BUILD SUCCESS, 5+ test eseguiti. Possibili intoppi da sistemare al volo:
- `TestVario.java`: se contiene esperimenti non compilabili o vuoti, valutare se è un test reale; se è scratch code, eliminarlo (`git rm src/test/java/reservationTest/TestVario.java`).
- Import inutilizzati di `java.io.File` in `Ricetta`/`Piatto`/`MenuTematico`/`StrutturaRistorante`: non bloccano la build, si puliranno con la riscrittura.

- [ ] **Step 2.4: Verifica che l'app parta ancora**

Run: `mvn -q compile exec:java "-Dexec.mainClass=reservationSystem.Main"` — oppure `java -cp target/classes reservationSystem.Main` dopo `mvn compile`.
Expected: stampa `---Ristorante di Ergys & Enrico---` e il menu. Uscire con `0`.

- [ ] **Checkpoint:** commit consigliato: `build: migrate to Maven standard layout`

---

## Task 3: CI con GitHub Actions

**Files:**
- Create: `.github/workflows/ci.yml`

- [ ] **Step 3.1: Crea il workflow**

```yaml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - name: Build and test
        run: mvn --batch-mode verify
```

- [ ] **Step 3.2: Verifica locale dell'equivalente comando CI**

Run: `mvn --batch-mode verify`
Expected: BUILD SUCCESS.

- [ ] **Checkpoint:** commit consigliato: `ci: add GitHub Actions workflow`. La CI si attiverà al primo push.

---

## Task 4: Package `model` (entità tradotte, nuove `DishOrder`/`MenuOrder`)

**Files:**
- Create: `src/main/java/it/restaurant/model/FoodItem.java`
- Create: `src/main/java/it/restaurant/model/Ingredient.java`
- Create: `src/main/java/it/restaurant/model/Drink.java`
- Create: `src/main/java/it/restaurant/model/ExtraGood.java`
- Create: `src/main/java/it/restaurant/model/Recipe.java`
- Create: `src/main/java/it/restaurant/model/Dish.java`
- Create: `src/main/java/it/restaurant/model/ThemedMenu.java`
- Create: `src/main/java/it/restaurant/model/DishOrder.java`
- Create: `src/main/java/it/restaurant/model/MenuOrder.java`
- Create: `src/main/java/it/restaurant/model/Reservation.java`
- Create: `src/main/java/it/restaurant/model/ShoppingList.java`
- Create: `src/main/java/it/restaurant/model/RestaurantConfig.java`
- Create: `src/main/java/it/restaurant/model/FoodItems.java`
- Create: `src/main/java/it/restaurant/util/ExpiryDates.java`

Regole comuni: niente `Serializable` (si usa Jackson); ogni classe concreta ha un costruttore no-arg `protected`/`private` per Jackson; `List`/`Map` nelle firme; nessun I/O né `System.out`.

- [ ] **Step 4.1: `FoodItem` (era `Alimento`) — niente più data casuale nel costruttore**

```java
package it.restaurant.model;

import java.time.LocalDate;

public abstract class FoodItem {
    private String name;
    private int quantity;
    private LocalDate expiryDate;

    protected FoodItem() {} // Jackson

    protected FoodItem(String name, int quantity, LocalDate expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public abstract String getUnit();

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpired(LocalDate today) { return expiryDate.isBefore(today); }
}
```

- [ ] **Step 4.2: Le tre sottoclassi**

```java
package it.restaurant.model;

import java.time.LocalDate;

public class Ingredient extends FoodItem {
    private Ingredient() {} // Jackson
    public Ingredient(String name, int quantity, LocalDate expiryDate) { super(name, quantity, expiryDate); }
    @Override public String getUnit() { return "KG"; }
}
```

`Drink` identica con unit `"L"`; `ExtraGood` identica con unit `"HG"`. (La data casuale che prima generava il costruttore di `Alimento` ora viene passata dal chiamante usando `ExpiryDates.random()`, Step 4.9.)

- [ ] **Step 4.3: `FoodItems` (helper, assorbe `UtilityElenchi`)**

```java
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

    /** Somma la quantità se un elemento omonimo esiste già, altrimenti aggiunge. */
    public static <T extends FoodItem> void mergeQuantity(List<T> target, T toAdd) {
        findByName(target, toAdd.getName()).ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + toAdd.getQuantity()),
                () -> target.add(toAdd));
    }
}
```

- [ ] **Step 4.4: `Recipe`, `Dish`, `ThemedMenu`**

```java
package it.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();
    private double workloadPerPortion;
    private int prepTimeMinutes;

    private Recipe() {} // Jackson

    /** workloadPerPortion = workloadPerPerson * fraction (comportamento originale di Ricetta). */
    public Recipe(String name, List<Ingredient> ingredients, double fractionOfPersonWorkload,
                  double workloadPerPerson, int prepTimeMinutes) {
        this.name = name;
        this.ingredients = ingredients;
        this.workloadPerPortion = workloadPerPerson * fractionOfPersonWorkload;
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public String getName() { return name; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public double getWorkloadPerPortion() { return workloadPerPortion; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
}
```

```java
package it.restaurant.model;

import java.time.LocalDate;
import java.util.List;

public class Dish {
    private String name;
    private Recipe recipe;
    private LocalDate availableUntil;

    private Dish() {} // Jackson

    public Dish(Recipe recipe, LocalDate availableUntil) {
        this.recipe = recipe;
        this.availableUntil = availableUntil;
        this.name = recipe.getName();
    }

    public String getName() { return name; }
    public Recipe getRecipe() { return recipe; }
    public LocalDate getAvailableUntil() { return availableUntil; }
    public List<Ingredient> getIngredients() { return recipe.getIngredients(); }
    public double workload() { return recipe.getWorkloadPerPortion(); }

    public boolean isAvailable(LocalDate today) { return !availableUntil.isBefore(today); }

    @Override public String toString() { return "[Dish: " + name + "]"; }
}
```

```java
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
```

Nota: il check `isAccettabile()` (workload menu < workloadPerPerson × 4/3) dipende dalla config del ristorante → diventa `KitchenService.isMenuAcceptable` (Task 6), non resta nel model.

- [ ] **Step 4.5: `DishOrder` e `MenuOrder`**

```java
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
```

`MenuOrder` identica con `ThemedMenu menu` al posto di `Dish dish` e `menu.workload() * quantity`.

- [ ] **Step 4.6: `Reservation` (snellita: 1 costruttore, niente config del ristorante)**

```java
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

    /** Somma la quantità se il piatto è già ordinato (comportamento originale di aggiungiPiatto). */
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

    /** Era caricoLavoroSingolaPrenotazione. */
    public double workload() {
        double dishes = dishOrders.stream().mapToDouble(DishOrder::workload).sum();
        double menus = menuOrders.stream().mapToDouble(MenuOrder::workload).sum();
        return dishes + menus;
    }
}
```

- [ ] **Step 4.7: `ShoppingList`**

```java
package it.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList {
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Drink> drinks = new ArrayList<>();
    private List<ExtraGood> extraGoods = new ArrayList<>();

    public ShoppingList() {}

    public ShoppingList(List<Ingredient> ingredients, List<Drink> drinks, List<ExtraGood> extraGoods) {
        this.ingredients = ingredients;
        this.drinks = drinks;
        this.extraGoods = extraGoods;
    }

    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Drink> getDrinks() { return drinks; }
    public List<ExtraGood> getExtraGoods() { return extraGoods; }

    /** Era aggiungiElementiDaListaSpesa: merge per nome, somma quantità. */
    public void merge(ShoppingList other) {
        other.getIngredients().forEach(i -> FoodItems.mergeQuantity(ingredients, i));
        other.getDrinks().forEach(d -> FoodItems.mergeQuantity(drinks, d));
        other.getExtraGoods().forEach(e -> FoodItems.mergeQuantity(extraGoods, e));
    }

    public boolean isEmpty() {
        return ingredients.isEmpty() && drinks.isEmpty() && extraGoods.isEmpty();
    }
}
```

(I metodi `creaListaElementiFiniti/Scaduti/...` NON vengono portati: la logica di ripristino magazzino diventa per-elemento in `WarehouseService`, Task 6.)

- [ ] **Step 4.8: `RestaurantConfig` — mappe per-capita keyed per nome (JSON-friendly)**

```java
package it.restaurant.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantConfig {
    private static final double SUSTAINABLE_FACTOR = 1.2;

    private int seats;
    private double workloadPerPerson;
    private boolean initialized;
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Drink> drinks = new ArrayList<>();
    private List<ExtraGood> extraGoods = new ArrayList<>();
    /** nome bevanda -> consumo pro capite (era HashMap<Bevanda,Integer>) */
    private Map<String, Integer> perCapitaDrinks = new HashMap<>();
    private Map<String, Integer> perCapitaExtraGoods = new HashMap<>();

    public RestaurantConfig() { this.initialized = false; } // anche per Jackson

    public RestaurantConfig(int seats, double workloadPerPerson,
                            List<Ingredient> ingredients, List<Drink> drinks, List<ExtraGood> extraGoods,
                            Map<String, Integer> perCapitaDrinks, Map<String, Integer> perCapitaExtraGoods) {
        this.seats = seats;
        this.workloadPerPerson = workloadPerPerson;
        this.ingredients = ingredients;
        this.drinks = drinks;
        this.extraGoods = extraGoods;
        this.perCapitaDrinks = perCapitaDrinks;
        this.perCapitaExtraGoods = perCapitaExtraGoods;
        this.initialized = true;
    }

    public double getSustainableWorkload() { return seats * workloadPerPerson * SUSTAINABLE_FACTOR; }

    public int getSeats() { return seats; }
    public double getWorkloadPerPerson() { return workloadPerPerson; }
    public boolean isInitialized() { return initialized; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Drink> getDrinks() { return drinks; }
    public List<ExtraGood> getExtraGoods() { return extraGoods; }
    public Map<String, Integer> getPerCapitaDrinks() { return perCapitaDrinks; }
    public Map<String, Integer> getPerCapitaExtraGoods() { return perCapitaExtraGoods; }
}
```

Nota: `getSustainableWorkload` è derivato → aggiungere `@JsonIgnore` (import `com.fasterxml.jackson.annotation.JsonIgnore`) per non serializzarlo.

- [ ] **Step 4.9: `ExpiryDates` (era `UtilityTime` + `NumeriCasuali`)**

```java
package it.restaurant.util;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public final class ExpiryDates {
    private ExpiryDates() {}

    /** Data di scadenza casuale tra 5 e 10 giorni da oggi (comportamento originale). */
    public static LocalDate random(LocalDate today) {
        return today.plusDays(ThreadLocalRandom.current().nextInt(5, 11));
    }

    public static LocalDate inDays(LocalDate today, int days) {
        return today.plusDays(days);
    }
}
```

- [ ] **Step 4.10: Compila**

Run: `mvn -q compile`
Expected: BUILD SUCCESS (i vecchi package non sono toccati e continuano a compilare).

- [ ] **Checkpoint:** commit consigliato: `feat: add English domain model (it.restaurant.model)`

---

## Task 5: Package `repository` (TDD)

**Files:**
- Create: `src/main/java/it/restaurant/repository/DataStoreException.java`
- Create: `src/main/java/it/restaurant/repository/DataStore.java`
- Create: `src/main/java/it/restaurant/repository/StorageKeys.java`
- Create: `src/main/java/it/restaurant/repository/JsonDataStore.java`
- Test: `src/test/java/it/restaurant/repository/JsonDataStoreTest.java`

- [ ] **Step 5.1: Interfaccia ed eccezione**

```java
package it.restaurant.repository;

public class DataStoreException extends RuntimeException {
    public DataStoreException(String message, Throwable cause) { super(message, cause); }
}
```

```java
package it.restaurant.repository;

import java.util.List;
import java.util.Optional;

public interface DataStore {
    <T> List<T> loadList(String key, Class<T> type);
    <T> void saveList(String key, List<T> items);
    <T> Optional<T> load(String key, Class<T> type);
    <T> void save(String key, T item);
}
```

```java
package it.restaurant.repository;

public final class StorageKeys {
    private StorageKeys() {}
    public static final String RESERVATIONS = "reservations";
    public static final String INGREDIENTS = "ingredients";
    public static final String DRINKS = "drinks";
    public static final String EXTRA_GOODS = "extra-goods";
    public static final String RECIPES = "recipes";
    public static final String DISHES = "dishes";
    public static final String THEMED_MENUS = "themed-menus";
    public static final String RESTAURANT_CONFIG = "restaurant-config";
    public static final String SHOPPING_LIST = "shopping-list";
}
```

- [ ] **Step 5.2: Scrivi i test (falliranno: `JsonDataStore` non esiste)**

```java
package it.restaurant.repository;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonDataStoreTest {

    @TempDir Path tempDir;
    private JsonDataStore store;

    @BeforeEach
    void setUp() { store = new JsonDataStore(tempDir); }

    @Test
    void loadListOnMissingFileReturnsEmptyList() {
        assertTrue(store.loadList(StorageKeys.INGREDIENTS, Ingredient.class).isEmpty());
    }

    @Test
    void loadOnMissingFileReturnsEmptyOptional() {
        assertTrue(store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class).isEmpty());
    }

    @Test
    void listRoundTripPreservesData() {
        LocalDate expiry = LocalDate.of(2026, 7, 1);
        store.saveList(StorageKeys.INGREDIENTS,
                List.of(new Ingredient("farina", 10, expiry), new Ingredient("uova", 24, expiry)));

        List<Ingredient> loaded = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);

        assertEquals(2, loaded.size());
        assertEquals("farina", loaded.get(0).getName());
        assertEquals(10, loaded.get(0).getQuantity());
        assertEquals(expiry, loaded.get(0).getExpiryDate());
        assertEquals("KG", loaded.get(0).getUnit());
    }

    @Test
    void reservationRoundTripPreservesOrders() {
        LocalDate date = LocalDate.of(2026, 7, 1);
        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 15, date)), 0.4, 1.0, 10);
        Dish dish = new Dish(recipe, date.plusDays(10));
        Reservation reservation = new Reservation(date, 2);
        reservation.addDishOrder(dish, 2);
        store.saveList(StorageKeys.RESERVATIONS, List.of(reservation));

        List<Reservation> loaded = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);

        assertEquals(1, loaded.size());
        assertEquals(2, loaded.get(0).getCovers());
        assertEquals(1, loaded.get(0).getDishOrders().size());
        assertEquals(0.4, loaded.get(0).workload(), 1e-9);
    }

    @Test
    void singleObjectRoundTrip() {
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), java.util.Map.of("acqua", 1), java.util.Map.of());
        store.save(StorageKeys.RESTAURANT_CONFIG, config);

        Optional<RestaurantConfig> loaded = store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class);

        assertTrue(loaded.isPresent());
        assertTrue(loaded.get().isInitialized());
        assertEquals(20, loaded.get().getSeats());
        assertEquals(120.0, loaded.get().getSustainableWorkload(), 1e-9);
    }
}
```

- [ ] **Step 5.3: Verifica che falliscano**

Run: `mvn -q test -Dtest=JsonDataStoreTest`
Expected: errore di compilazione "cannot find symbol: JsonDataStore".

- [ ] **Step 5.4: Implementa `JsonDataStore`**

```java
package it.restaurant.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonDataStore implements DataStore {

    private final ObjectMapper mapper;
    private final Path dataDir;

    public JsonDataStore(Path dataDir) {
        this.dataDir = dataDir;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new DataStoreException("Cannot create data directory: " + dataDir, e);
        }
    }

    @Override
    public <T> List<T> loadList(String key, Class<T> type) {
        Path file = fileFor(key);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
        try {
            return mapper.readValue(file.toFile(), listType);
        } catch (IOException e) {
            throw new DataStoreException("Cannot read " + file, e);
        }
    }

    @Override
    public <T> void saveList(String key, List<T> items) {
        write(fileFor(key), items);
    }

    @Override
    public <T> Optional<T> load(String key, Class<T> type) {
        Path file = fileFor(key);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(file.toFile(), type));
        } catch (IOException e) {
            throw new DataStoreException("Cannot read " + file, e);
        }
    }

    @Override
    public <T> void save(String key, T item) {
        write(fileFor(key), item);
    }

    private Path fileFor(String key) { return dataDir.resolve(key + ".json"); }

    private void write(Path file, Object value) {
        try {
            mapper.writeValue(file.toFile(), value);
        } catch (IOException e) {
            throw new DataStoreException("Cannot write " + file, e);
        }
    }
}
```

- [ ] **Step 5.5: Test verdi**

Run: `mvn -q test -Dtest=JsonDataStoreTest`
Expected: 5 test PASS. Se Jackson lamenta costruttori mancanti, verificare i costruttori no-arg del Task 4.

- [ ] **Checkpoint:** commit consigliato: `feat: add JSON persistence layer (DataStore/JsonDataStore)`

---

## Task 6: Package `service` + `service.event` (TDD sulla logica core)

**Files:**
- Create: `src/main/java/it/restaurant/service/ReservationService.java`
- Create: `src/main/java/it/restaurant/service/KitchenService.java`
- Create: `src/main/java/it/restaurant/service/WarehouseService.java`
- Create: `src/main/java/it/restaurant/service/event/ReservationObserver.java`
- Create: `src/main/java/it/restaurant/service/event/StockUpdater.java`
- Create: `src/main/java/it/restaurant/service/event/ShoppingListUpdater.java`
- Create: `src/main/java/it/restaurant/service/event/ReservationRegistry.java`
- Create: `src/main/java/it/restaurant/service/event/ReservationNotifier.java`
- Test: `src/test/java/it/restaurant/service/ReservationServiceTest.java`
- Test: `src/test/java/it/restaurant/service/WarehouseServiceTest.java`

- [ ] **Step 6.1: Test di `ReservationService` (replicano i 2 test storici di `TestPrenotazione` + casi nuovi)**

```java
package it.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);
    private ReservationService service;
    private Dish dish; // workload 0.4 (fraction 0.4 * workloadPerPerson 1.0)

    @BeforeEach
    void setUp() {
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), Map.of(), Map.of());
        service = new ReservationService(config);
        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 15, DAY)), 0.4, 1.0, 10);
        dish = new Dish(recipe, DAY.plusDays(10));
    }

    private Reservation reservationOf(int covers, int dishQty) {
        Reservation r = new Reservation(DAY, covers);
        r.addDishOrder(dish, dishQty);
        return r;
    }

    @Test
    void dayWorkloadSumsReservationsOfThatDay() { // era TestPrenotazione.caricoLavoroPrenotazioniGiornata
        List<Reservation> existing = List.of(reservationOf(1, 1), reservationOf(1, 1));
        assertEquals(0.8, service.dayWorkload(DAY, existing), 1e-9);
    }

    @Test
    void reservationWithinSustainableWorkloadIsAccepted() { // era TestPrenotazione.conformeAlCaricoDiLavoro
        List<Reservation> existing = List.of(reservationOf(1, 1), reservationOf(1, 1));
        assertTrue(service.isSustainable(reservationOf(1, 1), existing));
    }

    @Test
    void reservationExceedingSustainableWorkloadIsRejected() {
        // sustainable = 20 * 5.0 * 1.2 = 120; 301 porzioni * 0.4 = 120.4 > 120
        assertFalse(service.isSustainable(reservationOf(1, 301), new ArrayList<>()));
    }

    @Test
    void availableSeatsSubtractsCoversOfSameDay() {
        List<Reservation> existing = List.of(reservationOf(8, 1), reservationOf(5, 1));
        assertEquals(7, service.availableSeats(DAY, existing));
        assertEquals(20, service.availableSeats(DAY.plusDays(1), existing));
    }

    @Test
    void confirmNotifiesObserversOnlyWhenAcceptable() {
        List<Reservation> notified = new ArrayList<>();
        service.addObserver(notified::add);

        assertTrue(service.confirm(reservationOf(2, 1), new ArrayList<>()));
        assertEquals(1, notified.size());

        assertFalse(service.confirm(null, new ArrayList<>()));
        assertFalse(service.confirm(reservationOf(1, 301), new ArrayList<>()));
        assertEquals(1, notified.size());
    }
}
```

- [ ] **Step 6.2: Verifica che falliscano** — Run: `mvn -q test -Dtest=ReservationServiceTest` → errore di compilazione.

- [ ] **Step 6.3: Implementa observer + `ReservationService`**

```java
package it.restaurant.service.event;

import it.restaurant.model.Reservation;

@FunctionalInterface
public interface ReservationObserver {
    void onReservationConfirmed(Reservation reservation);
}
```

```java
package it.restaurant.service;

import it.restaurant.model.Reservation;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.service.event.ReservationObserver;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private final RestaurantConfig config;
    private final List<ReservationObserver> observers = new ArrayList<>();

    public ReservationService(RestaurantConfig config) { this.config = config; }

    public void addObserver(ReservationObserver observer) { observers.add(observer); }

    /** Era Prenotazione.limiteMassimoPostiaSedere. */
    public int availableSeats(LocalDate date, List<Reservation> existing) {
        int taken = existing.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToInt(Reservation::getCovers)
                .sum();
        return config.getSeats() - taken;
    }

    /** Era Prenotazione.caricoLavoroPrenotazioniGiornata. */
    public double dayWorkload(LocalDate date, List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> r.getDate().equals(date))
                .mapToDouble(Reservation::workload)
                .sum();
    }

    /** Era Prenotazione.conformeAlCaricoLavoro. */
    public boolean isSustainable(Reservation candidate, List<Reservation> existing) {
        double total = dayWorkload(candidate.getDate(), existing) + candidate.workload();
        return total < config.getSustainableWorkload();
    }

    /** Era GestionePrenotazione.valutaPrenotazione. */
    public boolean confirm(Reservation candidate, List<Reservation> existing) {
        if (candidate == null || !isSustainable(candidate, existing)) {
            return false;
        }
        observers.forEach(o -> o.onReservationConfirmed(candidate));
        return true;
    }
}
```

- [ ] **Step 6.4: Test verdi** — Run: `mvn -q test -Dtest=ReservationServiceTest` → 5 PASS.

- [ ] **Step 6.5: Test di `WarehouseService`**

```java
package it.restaurant.service;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.Ingredient;
import it.restaurant.repository.JsonDataStore;
import it.restaurant.repository.StorageKeys;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WarehouseServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 7, 1);
    @TempDir Path tempDir;

    @Test
    void depletedItemIsRestocked() { // soglia 5, ricarica +20
        List<Ingredient> items = new ArrayList<>(List.of(new Ingredient("farina", 3, TODAY.plusDays(5))));
        new WarehouseService(new JsonDataStore(tempDir)).restock(items, TODAY);
        assertEquals(23, items.get(0).getQuantity());
    }

    @Test
    void negativeQuantityResetsToRestockAmount() { // comportamento originale: -x + |x| + 20 = 20
        List<Ingredient> items = new ArrayList<>(List.of(new Ingredient("farina", -7, TODAY.plusDays(5))));
        new WarehouseService(new JsonDataStore(tempDir)).restock(items, TODAY);
        assertEquals(20, items.get(0).getQuantity());
    }

    @Test
    void expiredItemGetsNewFutureExpiryDate() {
        Ingredient expired = new Ingredient("latte", 10, TODAY.minusDays(1));
        new WarehouseService(new JsonDataStore(tempDir)).restock(new ArrayList<>(List.of(expired)), TODAY);
        assertFalse(expired.isExpired(TODAY));
        assertEquals(10, expired.getQuantity()); // non era sotto soglia: quantità invariata
    }

    @Test
    void healthyItemIsUntouched() {
        Ingredient ok = new Ingredient("sale", 50, TODAY.plusDays(9));
        new WarehouseService(new JsonDataStore(tempDir)).restock(new ArrayList<>(List.of(ok)), TODAY);
        assertEquals(50, ok.getQuantity());
        assertEquals(TODAY.plusDays(9), ok.getExpiryDate());
    }
}
```

- [ ] **Step 6.6: Verifica che falliscano** — Run: `mvn -q test -Dtest=WarehouseServiceTest` → errore di compilazione.

- [ ] **Step 6.7: Implementa `WarehouseService`**

```java
package it.restaurant.service;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import java.util.List;

public class WarehouseService {

    static final int DEPLETION_THRESHOLD = 5; // era ListaSpesa.SOGLIA
    static final int RESTOCK_AMOUNT = 20;     // era RegistroMagazzino.SOGLIA

    private final DataStore store;

    public WarehouseService(DataStore store) { this.store = store; }

    public ShoppingList currentStock() {
        return new ShoppingList(
                store.loadList(StorageKeys.INGREDIENTS, Ingredient.class),
                store.loadList(StorageKeys.DRINKS, Drink.class),
                store.loadList(StorageKeys.EXTRA_GOODS, ExtraGood.class));
    }

    /** Era RegistroMagazzino.ripristinoAlimenti: ripristina scaduti e sotto-soglia, poi salva. */
    public void restockAll(LocalDate today) {
        ShoppingList stock = currentStock();
        restock(stock.getIngredients(), today);
        restock(stock.getDrinks(), today);
        restock(stock.getExtraGoods(), today);
        store.saveList(StorageKeys.INGREDIENTS, stock.getIngredients());
        store.saveList(StorageKeys.DRINKS, stock.getDrinks());
        store.saveList(StorageKeys.EXTRA_GOODS, stock.getExtraGoods());
    }

    <T extends FoodItem> void restock(List<T> items, LocalDate today) {
        for (T item : items) {
            if (item.isExpired(today)) {
                item.setExpiryDate(ExpiryDates.random(today));
            }
            if (item.getQuantity() <= DEPLETION_THRESHOLD) {
                int base = Math.max(item.getQuantity(), 0);
                item.setQuantity(base + RESTOCK_AMOUNT);
            }
        }
    }

    /**
     * Era NotificaIngredientiPrenotazione.riptistinaAlimentiListaPrenotazioni + notificaListaPrenotazioni:
     * riversa la lista spesa accumulata nel magazzino, svuota la lista e la ritorna per la stampa.
     */
    public ShoppingList receivePendingShoppingList() {
        ShoppingList pending = store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class)
                .orElseGet(ShoppingList::new);
        if (pending.isEmpty()) {
            return pending;
        }
        ShoppingList stock = currentStock();
        stock.merge(pending);
        store.saveList(StorageKeys.INGREDIENTS, stock.getIngredients());
        store.saveList(StorageKeys.DRINKS, stock.getDrinks());
        store.saveList(StorageKeys.EXTRA_GOODS, stock.getExtraGoods());
        store.save(StorageKeys.SHOPPING_LIST, new ShoppingList());
        return pending;
    }
}
```

- [ ] **Step 6.8: Test verdi** — Run: `mvn -q test -Dtest=WarehouseServiceTest` → 4 PASS.

- [ ] **Step 6.9: `KitchenService` (assorbe `UtilityElenchi` lato cucina; senza test dedicati: logica banale coperta dai test esistenti portati nel Task 9)**

```java
package it.restaurant.service;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import java.time.LocalDate;
import java.util.List;

public class KitchenService {

    private static final double MENU_ACCEPTABILITY_FACTOR = 4.0 / 3.0;

    private final DataStore store;
    private final RestaurantConfig config;

    public KitchenService(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    public List<Recipe> recipes() { return store.loadList(StorageKeys.RECIPES, Recipe.class); }
    public List<Dish> dishes() { return store.loadList(StorageKeys.DISHES, Dish.class); }
    public List<ThemedMenu> themedMenus() { return store.loadList(StorageKeys.THEMED_MENUS, ThemedMenu.class); }

    /** Era UtilityElenchi.piattiDisponibili. */
    public List<Dish> availableDishes(LocalDate today) {
        return dishes().stream().filter(d -> d.isAvailable(today)).toList();
    }

    /** Era UtilityElenchi.aggiungiRicettaAElenco: rifiuta nomi duplicati (case-insensitive). */
    public boolean addRecipe(Recipe recipe) {
        List<Recipe> all = recipes();
        boolean duplicate = all.stream().anyMatch(r -> r.getName().equalsIgnoreCase(recipe.getName()));
        if (duplicate) return false;
        all.add(recipe);
        store.saveList(StorageKeys.RECIPES, all);
        return true;
    }

    /** Era UtilityElenchi.aggiungiPiatto. */
    public boolean addDish(Dish dish) {
        List<Dish> all = dishes();
        boolean duplicate = all.stream().anyMatch(d -> d.getName().equalsIgnoreCase(dish.getName()));
        if (duplicate) return false;
        all.add(dish);
        store.saveList(StorageKeys.DISHES, all);
        return true;
    }

    /** Era MenuTematico.isAccettabile: workload menu < workloadPerPerson * 4/3. */
    public boolean isMenuAcceptable(ThemedMenu menu) {
        return menu.workload() < config.getWorkloadPerPerson() * MENU_ACCEPTABILITY_FACTOR;
    }

    /** Era UtilityElenchi.aggiungiMenuAElenco: nome unico + accettabilità. */
    public boolean addThemedMenu(ThemedMenu menu) {
        if (menu == null || !isMenuAcceptable(menu)) return false;
        List<ThemedMenu> all = themedMenus();
        boolean duplicate = all.stream().anyMatch(m -> m.getName().equalsIgnoreCase(menu.getName()));
        if (duplicate) return false;
        all.add(menu);
        store.saveList(StorageKeys.THEMED_MENUS, all);
        return true;
    }
}
```

(Verificare in `UtilityElenchi.aggiungiMenuAElenco` originale, righe 80+, se il check di accettabilità c'era: se non c'era, è comunque coerente con `isAccettabile` di `MenuTematico` e va documentato nel README come fix.)

- [ ] **Step 6.10: I tre observer**

```java
package it.restaurant.service.event;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;

/** Era EliminaAlimentiPrenotazione: scala dal magazzino gli alimenti consumati dalla prenotazione. */
public class StockUpdater implements ReservationObserver {

    private final DataStore store;
    private final RestaurantConfig config;

    public StockUpdater(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        var ingredients = store.loadList(StorageKeys.INGREDIENTS, Ingredient.class);
        var drinks = store.loadList(StorageKeys.DRINKS, Drink.class);
        var extraGoods = store.loadList(StorageKeys.EXTRA_GOODS, ExtraGood.class);

        for (DishOrder order : reservation.getDishOrders()) {
            consumeDishIngredients(ingredients, order.getDish(), order.getQuantity());
        }
        for (MenuOrder order : reservation.getMenuOrders()) {
            for (Dish dish : order.getMenu().getDishes()) {
                consumeDishIngredients(ingredients, dish, order.getQuantity());
            }
        }
        config.getPerCapitaDrinks().forEach((name, perCapita) ->
                FoodItems.findByName(drinks, name).ifPresent(d ->
                        d.setQuantity(d.getQuantity() - perCapita * reservation.getCovers())));
        config.getPerCapitaExtraGoods().forEach((name, perCapita) ->
                FoodItems.findByName(extraGoods, name).ifPresent(e ->
                        e.setQuantity(e.getQuantity() - perCapita * reservation.getCovers())));

        store.saveList(StorageKeys.INGREDIENTS, ingredients);
        store.saveList(StorageKeys.DRINKS, drinks);
        store.saveList(StorageKeys.EXTRA_GOODS, extraGoods);
    }

    private void consumeDishIngredients(java.util.List<Ingredient> stock, Dish dish, int portions) {
        for (Ingredient needed : dish.getIngredients()) {
            FoodItems.findByName(stock, needed.getName())
                    .ifPresent(i -> i.setQuantity(i.getQuantity() - portions));
        }
    }
}
```

```java
package it.restaurant.service.event;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.util.ExpiryDates;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Era PrenotazioneToListaSpesa: accumula nella lista spesa ciò che la prenotazione consumerà. */
public class ShoppingListUpdater implements ReservationObserver {

    private final DataStore store;
    private final RestaurantConfig config;

    public ShoppingListUpdater(DataStore store, RestaurantConfig config) {
        this.store = store;
        this.config = config;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        LocalDate today = LocalDate.now();
        ShoppingList fromReservation = new ShoppingList();

        for (DishOrder order : reservation.getDishOrders()) {
            addDishIngredients(fromReservation.getIngredients(), order.getDish(), order.getQuantity(), today);
        }
        for (MenuOrder order : reservation.getMenuOrders()) {
            for (Dish dish : order.getMenu().getDishes()) {
                addDishIngredients(fromReservation.getIngredients(), dish, order.getQuantity(), today);
            }
        }
        config.getPerCapitaDrinks().forEach((name, perCapita) ->
                FoodItems.mergeQuantity(fromReservation.getDrinks(),
                        new Drink(name, perCapita * reservation.getCovers(), ExpiryDates.random(today))));
        config.getPerCapitaExtraGoods().forEach((name, perCapita) ->
                FoodItems.mergeQuantity(fromReservation.getExtraGoods(),
                        new ExtraGood(name, perCapita * reservation.getCovers(), ExpiryDates.random(today))));

        ShoppingList saved = store.load(StorageKeys.SHOPPING_LIST, ShoppingList.class)
                .orElseGet(ShoppingList::new);
        saved.merge(fromReservation);
        store.save(StorageKeys.SHOPPING_LIST, saved);
    }

    private void addDishIngredients(List<Ingredient> target, Dish dish, int portions, LocalDate today) {
        for (Ingredient needed : dish.getIngredients()) {
            FoodItems.mergeQuantity(target,
                    new Ingredient(needed.getName(), portions, ExpiryDates.random(today)));
        }
    }
}
```

```java
package it.restaurant.service.event;

import it.restaurant.model.Reservation;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import java.time.LocalDate;
import java.util.List;

/** Era GestioneElencoPrenotazioni: salva la prenotazione ed elimina quelle passate.
 *  Fix: l'originale rimuoveva durante un for-each (ConcurrentModificationException latente). */
public class ReservationRegistry implements ReservationObserver {

    private final DataStore store;
    private final ReservationNotifier notifier;

    public ReservationRegistry(DataStore store, ReservationNotifier notifier) {
        this.store = store;
        this.notifier = notifier;
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        List<Reservation> all = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
        all.removeIf(r -> r.getDate().isBefore(LocalDate.now()));
        all.add(reservation);
        store.saveList(StorageKeys.RESERVATIONS, all);
        notifier.flag();
    }
}
```

```java
package it.restaurant.service.event;

import it.restaurant.model.Reservation;

/** Era NotificaIngredientiPrenotazione (solo il flag; la logica di magazzino è in WarehouseService). */
public class ReservationNotifier implements ReservationObserver {

    private boolean pending = false;

    @Override
    public void onReservationConfirmed(Reservation reservation) { flag(); }

    void flag() { pending = true; }

    /** Ritorna true una sola volta per notifica (comportamento originale di checkPrenotazioni). */
    public boolean consumePending() {
        boolean was = pending;
        pending = false;
        return was;
    }
}
```

- [ ] **Step 6.11: Compila tutto e lancia i test**

Run: `mvn -q test`
Expected: BUILD SUCCESS, vecchi e nuovi test verdi.

- [ ] **Checkpoint:** commit consigliato: `feat: add service layer with observer-based reservation events`

---

## Task 7: Package `view`

**Files:**
- Create: `src/main/java/it/restaurant/view/Messages.java`
- Create: `src/main/java/it/restaurant/view/Menu.java`
- Create: `src/main/java/it/restaurant/view/ConsoleInput.java`
- Create: `src/main/java/it/restaurant/view/ConsoleView.java`

- [ ] **Step 7.1: `Messages`** — copiare TUTTE le costanti da `src/main/java/reservationSystem/MessaggiApplicazione.java` traducendo solo i NOMI delle costanti in inglese (es. `MESSAGGIO_NUMERO_POSTI` → `ASK_SEATS`, `MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO` → `RESTAURANT_NOT_INITIALIZED`, `INTESTAZIONE` → `PROMPT_PREFIX`) e correggendo i typo nei nomi (`MESSAGGGIO_…`, `…_VUTO`). I VALORI (testo italiano mostrato all'utente) restano identici. Classe `public final class Messages` con costruttore privato.

- [ ] **Step 7.2: `Menu` (traduzione 1:1 di `MyMenu`)**

```java
package it.restaurant.view;

public class Menu {

    private static final String FRAME = "--------------------------------";
    private static final String EXIT_OPTION = "0\tEsci";
    private static final String PROMPT = "Digita il numero dell'opzione desiderata > ";

    private final String title;
    private final String[] options;

    public Menu(String title, String[] options) {
        this.title = title;
        this.options = options;
    }

    public int choose() {
        print();
        return ConsoleInput.readInt(PROMPT, 0, options.length);
    }

    private void print() {
        System.out.println(FRAME);
        System.out.println(title);
        System.out.println(FRAME);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + "\t" + options[i]);
        }
        System.out.println();
        System.out.println(EXIT_OPTION);
        System.out.println();
    }
}
```

- [ ] **Step 7.3: `ConsoleInput`** — porting 1:1 dei metodi statici di `InputDati` (stesso `Scanner` statico, stessi messaggi d'errore italiani): `readString`, `readNonEmptyString`, `readInt(String)`, `readInt(String,int,int)`, `readIntAtLeast`, `readNonNegativeInt`, `readDouble(String,double,double)`, `readYesNo`. In più i metodi generici che eliminano i 4 duplicati di `IterazioneUtente`:

```java
/** Era selezionaInsiemeAlimento/Ingrediente/creaInsiemeBevade/Generi/creaListaPiattiMenu.
 *  Fix: l'originale con lista vuota chiamava readInt(msg, 0, -1) — range impossibile. */
public static <T> List<T> selectItems(List<T> items, java.util.function.Function<T, String> label,
                                      String selectPrompt, String addAnotherPrompt) {
    if (items.isEmpty()) {
        return new ArrayList<>();
    }
    List<Integer> picked = new ArrayList<>();
    for (int i = 0; i < items.size(); i++) {
        System.out.println(Messages.PROMPT_PREFIX + i + " - " + label.apply(items.get(i)));
    }
    do {
        int choice = readInt(selectPrompt, 0, items.size() - 1);
        if (!picked.contains(choice)) {
            picked.add(choice);
        }
    } while (readYesNo(addAnotherPrompt));
    return picked.stream().map(items::get).collect(java.util.stream.Collectors.toList());
}

/** Era selezionaRicetta/selezionaPiatto/selezionaMenuTematico. */
public static <T> T selectOne(List<T> items, java.util.function.Function<T, String> label, String prompt) {
    for (int i = 0; i < items.size(); i++) {
        System.out.println(Messages.PROMPT_PREFIX + i + " - " + label.apply(items.get(i)));
    }
    int choice = readInt(prompt, 0, items.size() - 1);
    return items.get(choice);
}
```

- [ ] **Step 7.4: `ConsoleView`** — solo stampe, traduzione dei metodi `stampa*` di `IterazioneUtente`:

```java
package it.restaurant.view;

import it.restaurant.model.*;
import java.util.List;

public class ConsoleView {

    public void showLine(String message) { System.out.println(message); }

    public void showFoodItems(List<? extends FoodItem> items) { // era stampaAlimentiCompleti
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

    public void showShoppingList(ShoppingList list) { // era stampaListaSpesa
        System.out.println(Messages.PROMPT_PREFIX + "Ingredienti");
        showFoodItems(list.getIngredients());
        System.out.println(Messages.PROMPT_PREFIX + "Bevande");
        showFoodItems(list.getDrinks());
        System.out.println(Messages.PROMPT_PREFIX + "Generi Alimentari Extra");
        showFoodItems(list.getExtraGoods());
    }

    public void showReservations(List<Reservation> reservations) { // era stampaPrenotazioni
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

    public void showRestaurantConfig(RestaurantConfig config) { // era stampaValoriRistorante
        if (!config.isInitialized()) {
            System.out.println(Messages.RESTAURANT_NOT_INITIALIZED);
            return;
        }
        System.out.println(Messages.RESTAURANT_FEATURES);
        System.out.println(Messages.RESTAURANT_SEATS + config.getSeats());
        System.out.println(Messages.WORKLOAD_PER_PERSON + config.getWorkloadPerPerson());
        System.out.println(Messages.SUSTAINABLE_WORKLOAD + config.getSustainableWorkload());
    }

    public void showNamedList(List<String> names, String emptyMessage) { // era stampaRicette/Piatti/Menu
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
```

- [ ] **Step 7.5: Compila** — Run: `mvn -q compile` → BUILD SUCCESS.

- [ ] **Checkpoint:** commit consigliato: `feat: add console view layer`

---

## Task 8: Controller, nuovo `Main`, eliminazione dei vecchi package

**Files:**
- Create: `src/main/java/it/restaurant/controller/ManagerController.java`
- Create: `src/main/java/it/restaurant/controller/ReservationController.java`
- Create: `src/main/java/it/restaurant/controller/WarehouseController.java`
- Create: `src/main/java/it/restaurant/Main.java`
- Delete: `src/main/java/reservationSystem/**`, `src/main/java/reservationUtility/**`

Ogni controller: campo `Menu`, dipendenze nel costruttore, metodo `run()` con `do/while` su `choose()`, un metodo privato per voce di menu. La struttura replica i tre `gestione*` di `Main.java` originale; le letture passano da `ConsoleInput`, le stampe da `ConsoleView`, i dati da `DataStore`/service. Il flusso interattivo di creazione (era `IterazioneUtente.creaStrutturaRistorante`, `creaRicetta`, `creaPiatto`, `creaMenuTematico`, `creaPrenotazione`, `selezionaPiattieMenu`) vive nei controller come metodi privati, usando `ConsoleInput.selectItems`/`selectOne`.

- [ ] **Step 8.1: `ReservationController`** (il più rappresentativo — gli altri due seguono lo stesso schema)

```java
package it.restaurant.controller;

import it.restaurant.model.*;
import it.restaurant.repository.DataStore;
import it.restaurant.repository.StorageKeys;
import it.restaurant.service.KitchenService;
import it.restaurant.service.ReservationService;
import it.restaurant.util.ExpiryDates;
import it.restaurant.view.*;
import java.time.LocalDate;
import java.util.List;

public class ReservationController {

    private static final String[] OPTIONS = {"Raccogli prenotazioni", "Visualizza prenotazioni"};

    private final DataStore store;
    private final ReservationService reservationService;
    private final KitchenService kitchenService;
    private final ConsoleView view;
    private final Menu menu = new Menu("Menu addetto delle prenotazioni", OPTIONS);

    public ReservationController(DataStore store, ReservationService reservationService,
                                 KitchenService kitchenService, ConsoleView view) {
        this.store = store;
        this.reservationService = reservationService;
        this.kitchenService = kitchenService;
        this.view = view;
    }

    public void run() {
        int choice;
        do {
            choice = menu.choose();
            switch (choice) {
                case 1 -> collectReservation();
                case 2 -> view.showReservations(store.loadList(StorageKeys.RESERVATIONS, Reservation.class));
                default -> { }
            }
        } while (choice != 0);
    }

    private void collectReservation() { // era IterazioneUtente.creaPrenotazione + valutaPrenotazione
        LocalDate today = LocalDate.now();
        List<ThemedMenu> menus = kitchenService.themedMenus();
        List<Dish> dishes = kitchenService.availableDishes(today);
        if (menus.isEmpty() && dishes.isEmpty()) {
            view.showLine(Messages.NO_MENUS_OR_DISHES);
            return;
        }
        List<Reservation> existing = store.loadList(StorageKeys.RESERVATIONS, Reservation.class);
        int days = ConsoleInput.readIntAtLeast(Messages.ASK_RESERVATION_DAYS, 0);
        LocalDate date = ExpiryDates.inDays(today, days);
        int maxSeats = reservationService.availableSeats(date, existing);
        if (maxSeats <= 0) {
            view.showLine(Messages.RESTAURANT_FULL);
            return;
        }
        int covers = ConsoleInput.readInt(Messages.ASK_COVERS, 1, maxSeats);
        Reservation reservation = new Reservation(date, covers);
        fillOrders(reservation, menus, dishes, covers);
        boolean accepted = reservationService.confirm(reservation, existing);
        view.showLine(accepted ? Messages.RESERVATION_ACCEPTED : Messages.RESERVATION_REJECTED);
    }

    private void fillOrders(Reservation reservation, List<ThemedMenu> menus, List<Dish> dishes, int covers) {
        int remaining = covers; // era selezionaPiattieMenu
        do {
            view.showLine(Messages.REMAINING_PEOPLE + remaining);
            if (ConsoleInput.readYesNo(Messages.ASK_SHOW_THEMED_MENUS) && !menus.isEmpty()) {
                ThemedMenu chosen = ConsoleInput.selectOne(menus, ThemedMenu::getName, Messages.ASK_SELECT_MENU);
                int qty = ConsoleInput.readInt(Messages.ASK_MENU_QUANTITY, 1, remaining);
                reservation.addMenuOrder(chosen, qty);
                remaining -= qty;
            } else if (!dishes.isEmpty()) {
                Dish chosen = ConsoleInput.selectOne(dishes, Dish::getName, Messages.ASK_SELECT_DISH);
                int qty = ConsoleInput.readInt(Messages.ASK_DISH_QUANTITY, 1, remaining);
                reservation.addDishOrder(chosen, qty);
                remaining -= qty;
            }
        } while (remaining > 0);
    }
}
```

- [ ] **Step 8.2: `WarehouseController`**

```java
package it.restaurant.controller;

import it.restaurant.model.ShoppingList;
import it.restaurant.service.WarehouseService;
import it.restaurant.service.event.ReservationNotifier;
import it.restaurant.view.*;
import java.time.LocalDate;

public class WarehouseController {

    private static final String[] OPTIONS = {"Visualizza elementi nel magazzino", "Elabora lista della spesa"};

    private final WarehouseService warehouseService;
    private final ReservationNotifier notifier;
    private final ConsoleView view;
    private final Menu menu = new Menu("Menu magazziniere", OPTIONS);

    public WarehouseController(WarehouseService warehouseService, ReservationNotifier notifier, ConsoleView view) {
        this.warehouseService = warehouseService;
        this.notifier = notifier;
        this.view = view;
    }

    public void run() {
        int choice;
        do {
            checkNotifications(); // era NotificaIngredientiPrenotazione.checkPrenotazioni
            choice = menu.choose();
            switch (choice) {
                case 1 -> view.showShoppingList(warehouseService.currentStock());
                case 2 -> warehouseService.restockAll(LocalDate.now());
                default -> { }
            }
        } while (choice != 0);
    }

    private void checkNotifications() {
        if (notifier.consumePending()) {
            ShoppingList received = warehouseService.receivePendingShoppingList();
            view.showLine(Messages.RESERVATION_FOOD_LIST);
            view.showShoppingList(received);
        }
    }
}
```

- [ ] **Step 8.3: `ManagerController`** — stesso schema, 7 voci come l'originale (`Main.gestioneRistorante` + i metodi `crea*`/`aggiungi*` di `IterazioneUtente`). Punti chiave:
- `run()` inizia caricando `RestaurantConfig` da `store.load(StorageKeys.RESTAURANT_CONFIG, RestaurantConfig.class)`; se assente o `!isInitialized()`, lancia il flusso interattivo `setupRestaurant()` (era `creaStrutturaRistorante`: chiede posti, carico per persona, poi ingredienti/bevande/generi iniziali con `ConsoleInput.selectItems` per gli insiemi pro-capite e `ConsoleInput.readInt` per le quantità pro-capite) e salva config + liste su `store`.
- Voce 1: `view.showRestaurantConfig(config)`.
- Voce 2: aggiungi/visualizza ingredienti — loop `readYesNo`, crea `Ingredient(nome, quantità, ExpiryDates.random(today))`, rifiuta duplicati con `FoodItems.containsName`, salva con `store.saveList(StorageKeys.INGREDIENTS, …)`.
- Voce 3: crea ricetta (era `creaRicetta`): nome, `selectItems` sugli ingredienti, frazione `readDouble(msg, 0.0, 1.0)`, tempo `readIntAtLeast(msg, 1)`, `new Recipe(...)` con `config.getWorkloadPerPerson()`, `kitchenService.addRecipe`.
- Voce 4: crea piatto (era `creaPiatto`): `selectOne` sulle ricette (se vuote → messaggio d'errore), giorni validità → `new Dish(recipe, ExpiryDates.inDays(today, days))`, `kitchenService.addDish`.
- Voce 5: crea menu tematico: `selectItems` sui piatti disponibili, nome, giorni → `new ThemedMenu(...)`, `kitchenService.addThemedMenu`.
- Voce 6: menu alla carta: `view.showNamedList(kitchenService.availableDishes(today).stream().map(Dish::getName).toList(), Messages.NO_DISHES)`.
- Voce 7: azzera dati: salva config nuova + liste vuote per tutte le `StorageKeys` (replica del case 7 originale).

- [ ] **Step 8.4: Nuovo `Main`**

```java
package it.restaurant;

import it.restaurant.controller.*;
import it.restaurant.model.RestaurantConfig;
import it.restaurant.repository.*;
import it.restaurant.service.*;
import it.restaurant.service.event.*;
import it.restaurant.view.*;
import java.nio.file.Path;

public class Main {

    private static final String INTRO = "---Ristorante di Ergys & Enrico---";
    private static final String[] ROLES = {"Gestore", "Addetto delle prenotazioni", "Magazziniere"};

    public static void main(String[] args) {
        System.out.println(INTRO);
        DataStore store = new JsonDataStore(Path.of("data"));
        ConsoleView view = new ConsoleView();

        ManagerController manager = new ManagerController(store, view);
        RestaurantConfig config = manager.ensureConfig(); // carica o inizializza interattivamente

        ReservationService reservationService = new ReservationService(config);
        KitchenService kitchenService = new KitchenService(store, config);
        WarehouseService warehouseService = new WarehouseService(store);
        ReservationNotifier notifier = new ReservationNotifier();
        reservationService.addObserver(new StockUpdater(store, config));
        reservationService.addObserver(new ShoppingListUpdater(store, config));
        reservationService.addObserver(new ReservationRegistry(store, notifier));

        ReservationController reservations =
                new ReservationController(store, reservationService, kitchenService, view);
        WarehouseController warehouse = new WarehouseController(warehouseService, notifier, view);

        Menu roleMenu = new Menu("Benvenuto nella gestione del ristorante! \nScegliere l'utente: ", ROLES);
        int choice;
        do {
            choice = roleMenu.choose();
            switch (choice) {
                case 1 -> manager.run();
                case 2 -> reservations.run();
                case 3 -> warehouse.run();
                default -> { }
            }
        } while (choice != 0);
        System.out.println("\nProgramma chiuso correttamente!");
    }
}
```

Nota: `ManagerController.ensureConfig()` è il metodo pubblico che carica la config o esegue il setup interattivo e la ritorna (differenza voluta rispetto all'originale, che inizializzava solo entrando nel menu gestore: così i flussi prenotazioni/magazzino non lavorano mai con config vuota — documentare nel README).

- [ ] **Step 8.5: Elimina i vecchi package**

```powershell
git rm -r src/main/java/reservationSystem src/main/java/reservationUtility
```

(I vecchi test in `src/test/java/reservationTest` si aggiornano nel Task 9 — temporaneamente non compilano: procedere subito col Task 9.)

- [ ] **Checkpoint** (dopo il Task 9, quando la build è di nuovo verde).

---

## Task 9: Porting dei test storici e verifica completa

**Files:**
- Delete: `src/test/java/reservationTest/**`
- Create: `src/test/java/it/restaurant/model/ReservationTest.java`
- Create: `src/test/java/it/restaurant/model/ShoppingListTest.java`
- Create: `src/test/java/it/restaurant/service/event/StockUpdaterTest.java`

- [ ] **Step 9.1: Rimuovi i vecchi test** — `git rm -r src/test/java/reservationTest`. I 2 test di `TestPrenotazione` sono già replicati in `ReservationServiceTest` (Task 6). Gli altri (`TestListaSpesa`, `TestEliminaAlimentiPrenotazione`, `TestPrenotazioneToListaSpesa`) si riscrivono contro le nuove classi:

- [ ] **Step 9.2: `ShoppingListTest`** (sostituisce `TestListaSpesa`)

```java
package it.restaurant.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ShoppingListTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);

    @Test
    void mergeSumsQuantitiesOfSameNameCaseInsensitive() {
        ShoppingList target = new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("Farina", 5, DAY))), new ArrayList<>(), new ArrayList<>());
        ShoppingList other = new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("farina", 3, DAY), new Ingredient("uova", 6, DAY))),
                new ArrayList<>(), new ArrayList<>());

        target.merge(other);

        assertEquals(2, target.getIngredients().size());
        assertEquals(8, FoodItems.findByName(target.getIngredients(), "farina").orElseThrow().getQuantity());
    }

    @Test
    void isEmptyOnlyWhenAllListsEmpty() {
        assertTrue(new ShoppingList().isEmpty());
        assertFalse(new ShoppingList(
                new ArrayList<>(List.of(new Ingredient("sale", 1, DAY))),
                new ArrayList<>(), new ArrayList<>()).isEmpty());
    }
}
```

- [ ] **Step 9.3: `ReservationTest`**

```java
package it.restaurant.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReservationTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);

    private Dish dish(String name, double fraction) {
        Recipe recipe = new Recipe(name, List.of(new Ingredient(name, 1, DAY)), fraction, 1.0, 10);
        return new Dish(recipe, DAY.plusDays(10));
    }

    @Test
    void addDishOrderMergesSameDish() {
        Reservation r = new Reservation(DAY, 4);
        Dish d = dish("salame", 0.4);
        r.addDishOrder(d, 2);
        r.addDishOrder(d, 1);
        assertEquals(1, r.getDishOrders().size());
        assertEquals(3, r.getDishOrders().get(0).getQuantity());
    }

    @Test
    void workloadSumsDishAndMenuOrders() {
        Reservation r = new Reservation(DAY, 4);
        r.addDishOrder(dish("a", 0.4), 2);                       // 0.8
        r.addMenuOrder(new ThemedMenu("menu", List.of(dish("b", 0.5)), DAY.plusDays(5)), 1); // 0.5
        assertEquals(1.3, r.workload(), 1e-9);
    }
}
```

- [ ] **Step 9.4: `StockUpdaterTest`** (sostituisce `TestEliminaAlimentiPrenotazione`/`TestPrenotazioneToListaSpesa` — usa `JsonDataStore` su `@TempDir`)

```java
package it.restaurant.service.event;

import static org.junit.jupiter.api.Assertions.*;

import it.restaurant.model.*;
import it.restaurant.repository.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StockUpdaterTest {

    private static final LocalDate DAY = LocalDate.of(2026, 7, 1);
    @TempDir Path tempDir;

    @Test
    void confirmedReservationDecreasesStock() {
        JsonDataStore store = new JsonDataStore(tempDir);
        store.saveList(StorageKeys.INGREDIENTS, List.of(new Ingredient("salame", 15, DAY.plusDays(5))));
        store.saveList(StorageKeys.DRINKS, List.of(new Drink("acqua", 30, DAY.plusDays(5))));
        RestaurantConfig config = new RestaurantConfig(20, 5.0,
                List.of(), List.of(), List.of(), Map.of("acqua", 2), Map.of());

        Recipe recipe = new Recipe("salame", List.of(new Ingredient("salame", 1, DAY)), 0.4, 5.0, 10);
        Reservation reservation = new Reservation(DAY, 3); // 3 coperti
        reservation.addDishOrder(new Dish(recipe, DAY.plusDays(10)), 4); // 4 porzioni

        new StockUpdater(store, config).onReservationConfirmed(reservation);

        assertEquals(11, store.loadList(StorageKeys.INGREDIENTS, Ingredient.class).get(0).getQuantity()); // 15-4
        assertEquals(24, store.loadList(StorageKeys.DRINKS, Drink.class).get(0).getQuantity()); // 30-2*3
    }
}
```

- [ ] **Step 9.5: Suite completa verde**

Run: `mvn --batch-mode verify`
Expected: BUILD SUCCESS, ~20 test PASS, zero riferimenti residui ai vecchi package (`grep -r "reservationSystem" src/` vuoto).

- [ ] **Checkpoint:** commit consigliato (copre Task 8+9): `refactor!: replace legacy packages with layered it.restaurant architecture`

---

## Task 10: Fat-jar eseguibile

**Files:**
- Modify: `pom.xml` (sezione `<plugins>`)

- [ ] **Step 10.1: Aggiungi maven-shade-plugin**

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.5.2</version>
  <executions>
    <execution>
      <phase>package</phase>
      <goals><goal>shade</goal></goals>
      <configuration>
        <transformers>
          <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
            <mainClass>it.restaurant.Main</mainClass>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

- [ ] **Step 10.2: Build e smoke test manuale**

```powershell
mvn -q package
java -jar target/restaurant.jar
```

Expected: l'app parte, chiede l'inizializzazione del ristorante; completare un giro minimo (init → ingrediente → ricetta → piatto → prenotazione → magazzino) e verificare che `data/*.json` siano leggibili. Uscire con `0`.

- [ ] **Checkpoint:** commit consigliato: `build: produce executable fat-jar via maven-shade`

---

## Task 11: README

**Files:**
- Modify: `README.md` (riscrittura completa, in inglese)

- [ ] **Step 11.1: Riscrivi il README** con queste sezioni: titolo + badge CI (`![CI](https://github.com/<user>/Restaurant-project/actions/workflows/ci.yml/badge.svg)`); descrizione (console app per gestione ristorante: 3 ruoli — manager, booking clerk, warehouse worker); **Architecture** (diagramma testuale dei layer model/repository/service/controller/view + nota sul pattern Observer per gli eventi prenotazione); **Build & Run** (`mvn package`, `java -jar target/restaurant.jar`, requisito Java 21); **Data** (file JSON in `data/`); **Testing** (`mvn test`); **History** (nato come progetto universitario, modernizzato: Maven, MVC, JSON, CI — link alla spec in `docs/`). UI in italiano: dirlo esplicitamente ("UI text is in Italian").

- [ ] **Step 11.2: Verifica finale completa**

```powershell
mvn --batch-mode verify
java -jar target/restaurant.jar
```

Expected: build verde; app funzionante. Confronto con i criteri di successo della spec: nessuna classe > ~300 righe, nessun `System.out` fuori da `view` (`grep -rn "System.out" src/main/java/it/restaurant --include=*.java | grep -v view` → solo `Main` per l'intro, accettabile, o spostare anche quello in `ConsoleView`), nessun file I/O fuori da `repository`.

- [ ] **Checkpoint:** commit consigliato: `docs: rewrite README for modernized project`

---

## Self-review del piano (eseguita)

- **Copertura spec:** gitignore/untrack (T1), Maven (T2), CI (T3), model+correzioni modellazione (T4), JSON/Optional/DataStoreException/data-dir (T5), service+observer+fix bug remove-in-iterazione (T6), view+selectItems+fix lista vuota (T7), controller+Main (T8), test ~20 (T5/6/9), fat-jar (T10), README (T11). Release GitHub: a carico utente (spec). ✔
- **Tipi coerenti tra task:** `DataStore.loadList(String, Class<T>)` usato identico in T5/T6/T8/T9; `ReservationObserver.onReservationConfirmed` identico in T6/T8; `ConsoleInput.selectItems/selectOne/readInt/readIntAtLeast/readYesNo` coerenti tra T7 e T8; costruttori model T4 usati in T5/T6/T9. ✔
- **Niente placeholder:** gli unici punti "per riferimento" sono copie 1:1 da file esistenti nel repo (`Messages` ← `MessaggiApplicazione`, `ConsoleInput` ← `InputDati`) e `ManagerController` (Step 8.3) descritto voce per voce con i metodi esatti da usare. ✔
