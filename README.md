# Restaurant Management System

![CI](https://github.com/ergys14/Restaurant-project/actions/workflows/ci.yml/badge.svg)

Console application for restaurant management with three user roles:
**Manager**, **Booking Clerk**, and **Warehouse Worker**.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                        Main.java                        │
│  (wires everything, starts the CLI loop)                │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│  Controller (ManagerController, ReservationController,  │
│             WarehouseController)                        │
│  Orchestrates user actions via service layer.           │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│  Service  (ReservationService, KitchenService,          │
│            WarehouseService)                            │
│  Business logic, validation, event dispatch.            │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│  Repository (DataStore, JsonDataStore)                  │
│  Persistence layer — reads/writes JSON to data/ dir.    │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│  Model    (Reservation, Dish, Recipe, Ingredient, ...)  │
│  Domain objects used across all layers.                 │
└──────────┬──────────────────────────────────────────────┘
           │
┌──────────▼──────────────────────────────────────────────┐
│  View     (ConsoleView, Menu, ConsoleInput, Messages)   │
│  Terminal I/O — all System.out confined to this layer.  │
└─────────────────────────────────────────────────────────┘
```

**Observer pattern** — `ReservationNotifier` / `ReservationObserver` decouple
reservation events from downstream effects (stock deduction, shopping-list
regeneration, reservation registry updates).

## Build & Run

```bash
mvn package
java -jar target/restaurant.jar
```

Requires **Java 21**.

## Data

All data is persisted as JSON files inside the `data/` directory at the project
root. The directory is created automatically on first run if it does not exist.

## Testing

```bash
mvn test
```

## History

Originally developed as a university project. Modernised with:
Maven build, MVC package structure, JSON persistence (Jackson), and CI via
GitHub Actions.

**UI text is in Italian** — all user-facing menus, prompts, and messages
are written in Italian.
