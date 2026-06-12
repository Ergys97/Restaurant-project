package ui

func (m Model) render() string {
	switch m.screen {
	case dashboardScreen:
		return "Restaurant TUI\n\nDashboard\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	case reservationsScreen:
		return "Restaurant TUI\n\nPrenotazioni\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	case warehouseScreen:
		return "Restaurant TUI\n\nMagazzino\n\nTab/Right: avanti  Left: indietro  q: esci\n"
	default:
		return "Restaurant TUI\n"
	}
}
