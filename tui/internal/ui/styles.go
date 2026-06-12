package ui

import "charm.land/lipgloss/v2"

var (
	titleStyle    = lipgloss.NewStyle().Bold(true)
	cursorStyle   = lipgloss.NewStyle().Foreground(lipgloss.Color("#00ff00")).Bold(true)
	errorStyle    = lipgloss.NewStyle().Foreground(lipgloss.Color("#ff0000"))
	statusStyle   = lipgloss.NewStyle().Foreground(lipgloss.Color("#888888"))
	highlightBg   = lipgloss.NewStyle().Background(lipgloss.Color("#444444"))
	helpStyle     = lipgloss.NewStyle().Foreground(lipgloss.Color("#666666"))
)
