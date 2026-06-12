package api

type RestaurantConfig struct {
	Seats                   int               `json:"seats"`
	WorkloadPerPerson       float64           `json:"workloadPerPerson"`
	Initialized             bool              `json:"initialized"`
	Ingredients             []FoodItem        `json:"ingredients"`
	Drinks                  []FoodItem        `json:"drinks"`
	ExtraGoods              []FoodItem        `json:"extraGoods"`
	PerCapitaDrinks         map[string]int    `json:"perCapitaDrinks"`
	PerCapitaExtraGoods     map[string]int    `json:"perCapitaExtraGoods"`
}

type FoodItem struct {
	Name       string `json:"name"`
	Quantity   int    `json:"quantity"`
	ExpiryDate string `json:"expiryDate"`
	Unit       string `json:"unit"`
}

type ShoppingList struct {
	Ingredients []FoodItem `json:"ingredients"`
	Drinks      []FoodItem `json:"drinks"`
	ExtraGoods  []FoodItem `json:"extraGoods"`
}

type Reservation struct {
	ID         string      `json:"id"`
	Date       string      `json:"date"`
	Covers     int         `json:"covers"`
	DishOrders []DishOrder `json:"dishOrders"`
	MenuOrders []MenuOrder `json:"menuOrders"`
}

type DishOrder struct {
	Dish     NamedEntity `json:"dish"`
	Quantity int         `json:"quantity"`
}

type MenuOrder struct {
	Menu     NamedEntity `json:"menu"`
	Quantity int         `json:"quantity"`
}

type NamedEntity struct {
	Name string `json:"name"`
}

type ReservationRequest struct {
	Date       string             `json:"date"`
	Covers     int                `json:"covers"`
	DishOrders []DishOrderRequest `json:"dishOrders,omitempty"`
	MenuOrders []MenuOrderRequest `json:"menuOrders,omitempty"`
}

type DishOrderRequest struct {
	DishName string `json:"dishName"`
	Quantity int    `json:"quantity"`
}

type MenuOrderRequest struct {
	MenuName string `json:"menuName"`
	Quantity int    `json:"quantity"`
}

type DemoResetSummary struct {
	Reservations int `json:"reservations"`
	Ingredients  int `json:"ingredients"`
	Dishes       int `json:"dishes"`
	Menus        int `json:"menus"`
}
