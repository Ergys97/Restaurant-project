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
		Date:       "2026-07-01",
		Covers:     2,
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

func TestGetDishes(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet || r.URL.Path != "/api/dishes" {
			t.Fatalf("unexpected request: %s %s", r.Method, r.URL.Path)
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`[{"name":"Pizza Margherita","available":true},{"name":"Insalata Caprese","available":true}]`))
	}))
	defer server.Close()

	client := NewClient(server.URL)
	dishes, err := client.GetDishes()
	if err != nil {
		t.Fatalf("GetDishes returned error: %v", err)
	}
	if len(dishes) != 2 {
		t.Fatalf("expected 2 dishes, got %d", len(dishes))
	}
	if dishes[0].Name != "Pizza Margherita" {
		t.Fatalf("expected Pizza Margherita, got %q", dishes[0].Name)
	}
	if !dishes[1].Available {
		t.Fatalf("expected dish to be available")
	}
}

func TestGetMenus(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet || r.URL.Path != "/api/menus" {
			t.Fatalf("unexpected request: %s %s", r.Method, r.URL.Path)
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`[{"name":"Menu Italiano","dishCount":2,"available":true}]`))
	}))
	defer server.Close()

	client := NewClient(server.URL)
	menus, err := client.GetMenus()
	if err != nil {
		t.Fatalf("GetMenus returned error: %v", err)
	}
	if len(menus) != 1 {
		t.Fatalf("expected 1 menu, got %d", len(menus))
	}
	if menus[0].Name != "Menu Italiano" {
		t.Fatalf("expected Menu Italiano, got %q", menus[0].Name)
	}
	if menus[0].DishCount != 2 {
		t.Fatalf("expected dishCount 2, got %d", menus[0].DishCount)
	}
}
