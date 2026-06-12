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

type apiErrorMsg struct {
	err error
}
