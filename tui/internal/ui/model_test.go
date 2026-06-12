package ui

import (
	"testing"
	tea "charm.land/bubbletea/v2"
)

func TestTabNavigation(t *testing.T) {
	m := NewModel(nil)
	updated, _ := m.Update(tea.KeyPressMsg{Code: tea.KeyRight})
	next := updated.(Model)
	if next.screen != reservationsScreen {
		t.Fatalf("expected reservations screen, got %v", next.screen)
	}
}
