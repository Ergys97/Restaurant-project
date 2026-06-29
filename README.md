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

## REST API + Bubble Tea TUI

The project includes a Go TUI (Terminal User Interface) that communicates with
the Spring Boot REST API.

### One-command startup

```powershell
.\run.ps1
```

This builds the backend jar, starts the Spring Boot API, waits for readiness, then launches the TUI.

### Start separately

Backend first:

```powershell
mvn package
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

Then TUI:

```powershell
go run .\tui\cmd\restaurant-tui
```

Optional custom backend URL:

```powershell
$env:RESTAURANT_API_URL="http://localhost:8080"
go run .\tui\cmd\restaurant-tui
```

### TUI commands

| Key | Action |
|-----|--------|
| `Tab` / `Right` | Next screen |
| `Left` / `Shift+Tab` | Previous screen |
| `r` | Refresh data |
| `d` | Reset demo data |
| `n` | New reservation (reservations screen) |
| `x` | Delete selected reservation |
| `y` | Confirm deletion |
| `Esc` | Cancel / close |
| `q` / `Ctrl+C` | Quit |

### Run TUI tests

```powershell
go test .\tui\...
```

## Sviluppi futuri

### Alta priorità
- **Autenticazione REST API** — Proteggere gli endpoint con Spring Security e JWT, con ruoli distinti per Gestore, Addetto e Magazziniere.
- **Gestione scadenze ingredienti** — Alert automatici e/o endpoint dedicato (`/api/warehouse/expiring?days=N`) per segnalare i prodotti in scadenza.
- **Documentazione API (Swagger/OpenAPI)** — Integrare `springdoc-openapi` per generare documentazione interattiva su `/swagger-ui.html`.

### Media priorità
- **Filtri e paginazione** — Aggiungere filtri per data e paginazione agli endpoint di lista (`/api/reservations`, `/api/dishes`).
- **Storico prenotazioni** — Archiviare le prenotazioni scadute anziché eliminarle, per abilitare statistiche e analytics.
- **Gestione tavoli** — Associare prenotazioni a tavoli specifici con capacità individuale, non solo conteggio posti globale.
- **Docker + docker-compose** — Containerizzare backend Java e TUI Go per semplificare il deploy.

### Bassa priorità
- **Migrazione a database reale** — Passare da `JsonDataStore` a SQLite/H2 con Spring Data JPA. L'interfaccia `DataStore` esistente rende il refactoring pulito.
- **WebSocket per notifiche real-time** — Sostituire il flag booleano di `ReservationNotifier` con WebSocket per notifiche push al magazziniere nel TUI.
- **Test Go nel TUI** — Ampliare la copertura dei test del client Go Bubble Tea.

## History

Originally developed as a university project. Modernised with:
Maven build, MVC package structure, JSON persistence (Jackson), and CI via
GitHub Actions.

**UI text is in Italian** — all user-facing menus, prompts, and messages
are written in Italian.
