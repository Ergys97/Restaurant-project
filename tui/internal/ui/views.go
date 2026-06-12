package ui

import "fmt"

func (m Model) render() string {
	switch m.screen {
	case dashboardScreen:
		return m.renderDashboard()
	case reservationsScreen:
		return m.renderReservations()
	case warehouseScreen:
		return m.renderWarehouse()
	default:
		return "Restaurant TUI\n"
	}
}

func (m Model) renderReservations() string {
	if m.creating {
		return m.renderForm()
	}
	s := titleStyle.Render("Restaurant TUI") + "\n\nPrenotazioni\n\n"
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
				cursor = cursorStyle.Render("> ")
			}
			s += fmt.Sprintf("%s%s - %d coperti - id %s\n", cursor, r.Date, r.Covers, r.ID)
		}
	}
	if m.confirmDelete {
		s += "\nConfermi cancellazione? y/n\n"
	} else {
		s += helpStyle.Render("\nup/down seleziona  x cancella  n nuova  r aggiorna  [tab] cambia vista  [q] esci") + "\n"
	}
	if m.err != nil {
		s += "\n" + errorStyle.Render(m.err.Error()) + "\n"
	} else if m.status != "" {
		s += "\n" + statusStyle.Render(m.status) + "\n"
	}
	return s
}

func (m Model) renderForm() string {
	if m.dishesLoading {
		return titleStyle.Render("Restaurant TUI") + "\n\nCaricamento piatti disponibili...\n"
	}
	s := titleStyle.Render("Restaurant TUI") + "\n\nNuova prenotazione\n\n"

	dateLabel := "Data: " + m.form.date
	if m.form.field == 0 {
		dateLabel = highlightBg.Render(dateLabel)
	}
	s += dateLabel + "\n"

	coversLabel := "Coperti: " + m.form.covers
	if m.form.field == 1 {
		coversLabel = highlightBg.Render(coversLabel)
	}
	s += coversLabel + "\n"

	s += "\nPiatti disponibili:\n"
	for i, d := range m.dishes {
		cursor := "  "
		if i == m.form.dishCursor {
			cursor = cursorStyle.Render("> ")
		}
		line := fmt.Sprintf("%s%s", cursor, d.Name)
		if m.form.field == 2 && i == m.form.dishCursor {
			line = highlightBg.Render(line)
		}
		s += "  " + line + "\n"
	}

	qtyLabel := fmt.Sprintf("Quantita': %s", m.form.quantity)
	if m.form.field == 3 {
		qtyLabel = highlightBg.Render(qtyLabel)
	}
	s += qtyLabel + "\n"

	s += "\n" + helpStyle.Render("tab/up/down sposta  enter crea  esc annulla") + "\n"
	return s
}

func (m Model) renderDashboard() string {
	s := titleStyle.Render("Restaurant TUI") + "\n\n"
	if m.loading {
		s += "Caricamento...\n"
		return s
	}
	s += "Dashboard\n\n"
	s += fmt.Sprintf("Posti: %d\n", m.config.Seats)
	s += fmt.Sprintf("Prenotazioni: %d\n", len(m.reservations))
	s += fmt.Sprintf("Ingredienti in magazzino: %d\n", len(m.warehouse.Ingredients))
	s += fmt.Sprintf("Bevande in magazzino: %d\n", len(m.warehouse.Drinks))
	s += "\n" + helpStyle.Render("[r] aggiorna  [d] reset demo  [tab] cambia vista  [q] esci") + "\n"
	if m.err != nil {
		s += "\n" + errorStyle.Render(m.err.Error()) + "\n"
	} else if m.status != "" {
		s += "\n" + statusStyle.Render(m.status) + "\n"
	}
	return s
}

func (m Model) renderWarehouse() string {
	s := titleStyle.Render("Restaurant TUI") + "\n\nMagazzino\n\n"
	if m.loading {
		s += "Caricamento...\n"
		return s
	}
	if len(m.warehouse.Ingredients) == 0 && len(m.warehouse.Drinks) == 0 && len(m.warehouse.ExtraGoods) == 0 {
		s += "Magazzino vuoto\n"
	} else {
		s += "Ingredienti:\n"
		for _, item := range m.warehouse.Ingredients {
			s += fmt.Sprintf("  %s: %d %s\n", item.Name, item.Quantity, item.Unit)
		}
		s += "\nBevande:\n"
		for _, item := range m.warehouse.Drinks {
			s += fmt.Sprintf("  %s: %d %s\n", item.Name, item.Quantity, item.Unit)
		}
		s += "\nExtra:\n"
		for _, item := range m.warehouse.ExtraGoods {
			s += fmt.Sprintf("  %s: %d %s\n", item.Name, item.Quantity, item.Unit)
		}
	}
	s += "\n" + helpStyle.Render("[r] aggiorna  [tab] cambia vista  [q] esci") + "\n"
	if m.err != nil {
		s += "\n" + errorStyle.Render(m.err.Error()) + "\n"
	} else if m.status != "" {
		s += "\n" + statusStyle.Render(m.status) + "\n"
	}
	return s
}
