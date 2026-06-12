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
