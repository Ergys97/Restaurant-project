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
		http:    &http.Client{Timeout: 5 * time.Second},
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

func (c *Client) GetDishes() ([]DishSummary, error) {
	var out []DishSummary
	err := c.get("/api/dishes", &out)
	return out, err
}

func (c *Client) GetMenus() ([]MenuSummary, error) {
	var out []MenuSummary
	err := c.get("/api/menus", &out)
	return out, err
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
