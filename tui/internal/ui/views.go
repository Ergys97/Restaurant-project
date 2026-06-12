package ui

import "fmt"

func (m Model) render() string {
	switch m.screen {
	case dashboardScreen:
		return m.renderDashboard()
	case reservationsScreen:
		return "Restaurant TUI\n\nPrenotazioni\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	case warehouseScreen:
		return "Restaurant TUI\n\nMagazzino\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	default:
		return "Restaurant TUI\n"
	}
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
