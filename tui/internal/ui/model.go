package ui

import (
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
	client       *api.Client
	screen       screen
	err          error
	config       api.RestaurantConfig
	reservations []api.Reservation
	warehouse    api.ShoppingList
	loading      bool
	status       string
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
		}
	case dashboardLoadedMsg:
		m.loading = false
		m.config = msg.config
		m.reservations = msg.reservations
		m.warehouse = msg.warehouse
		m.status = "Dati caricati"
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
