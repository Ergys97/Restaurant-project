package ui

import "fmt"

func (m Model) render() string {
	switch m.screen {
	case dashboardScreen:
		return m.renderDashboard()
	case reservationsScreen:
		return m.renderReservations()
	case warehouseScreen:
		return "Restaurant TUI\n\nMagazzino\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	default:
		return "Restaurant TUI\n"
	}
}

func (m Model) renderReservations() string {
	s := "Restaurant TUI\n\nPrenotazioni\n\n"
	if m.loading {
		s += "Caricamento...\n"
		return s
	}
	if len(m.reservations) == 0 {
		s += "Nessuna prenotazione\n"
	} else {
		for i, r := range m.reservations {
			cursor := "  "
			if i == m.reservationCursor {
				cursor = "> "
			}
			s += fmt.Sprintf("%s%s - %d coperti - id %s\n", cursor, r.Date, r.Covers, r.ID)
		}
	}
	if m.confirmDelete {
		s += "\nConfermi cancellazione? y/n\n"
	} else {
		s += "\nup/down seleziona  x cancella  n nuova  r aggiorna  [tab] cambia vista  [q] esci\n"
	}
	if m.status != "" {
		s += fmt.Sprintf("\n%s\n", m.status)
	}
	return s
}

func (m Model) renderDashboard() string {
	s := "Restaurant TUI\n\n"
	if m.loading {
		s += "Caricamento...\n"
		return s
	}
	s += "Dashboard\n\n"
	s += fmt.Sprintf("Posti: %d\n", m.config.Seats)
	s += fmt.Sprintf("Prenotazioni: %d\n", len(m.reservations))
	s += fmt.Sprintf("Ingredienti in magazzino: %d\n", len(m.warehouse.Ingredients))
	s += fmt.Sprintf("\n[r] aggiorna  [d] reset demo  [tab] cambia vista  [q] esci\n")
	if m.status != "" {
		s += fmt.Sprintf("\n%s\n", m.status)
	}
	return s
}
