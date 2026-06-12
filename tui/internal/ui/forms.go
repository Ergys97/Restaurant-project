package ui

type reservationForm struct {
	date     string
	covers   string
	dishName string
	quantity string
	field    int
}

func newReservationForm() reservationForm {
	return reservationForm{
		date:     "2026-07-01",
		covers:   "2",
		dishName: "Pizza Margherita",
		quantity: "2",
	}
}
