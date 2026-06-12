# Bubble Tea TUI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Creare una TUI Go/Bubble Tea operativa che usa la Spring Boot REST API del progetto per visualizzare, creare e cancellare prenotazioni, vedere magazzino/configurazione e resettare i dati demo.

**Architecture:** La TUI vive in un modulo Go separato sotto `tui/` e non accede mai direttamente ai file JSON. Tutte le operazioni passano dal backend Spring Boot su `http://localhost:8080`, così la business logic resta nel service layer Java. Lo sviluppo continua direttamente sul branch `dev`, con commit frequenti ma senza cercare stabilità definitiva prima di iterare.

**Tech Stack:** Go, Bubble Tea v2 (`charm.land/bubbletea/v2`), Bubbles v2 (`github.com/charmbracelet/bubbles/v2`), Lip Gloss v2 (`charm.land/lipgloss/v2`), Spring Boot REST API esistente, `net/http`, `encoding/json`, `httptest`.

---

## Contesto Corrente

- Branch atteso: `dev`.
- Backend gia' disponibile come JAR Spring Boot:

```powershell
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

- Endpoint gia' presenti:
  - `POST /api/admin/demo/reset`
  - `GET /api/config`
  - `PUT /api/config`
  - `GET /api/reservations`
  - `POST /api/reservations`
  - `DELETE /api/reservations/{id}`
  - `GET /api/warehouse`

## File Structure

- Create: `tui/go.mod` - modulo Go separato per la TUI.
- Create: `tui/cmd/restaurant-tui/main.go` - entrypoint CLI.
- Create: `tui/internal/api/client.go` - client HTTP verso Spring Boot.
- Create: `tui/internal/api/types.go` - DTO Go allineati agli endpoint Java.
- Create: `tui/internal/api/client_test.go` - test con `httptest`.
- Create: `tui/internal/ui/model.go` - modello Bubble Tea principale.
- Create: `tui/internal/ui/messages.go` - messaggi asincroni per risposte API.
- Create: `tui/internal/ui/views.go` - rendering delle schermate.
- Create: `tui/internal/ui/styles.go` - stili Lip Gloss.
- Create: `tui/internal/ui/forms.go` - form creazione prenotazione.
- Create: `tui/internal/ui/model_test.go` - test di navigazione e stati UI.
- Modify: `README.md` - istruzioni backend + TUI.

## Regole di Implementazione

- Non duplicare logica di business in Go.
- La TUI deve gestire errori HTTP mostrando messaggi leggibili.
- Il backend deve essere avviato separatamente.
- Il client Go deve avere `baseURL` configurabile:

```powershell
$env:RESTAURANT_API_URL="http://localhost:8080"
go run .\tui\cmd\restaurant-tui
```

- Default se env mancante: `http://localhost:8080`.
- Primo MVP: niente autenticazione, niente modifica prenotazione, niente editor completo ricette/menu.

---

## Task 0: Verifica Toolchain e Backend

**Files:** nessuno.

- [ ] **Step 0.1: Verifica branch**

Run:

```powershell
git branch --show-current
git status --short
```

Expected:

```text
dev
```

Working tree pulito o con soli cambi intenzionali.

- [ ] **Step 0.2: Verifica Go**

Run:

```powershell
go version
```

Expected: Go installato. Se manca, installare Go da `https://go.dev/dl/`, riaprire PowerShell e ripetere.

- [ ] **Step 0.3: Verifica backend**

Run:

```powershell
mvn test
mvn package
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

In un secondo terminale:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/admin/demo/reset
Invoke-RestMethod http://localhost:8080/api/reservations
```

Expected: reset riuscito e almeno una prenotazione demo.

---

## Task 1: Modulo Go e Client REST

**Files:**
- Create: `tui/go.mod`
- Create: `tui/internal/api/types.go`
- Create: `tui/internal/api/client.go`
- Create: `tui/internal/api/client_test.go`

- [ ] **Step 1.1: Inizializza modulo Go**

Run:

```powershell
New-Item -ItemType Directory -Force tui
Set-Location tui
go mod init restaurant-tui
go get charm.land/bubbletea/v2 charm.land/lipgloss/v2 github.com/charmbracelet/bubbles/v2
Set-Location ..
```

Expected: `tui/go.mod` e `tui/go.sum` creati.

- [ ] **Step 1.2: Scrivi test RED per `Client.GetReservations`**

Create `tui/internal/api/client_test.go`:

```go
package api

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestGetReservations(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet || r.URL.Path != "/api/reservations" {
			t.Fatalf("unexpected request: %s %s", r.Method, r.URL.Path)
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`[{"id":"r-1","date":"2026-07-01","covers":4,"dishOrders":[],"menuOrders":[]}]`))
	}))
	defer server.Close()

	client := NewClient(server.URL)
	reservations, err := client.GetReservations()
	if err != nil {
		t.Fatalf("GetReservations returned error: %v", err)
	}
	if len(reservations) != 1 {
		t.Fatalf("expected 1 reservation, got %d", len(reservations))
	}
	if reservations[0].ID != "r-1" {
		t.Fatalf("expected id r-1, got %q", reservations[0].ID)
	}
}
```

Run:

```powershell
go test .\tui\internal\api
```

Expected: FAIL because package/types/client do not exist.

- [ ] **Step 1.3: Implementa DTO Go**

Create `tui/internal/api/types.go`:

```go
package api

type RestaurantConfig struct {
	Seats                   int               `json:"seats"`
	WorkloadPerPerson       float64           `json:"workloadPerPerson"`
	Initialized             bool              `json:"initialized"`
	Ingredients             []FoodItem        `json:"ingredients"`
	Drinks                  []FoodItem        `json:"drinks"`
	ExtraGoods              []FoodItem        `json:"extraGoods"`
	PerCapitaDrinks         map[string]int    `json:"perCapitaDrinks"`
	PerCapitaExtraGoods     map[string]int    `json:"perCapitaExtraGoods"`
}

type FoodItem struct {
	Name       string `json:"name"`
	Quantity   int    `json:"quantity"`
	ExpiryDate string `json:"expiryDate"`
	Unit       string `json:"unit"`
}

type ShoppingList struct {
	Ingredients []FoodItem `json:"ingredients"`
	Drinks      []FoodItem `json:"drinks"`
	ExtraGoods  []FoodItem `json:"extraGoods"`
}

type Reservation struct {
	ID         string      `json:"id"`
	Date       string      `json:"date"`
	Covers     int         `json:"covers"`
	DishOrders []DishOrder `json:"dishOrders"`
	MenuOrders []MenuOrder `json:"menuOrders"`
}

type DishOrder struct {
	Dish     NamedEntity `json:"dish"`
	Quantity int        `json:"quantity"`
}

type MenuOrder struct {
	Menu     NamedEntity `json:"menu"`
	Quantity int        `json:"quantity"`
}

type NamedEntity struct {
	Name string `json:"name"`
}

type ReservationRequest struct {
	Date       string             `json:"date"`
	Covers     int                `json:"covers"`
	DishOrders []DishOrderRequest `json:"dishOrders,omitempty"`
	MenuOrders []MenuOrderRequest `json:"menuOrders,omitempty"`
}

type DishOrderRequest struct {
	DishName string `json:"dishName"`
	Quantity int    `json:"quantity"`
}

type MenuOrderRequest struct {
	MenuName string `json:"menuName"`
	Quantity int    `json:"quantity"`
}

type DemoResetSummary struct {
	Reservations int `json:"reservations"`
	Ingredients  int `json:"ingredients"`
	Dishes       int `json:"dishes"`
	Menus        int `json:"menus"`
}
```

- [ ] **Step 1.4: Implementa client HTTP minimo**

Create `tui/internal/api/client.go`:

```go
package api

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"time"
)

type Client struct {
	baseURL string
	http    *http.Client
}

func NewClient(baseURL string) *Client {
	return &Client{
		baseURL: strings.TrimRight(baseURL, "/"),
		http: &http.Client{Timeout: 5 * time.Second},
	}
}

func (c *Client) GetConfig() (RestaurantConfig, error) {
	var out RestaurantConfig
	err := c.get("/api/config", &out)
	return out, err
}

func (c *Client) GetWarehouse() (ShoppingList, error) {
	var out ShoppingList
	err := c.get("/api/warehouse", &out)
	return out, err
}

func (c *Client) GetReservations() ([]Reservation, error) {
	var out []Reservation
	err := c.get("/api/reservations", &out)
	return out, err
}

func (c *Client) CreateReservation(req ReservationRequest) (Reservation, error) {
	var out Reservation
	err := c.send(http.MethodPost, "/api/reservations", req, &out)
	return out, err
}

func (c *Client) DeleteReservation(id string) error {
	return c.send(http.MethodDelete, "/api/reservations/"+id, nil, nil)
}

func (c *Client) ResetDemo() (DemoResetSummary, error) {
	var out DemoResetSummary
	err := c.send(http.MethodPost, "/api/admin/demo/reset", nil, &out)
	return out, err
}

func (c *Client) get(path string, out any) error {
	return c.send(http.MethodGet, path, nil, out)
}

func (c *Client) send(method, path string, body any, out any) error {
	var reader *bytes.Reader
	if body == nil {
		reader = bytes.NewReader(nil)
	} else {
		payload, err := json.Marshal(body)
		if err != nil {
			return err
		}
		reader = bytes.NewReader(payload)
	}

	request, err := http.NewRequest(method, c.baseURL+path, reader)
	if err != nil {
		return err
	}
	if body != nil {
		request.Header.Set("Content-Type", "application/json")
	}

	response, err := c.http.Do(request)
	if err != nil {
		return err
	}
	defer response.Body.Close()

	if response.StatusCode < 200 || response.StatusCode >= 300 {
		return fmt.Errorf("%s %s returned %d", method, path, response.StatusCode)
	}
	if out == nil {
		return nil
	}
	return json.NewDecoder(response.Body).Decode(out)
}
```

- [ ] **Step 1.5: Verifica GREEN client**

Run:

```powershell
go test .\tui\internal\api
```

Expected: PASS.

- [ ] **Step 1.6: Aggiungi test client per create/delete/reset**

Extend `client_test.go` with:

```go
func TestCreateReservation(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost || r.URL.Path != "/api/reservations" {
			t.Fatalf("unexpected request: %s %s", r.Method, r.URL.Path)
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id":"created","date":"2026-07-01","covers":2,"dishOrders":[],"menuOrders":[]}`))
	}))
	defer server.Close()

	client := NewClient(server.URL)
	created, err := client.CreateReservation(ReservationRequest{
		Date: "2026-07-01",
		Covers: 2,
		DishOrders: []DishOrderRequest{{DishName: "Pizza Margherita", Quantity: 2}},
	})
	if err != nil {
		t.Fatalf("CreateReservation returned error: %v", err)
	}
	if created.ID != "created" {
		t.Fatalf("expected created id, got %q", created.ID)
	}
}

func TestDeleteReservation(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodDelete || r.URL.Path != "/api/reservations/r-1" {
			t.Fatalf("unexpected request: %s %s", r.Method, r.URL.Path)
		}
		w.WriteHeader(http.StatusNoContent)
	}))
	defer server.Close()

	client := NewClient(server.URL)
	if err := client.DeleteReservation("r-1"); err != nil {
		t.Fatalf("DeleteReservation returned error: %v", err)
	}
}
```

Run:

```powershell
go test .\tui\internal\api
```

Expected: PASS.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: add Go REST client for restaurant API"
```

---

## Task 2: Shell Bubble Tea e Navigazione

**Files:**
- Create: `tui/cmd/restaurant-tui/main.go`
- Create: `tui/internal/ui/model.go`
- Create: `tui/internal/ui/messages.go`
- Create: `tui/internal/ui/views.go`
- Create: `tui/internal/ui/styles.go`
- Create: `tui/internal/ui/model_test.go`

- [ ] **Step 2.1: Scrivi test RED per navigazione tab**

Create `tui/internal/ui/model_test.go`:

```go
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
```

Run:

```powershell
go test .\tui\internal\ui
```

Expected: FAIL because `NewModel` and `Model` do not exist.

- [ ] **Step 2.2: Implementa modello UI minimo**

Create `tui/internal/ui/model.go`:

```go
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
```

Create `tui/internal/ui/views.go`:

```go
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
```

Create `tui/internal/ui/styles.go`:

```go
package ui

import "charm.land/lipgloss/v2"

var titleStyle = lipgloss.NewStyle().Bold(true)
```

- [ ] **Step 2.3: Crea entrypoint**

Create `tui/cmd/restaurant-tui/main.go`:

```go
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
```

- [ ] **Step 2.4: Verifica GREEN UI shell**

Run:

```powershell
go test .\tui\internal\ui
go test .\tui\...
go run .\tui\cmd\restaurant-tui
```

Expected: test PASS; TUI parte; `q` chiude.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: add Bubble Tea shell navigation"
```

---

## Task 3: Caricamento Dati e Dashboard

**Files:**
- Modify: `tui/internal/ui/model.go`
- Modify: `tui/internal/ui/messages.go`
- Modify: `tui/internal/ui/views.go`
- Modify: `tui/internal/ui/model_test.go`

- [ ] **Step 3.1: Aggiungi messaggi asincroni**

Create `tui/internal/ui/messages.go`:

```go
package ui

import "restaurant-tui/internal/api"

type dashboardLoadedMsg struct {
	config       api.RestaurantConfig
	reservations []api.Reservation
	warehouse    api.ShoppingList
}

type apiErrorMsg struct {
	err error
}
```

- [ ] **Step 3.2: Aggiungi comando `loadDashboard`**

In `model.go`, add fields:

```go
config       api.RestaurantConfig
reservations []api.Reservation
warehouse    api.ShoppingList
loading      bool
status       string
```

Add function:

```go
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
```

Change `Init`:

```go
func (m Model) Init() tea.Cmd {
	if m.client == nil {
		return nil
	}
	m.loading = true
	return m.loadDashboard()
}
```

Handle messages in `Update`:

```go
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
```

- [ ] **Step 3.3: Render dashboard**

In `views.go`, dashboard should include:

```text
Restaurant TUI

Posti: 25
Prenotazioni: 1
Ingredienti in magazzino: 5

[r] aggiorna  [d] reset demo  [tab] cambia vista  [q] esci
```

Add key handling:

```go
case "r":
	m.loading = true
	return m, m.loadDashboard()
```

- [ ] **Step 3.4: Verifica con backend reale**

Terminale 1:

```powershell
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

Terminale 2:

```powershell
go run .\tui\cmd\restaurant-tui
```

Expected: dashboard mostra config, prenotazioni e magazzino demo.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: load dashboard data from restaurant API"
```

---

## Task 4: Vista Prenotazioni e Cancellazione

**Files:**
- Modify: `tui/internal/ui/model.go`
- Modify: `tui/internal/ui/views.go`
- Modify: `tui/internal/ui/messages.go`
- Modify: `tui/internal/ui/model_test.go`

- [ ] **Step 4.1: Aggiungi selezione prenotazione**

Add fields:

```go
reservationCursor int
confirmDelete     bool
```

In `Update`, on reservations screen:

```go
case "up", "k":
	if m.screen == reservationsScreen && m.reservationCursor > 0 {
		m.reservationCursor--
	}
case "down", "j":
	if m.screen == reservationsScreen && m.reservationCursor < len(m.reservations)-1 {
		m.reservationCursor++
	}
case "x":
	if m.screen == reservationsScreen && len(m.reservations) > 0 {
		m.confirmDelete = true
	}
case "esc":
	m.confirmDelete = false
```

- [ ] **Step 4.2: Aggiungi comando cancellazione**

In `messages.go`:

```go
type reservationDeletedMsg struct {
	id string
}
```

In `model.go`:

```go
func (m Model) deleteSelectedReservation() tea.Cmd {
	id := m.reservations[m.reservationCursor].ID
	return func() tea.Msg {
		if err := m.client.DeleteReservation(id); err != nil {
			return apiErrorMsg{err: err}
		}
		return reservationDeletedMsg{id: id}
	}
}
```

Handle confirm:

```go
case "y":
	if m.confirmDelete && m.screen == reservationsScreen && len(m.reservations) > 0 {
		m.confirmDelete = false
		return m, m.deleteSelectedReservation()
	}
```

Handle success:

```go
case reservationDeletedMsg:
	m.status = "Prenotazione cancellata"
	m.loading = true
	return m, m.loadDashboard()
```

- [ ] **Step 4.3: Render prenotazioni**

Render each reservation:

```text
> 2026-07-01 - 4 coperti - id r-1
  2026-07-03 - 2 coperti - id r-2
```

If confirmation active:

```text
Confermi cancellazione? y/n
```

Keys:

```text
up/down seleziona  x cancella  y conferma  esc annulla  r aggiorna
```

- [ ] **Step 4.4: Manual test**

Run TUI with backend started. Steps:
1. reset demo with `d` if available from Task 5, otherwise API command;
2. go to prenotazioni;
3. select reservation;
4. press `x`, then `y`;
5. verify list refreshes and reservation disappears.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: support reservation deletion in TUI"
```

---

## Task 5: Reset Demo dalla TUI

**Files:**
- Modify: `tui/internal/ui/model.go`
- Modify: `tui/internal/ui/messages.go`
- Modify: `tui/internal/ui/views.go`

- [ ] **Step 5.1: Aggiungi messaggio reset**

In `messages.go`:

```go
type demoResetMsg struct {
	summary api.DemoResetSummary
}
```

- [ ] **Step 5.2: Aggiungi comando reset**

In `model.go`:

```go
func (m Model) resetDemo() tea.Cmd {
	return func() tea.Msg {
		summary, err := m.client.ResetDemo()
		if err != nil {
			return apiErrorMsg{err: err}
		}
		return demoResetMsg{summary: summary}
	}
}
```

Key handling:

```go
case "d":
	m.loading = true
	return m, m.resetDemo()
```

Handle message:

```go
case demoResetMsg:
	m.status = "Demo reset: prenotazioni " + strconv.Itoa(msg.summary.Reservations)
	m.loading = true
	return m, m.loadDashboard()
```

Add import:

```go
import "strconv"
```

- [ ] **Step 5.3: Manual test**

Backend:

```powershell
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

TUI:

```powershell
go run .\tui\cmd\restaurant-tui
```

Expected:
- pressing `d` resets demo data;
- dashboard count returns to known demo values;
- if backend started without `--restaurant.demo.reset-enabled=true`, status shows an HTTP 403 error.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: add demo reset action to TUI"
```

---

## Task 6: Form Creazione Prenotazione

**Files:**
- Create: `tui/internal/ui/forms.go`
- Modify: `tui/internal/ui/model.go`
- Modify: `tui/internal/ui/views.go`
- Modify: `tui/internal/ui/messages.go`
- Modify: `tui/internal/ui/model_test.go`

- [ ] **Step 6.1: Definisci stato form semplice**

Create `forms.go`:

```go
package ui

type reservationForm struct {
	date     string
	covers   string
	dishName string
	quantity string
	field    int
}

func newReservationForm() reservationForm {
	return reservationForm{
		date:     "2026-07-01",
		covers:   "2",
		dishName: "Pizza Margherita",
		quantity: "2",
	}
}
```

Add field to `Model`:

```go
form reservationForm
creating bool
```

Initialize:

```go
form: newReservationForm(),
```

- [ ] **Step 6.2: Form MVP senza Bubbles textinput**

Per il primo MVP, non usare ancora `textinput`; implementare editing minimale:
- `n` apre form;
- `tab` cambia campo;
- `backspace` cancella ultimo carattere;
- caratteri stampabili appendono al campo corrente;
- `enter` invia;
- `esc` annulla.

Questo riduce rischio e mantiene chiaro il flusso. In una fase successiva si sostituisce con `bubbles/textinput`.

- [ ] **Step 6.3: Aggiungi comando create**

In `messages.go`:

```go
type reservationCreatedMsg struct {
	reservation api.Reservation
}
```

In `model.go`:

```go
func (m Model) createReservation() tea.Cmd {
	req := api.ReservationRequest{
		Date: m.form.date,
		Covers: atoiOrZero(m.form.covers),
		DishOrders: []api.DishOrderRequest{{
			DishName: m.form.dishName,
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
```

Handle success:

```go
case reservationCreatedMsg:
	m.creating = false
	m.status = "Prenotazione creata"
	m.loading = true
	return m, m.loadDashboard()
```

- [ ] **Step 6.4: Render form**

Render:

```text
Nuova prenotazione

Data: 2026-07-01
Coperti: 2
Piatto: Pizza Margherita
Quantita': 2

tab campo successivo  enter crea  esc annulla
```

Highlight the current field with Lip Gloss.

- [ ] **Step 6.5: Manual test**

1. start backend with reset enabled;
2. start TUI;
3. go to reservations;
4. press `n`;
5. create reservation using `Pizza Margherita`;
6. verify reservation count increases.

Checkpoint commit consigliato:

```powershell
git add tui
git commit -m "feat: create reservations from TUI"
```

---

## Task 7: Polish, README e Verifica Finale

**Files:**
- Modify: `README.md`
- Modify: `tui/internal/ui/views.go`
- Modify: `tui/internal/ui/styles.go`

- [ ] **Step 7.1: Migliora layout con Lip Gloss**

Use:
- title bold;
- status bar;
- selected row style;
- error style;
- compact help footer.

Keep palette restrained and readable on default terminal backgrounds.

- [ ] **Step 7.2: Aggiorna README**

Add section:

```markdown
## REST API + Bubble Tea TUI

Start the backend:

```powershell
mvn package
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
```

Start the TUI:

```powershell
go run .\tui\cmd\restaurant-tui
```

Optional custom backend URL:

```powershell
$env:RESTAURANT_API_URL="http://localhost:8080"
go run .\tui\cmd\restaurant-tui
```
```

- [ ] **Step 7.3: Verifica completa**

Run:

```powershell
mvn test
mvn package
go test .\tui\...
```

Manual smoke:

```powershell
java -jar target\restaurant.jar --restaurant.demo.reset-enabled=true
go run .\tui\cmd\restaurant-tui
```

Manual checklist:
- dashboard loads;
- reset demo works;
- reservations list loads;
- reservation delete works;
- reservation create works;
- warehouse view loads;
- `q` exits cleanly;
- backend offline shows readable error.

Checkpoint commit consigliato:

```powershell
git add README.md tui
git commit -m "docs: document Bubble Tea TUI workflow"
```

---

## Deferred After Stable MVP

- Replace manual form handling with `bubbles/textinput`.
- Add `bubbles/table` for reservations and warehouse.
- Add edit reservation flow.
- Add menu/dish discovery endpoints in Java so the TUI does not require known dish names.
- Add release packaging for Windows:

```powershell
go build -o target\restaurant-tui.exe .\tui\cmd\restaurant-tui
```

- Add CI job for Go tests.

## Self-Review

- The plan keeps business logic in Java and uses REST from Go.
- The plan is compatible with development directly on `dev`.
- The MVP is operational: reset, dashboard, list, create, delete, warehouse.
- The plan avoids editing JSON from Go.
- The current backend has no dish/menu listing endpoint, so MVP creation uses known demo dish name `Pizza Margherita`; a dedicated endpoint is deferred.
