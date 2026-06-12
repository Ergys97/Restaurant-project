package ui

import "restaurant-tui/internal/api"

type dashboardLoadedMsg struct {
	config       api.RestaurantConfig
	reservations []api.Reservation
	warehouse    api.ShoppingList
}

type reservationDeletedMsg struct {
	id string
}

type demoResetMsg struct {
	summary api.DemoResetSummary
}

type reservationCreatedMsg struct {
	reservation api.Reservation
}

type dishesFormLoadedMsg struct {
	dishes []api.DishSummary
}

type apiErrorMsg struct {
	err error
}
