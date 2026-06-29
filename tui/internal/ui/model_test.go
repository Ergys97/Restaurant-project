package ui

import (
	"testing"

	"restaurant-tui/internal/api"
	tea "charm.land/bubbletea/v2"
)

func TestFormValidationRejectsBadNumbers(t *testing.T) {
	m := NewModel(nil)
	m.creating = true
	m.dishes = []api.DishSummary{{Name: "Pizza Margherita", Available: true}}
	m.form.date = "2026-07-01"
	m.form.covers = "abc"
	m.form.quantity = "1"

	updated, _ := m.Update(tea.KeyPressMsg{Code: tea.KeyEnter})
	model := updated.(Model)

	if model.status != "Coperti non validi" {
		t.Fatalf("expected validation status, got %q", model.status)
	}
}

func TestTabNavigation(t *testing.T) {
	m := NewModel(nil)
	updated, _ := m.Update(tea.KeyPressMsg{Code: tea.KeyRight})
	next := updated.(Model)
	if next.screen != reservationsScreen {
		t.Fatalf("expected reservations screen, got %v", next.screen)
	}
}
