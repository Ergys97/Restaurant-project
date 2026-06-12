# Spring Boot REST API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a Spring Boot REST API that can gradually replace the console UI and serve a future Bubble Tea client.

**Architecture:** Keep the existing model, repository, and service layer as the source of business rules. Add a separate Spring Boot bootstrap under `it.restaurant.api`, REST DTOs/controllers, and a demo data reset path that seeds JSON files through domain services where reservation side effects matter. The console entrypoint remains available during the transition.

**Tech Stack:** Java 21, Spring Boot 3.5.x, Spring Web MVC, Jakarta Bean Validation, Jackson, JUnit 5, MockMvc.

---

## File Structure

- Modify `pom.xml`: adopt Spring Boot parent/dependencies/plugin and keep `it.restaurant.Main` available as a console main class.
- Create `src/main/java/it/restaurant/api/RestaurantApiApplication.java`: Spring Boot application entrypoint.
- Create `src/main/java/it/restaurant/api/config/RestaurantProperties.java`: data directory and demo reset properties.
- Create `src/main/java/it/restaurant/api/config/RestaurantBeans.java`: Spring bean wiring for `DataStore`, services, config, and observer chain.
- Create `src/main/java/it/restaurant/api/demo/DemoDataSeeder.java`: builds coherent demo data.
- Create `src/main/java/it/restaurant/api/demo/DemoResetService.java`: clears and reseeds data.
- Create `src/main/java/it/restaurant/api/dto/*`: request/response DTOs for config, reservations, warehouse, and errors.
- Create `src/main/java/it/restaurant/api/web/*Controller.java`: REST endpoints.
- Modify `src/main/java/it/restaurant/model/Reservation.java`: add stable `id`.
- Modify `src/main/java/it/restaurant/service/ReservationService.java`: add create/list/cancel methods backed by `DataStore`.
- Add tests under `src/test/java/it/restaurant/api` and extend service tests.

## Task 1: Build and Spring Bootstrap

- [ ] Write a failing Spring context test that loads `RestaurantApiApplication` with a temp data directory.
- [ ] Update `pom.xml` with Spring Boot dependencies and plugin.
- [ ] Add application/config bean classes.
- [ ] Run the context test and existing unit tests.

## Task 2: Demo Seed and Reset

- [ ] Write a failing test proving demo reset creates config, stock, recipes, dishes, menus, reservations, and shopping list data.
- [ ] Implement demo seed and explicit reset service.
- [ ] Add `POST /api/admin/demo/reset`, guarded by `restaurant.demo.reset-enabled`.
- [ ] Test enabled and disabled reset behavior.

## Task 3: Config and Warehouse API

- [ ] Write failing MockMvc tests for `GET /api/config`, `PUT /api/config`, and `GET /api/warehouse`.
- [ ] Implement DTOs and controllers.
- [ ] Verify validation errors return JSON error responses.

## Task 4: Reservation API

- [ ] Write failing tests for listing, creating, and deleting reservations by stable id.
- [ ] Add `Reservation.id`, service methods, DTO mapping, and controller.
- [ ] Ensure create/delete use service logic and observer effects.

## Task 5: Verification

- [ ] Run `mvn test`.
- [ ] Run `mvn package`.
- [ ] Report changed files, tests run, and any limitations.

## Self-Review

- Scope is intentionally limited to the first API slice needed by a future Bubble Tea client.
- Demo reset is explicit and property-gated.
- The console remains during gradual replacement.
- No database, authentication, or Go TUI implementation is included in this plan.
