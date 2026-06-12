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

type apiErrorMsg struct {
	err error
}
