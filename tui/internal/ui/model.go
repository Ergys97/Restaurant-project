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
	client *api.Client
	screen screen
	err    error
}

func NewModel(client *api.Client) Model {
	return Model{client: client, screen: dashboardScreen}
}

func (m Model) Init() tea.Cmd {
	return nil
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
		}
	}
	return m, nil
}

func (m Model) View() tea.View {
	return tea.NewView(m.render())
}
