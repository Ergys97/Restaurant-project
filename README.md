# Restaurant Management System

A comprehensive restaurant management application entirely written in Java, developed as a university project by Ergys & Enrico.

## 📋 Overview

This is a terminal-based restaurant management system that handles the complete workflow of a restaurant, from menu creation to reservation management and inventory control. The application implements an Observer pattern for real-time updates and uses file serialization for data persistence.

## ✨ Features

The application provides three different user roles, each with specific functionalities:

### 🔧 Manager (Gestore)
- **Restaurant Structure Setup**: Initialize and configure the restaurant's basic parameters (seating capacity, workload capacity)
- **Ingredient Management**: Create and manage ingredients inventory
- **Recipe Management**: Create recipes using available ingredients with workload calculations
- **Dish Management**: Create dishes from recipes and ingredients
- **Thematic Menu Creation**: Design themed menus with multiple dishes
- **À la Carte Menu**: View all available dishes
- **Data Reset**: Clear all restaurant data

### 📅 Reservation Manager (Addetto delle Prenotazioni)
- **Collect Reservations**: Create new reservations with customer details, date, time, and orders
- **View Reservations**: Display all current reservations
- **Automatic Validation**: System validates reservations based on:
  - Available seating capacity
  - Kitchen workload sustainability
  - Ingredient availability

### 📦 Warehouse Manager (Magazziniere)
- **View Inventory**: Display current stock of ingredients, beverages, and food items
- **Shopping List**: Automatically generates shopping lists based on reservations
- **Stock Replenishment**: Process and update inventory after restocking
- **Real-time Notifications**: Receive alerts when ingredients are needed for upcoming reservations

## 🏗️ Architecture

The application is organized into three main packages:

- **`reservationSystem`**: Core business logic (24 classes)
  - Domain models: `Prenotazione`, `Piatto`, `Ricetta`, `Ingrediente`, `MenuTematico`
  - Management: `GestionePrenotazione`, `StrutturaRistorante`, `RegistroMagazzino`
  - Observer pattern implementation: `Osservabile`, `Osservatori`
  - File operations: `SerializableFileOperation`
  
- **`reservationTest`**: Unit tests

- **`reservationUtility`**: Utility classes and helpers

### Design Patterns Used
- **Observer Pattern**: For real-time updates between reservation system, inventory, and shopping list
- **Serialization**: For persistent data storage

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Terminal/Command Prompt

### Installation & Execution

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ergys97/Restaurant-project.git
   cd Restaurant-project
   ```

2. **Compile the project**
   ```bash
   javac -d bin src/reservationSystem/*.java src/reservationUtility/*.java
   ```

3. **Run the application**
   ```bash
   java -cp bin reservationSystem.Main
   ```

### First-Time Setup

When you run the application for the first time:

1. **Select "Gestore" (Manager)** from the main menu
2. **Configure the restaurant structure**:
   - Set the number of available seats
   - Define the sustainable workload for the kitchen
   - Add beverages and extra food items
3. **Create ingredients** that will be used in recipes
4. **Create recipes** combining ingredients
5. **Create dishes** from your recipes
6. **Create thematic menus** (optional) or use à la carte

After setup, you can switch between the three user roles to manage different aspects of the restaurant.

## 🎯 Usage Flow

```
1. Manager sets up restaurant → Creates ingredients → Creates recipes → Creates dishes/menus
2. Reservation Manager accepts customer reservations
3. System automatically:
   - Validates capacity and workload
   - Deducts ingredients from inventory
   - Adds items to shopping list
4. Warehouse Manager checks inventory and restocks as needed
```

## 📁 Data Persistence

All data is automatically saved to serialized files:
- Restaurant structure configuration
- Ingredients, beverages, and food items inventory
- Recipes and dishes
- Thematic menus
- Reservations
- Shopping lists

Data persists between application sessions.

## ⚠️ Important Notes

- **Terminal-only interface**: No graphical user interface (GUI)
- **Restaurant structure**: Once initialized, basic parameters cannot be modified
- **Automatic inventory management**: Ingredients are automatically deducted when reservations are confirmed
- **Italian language**: Some menu options and prompts are in Italian

## 🛠️ Technologies

- **Language**: Java
- **Serialization**: Java Object Serialization for data persistence
- **Pattern**: Observer pattern for event-driven updates
- **Interface**: Command-line interface (CLI)

## 👥 Authors

- Ergys
- Enrico

## 📝 License

University project - Educational purposes

---

**Note**: This was developed as a university task to demonstrate object-oriented programming concepts, design patterns, and Java fundamentals.
