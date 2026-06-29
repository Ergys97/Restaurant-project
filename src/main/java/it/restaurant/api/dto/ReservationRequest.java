package it.restaurant.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class ReservationRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    @Min(1)
    private Integer covers;

    @Valid
    private List<DishOrderRequest> dishOrders;

    @Valid
    private List<MenuOrderRequest> menuOrders;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getCovers() { return covers; }
    public void setCovers(Integer covers) { this.covers = covers; }

    public List<DishOrderRequest> getDishOrders() { return dishOrders; }
    public void setDishOrders(List<DishOrderRequest> dishOrders) { this.dishOrders = dishOrders; }

    public List<MenuOrderRequest> getMenuOrders() { return menuOrders; }
    public void setMenuOrders(List<MenuOrderRequest> menuOrders) { this.menuOrders = menuOrders; }
}
