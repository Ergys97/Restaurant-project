package ui

import (
	"strconv"

	"restaurant-tui/internal/api"
	tea "charm.land/bubbletea/v2"
)

type screen int

const (
	dashboardScreen screen = iota
	reservationsScreen
	warehouseScreen
)

type Model struct {
	client            *api.Client
	screen            screen
	err               error
	config            api.RestaurantConfig
	reservations      []api.Reservation
	warehouse         api.ShoppingList
	loading           bool
	status            string
	reservationCursor int
	confirmDelete     bool
}

func NewModel(client *api.Client) Model {
	return Model{client: client, screen: dashboardScreen}
}

func (m Model) Init() tea.Cmd {
	if m.client == nil {
		return nil
	}
	m.loading = true
	return m.loadDashboard()
}

func (m Model) deleteSelectedReservation() tea.Cmd {
	id := m.reservations[m.reservationCursor].ID
	return func() tea.Msg {
		if err := m.client.DeleteReservation(id); err != nil {
			return apiErrorMsg{err: err}
		}
		return reservationDeletedMsg{id: id}
	}
}

func (m Model) resetDemo() tea.Cmd {
	return func() tea.Msg {
		summary, err := m.client.ResetDemo()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		return demoResetMsg{summary: summary}
	}
}

func (m Model) loadDashboard() tea.Cmd {
	return func() tea.Msg {
		config, err := m.client.GetConfig()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		reservations, err := m.client.GetReservations()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		warehouse, err := m.client.GetWarehouse()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		return dashboardLoadedMsg{config: config, reservations: reservations, warehouse: warehouse}
	}
}

func (m Model) Update(msg tea.Msg) (tea.Model, tea.Cmd) {
	switch msg := msg.(type) {
	case tea.KeyPressMsg:
		switch msg.String() {
		case "q", "ctrl+c":
			return m, tea.Quit
		case "right", "tab":
			m.screen = (m.screen + 1) % 3
		case "left", "shift+tab":
			m.screen = (m.screen + 2) % 3
		case "r":
			m.loading = true
			return m, m.loadDashboard()
		case "d":
			m.loading = true
			return m, m.resetDemo()
		case "up", "k":
			if m.screen == reservationsScreen && m.reservationCursor > 0 && !m.confirmDelete {
				m.reservationCursor--
			}
		case "down", "j":
			if m.screen == reservationsScreen && m.reservationCursor < len(m.reservations)-1 && !m.confirmDelete {
				m.reservationCursor++
			}
		case "x":
			if m.screen == reservationsScreen && len(m.reservations) > 0 && !m.confirmDelete {
				m.confirmDelete = true
			}
		case "y":
			if m.confirmDelete && m.screen == reservationsScreen && len(m.reservations) > 0 {
				m.confirmDelete = false
				return m, m.deleteSelectedReservation()
			}
		case "esc":
			m.confirmDelete = false
		}
	case dashboardLoadedMsg:
		m.loading = false
		m.config = msg.config
		m.reservations = msg.reservations
		m.warehouse = msg.warehouse
		m.status = "Dati caricati"
	case reservationDeletedMsg:
		m.status = "Prenotazione cancellata"
		m.loading = true
		return m, m.loadDashboard()
	case demoResetMsg:
		m.status = "Demo reset: prenotazioni " + strconv.Itoa(msg.summary.Reservations)
		m.loading = true
		return m, m.loadDashboard()
	case apiErrorMsg:
		m.loading = false
		m.err = msg.err
		m.status = msg.err.Error()
	}
	return m, nil
}

func (m Model) View() tea.View {
	return tea.NewView(m.render())
}
