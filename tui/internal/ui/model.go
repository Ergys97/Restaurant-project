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
	dishes            []api.DishSummary
	loading           bool
	dishesLoading     bool
	status            string
	reservationCursor int
	confirmDelete     bool
	form              reservationForm
	creating          bool
}

func NewModel(client *api.Client) Model {
	return Model{client: client, screen: dashboardScreen, form: newReservationForm()}
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

func (m Model) createReservation() tea.Cmd {
	dishName := ""
	if m.form.dishCursor >= 0 && m.form.dishCursor < len(m.dishes) {
		dishName = m.dishes[m.form.dishCursor].Name
	}
	req := api.ReservationRequest{
		Date:   m.form.date,
		Covers: atoiOrZero(m.form.covers),
		DishOrders: []api.DishOrderRequest{{
			DishName: dishName,
			Quantity: atoiOrZero(m.form.quantity),
		}},
	}
	return func() tea.Msg {
		reservation, err := m.client.CreateReservation(req)
		if err != nil {
			return apiErrorMsg{err: err}
		}
		return reservationCreatedMsg{reservation: reservation}
	}
}

func atoiOrZero(value string) int {
	n, err := strconv.Atoi(value)
	if err != nil {
		return 0
	}
	return n
}

func (m Model) loadDishes() tea.Cmd {
	return func() tea.Msg {
		dishes, err := m.client.GetDishes()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		return dishesFormLoadedMsg{dishes: dishes}
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

func (m Model) handleFormKey(msg tea.KeyPressMsg) (tea.Model, tea.Cmd) {
	switch msg.String() {
	case "esc":
		m.creating = false
	case "enter":
		if len(m.dishes) > 0 {
			return m, m.createReservation()
		}
	case "tab":
		m.form.field = (m.form.field + 1) % 4
	case "shift+tab":
		m.form.field = (m.form.field + 3) % 4
	case "up", "k":
		if m.form.field == 2 && m.form.dishCursor > 0 {
			m.form.dishCursor--
		}
	case "down", "j":
		if m.form.field == 2 && m.form.dishCursor < len(m.dishes)-1 {
			m.form.dishCursor++
		}
	case "backspace":
		if m.form.field == 2 {
			break
		}
		switch m.form.field {
		case 0:
			if len(m.form.date) > 0 {
				m.form.date = m.form.date[:len(m.form.date)-1]
			}
		case 1:
			if len(m.form.covers) > 0 {
				m.form.covers = m.form.covers[:len(m.form.covers)-1]
			}
		case 3:
			if len(m.form.quantity) > 0 {
				m.form.quantity = m.form.quantity[:len(m.form.quantity)-1]
			}
		}
	default:
		if len(msg.String()) == 1 {
			switch m.form.field {
			case 0:
				m.form.date += msg.String()
			case 1:
				m.form.covers += msg.String()
			case 3:
				m.form.quantity += msg.String()
			}
		}
	}
	return m, nil
}

func (m Model) Update(msg tea.Msg) (tea.Model, tea.Cmd) {
	switch msg := msg.(type) {
	case tea.KeyPressMsg:
		if m.creating {
			return m.handleFormKey(msg)
		}
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
		case "n":
			if m.screen == reservationsScreen && !m.confirmDelete {
				m.dishesLoading = true
				m.form = newReservationForm()
				return m, m.loadDishes()
			}
		case "esc":
			m.confirmDelete = false
		}
	case dishesFormLoadedMsg:
		m.dishesLoading = false
		m.dishes = msg.dishes
		m.creating = true
		m.form = newReservationForm()
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
	case reservationCreatedMsg:
		m.creating = false
		m.dishes = nil
		m.status = "Prenotazione creata"
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
