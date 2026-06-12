package main

import (
	"fmt"
	"os"

	"restaurant-tui/internal/api"
	"restaurant-tui/internal/ui"
	tea "charm.land/bubbletea/v2"
)

func main() {
	baseURL := os.Getenv("RESTAURANT_API_URL")
	if baseURL == "" {
		baseURL = "http://localhost:8080"
	}

	p := tea.NewProgram(ui.NewModel(api.NewClient(baseURL)))
	if _, err := p.Run(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}
