package cphbusiness.noInPuts.authService.dto;

import jakarta.validation.constraints.NotNull;

public class RestaurantEmployeeDTO {

    private Long id;

    @NotNull
    private String username;
    @NotNull
    private String password;
    private RestaurantDTO restaurant;

    public RestaurantEmployeeDTO(Long id, String username, String password, RestaurantDTO restaurant) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.restaurant = restaurant;
    }

    public RestaurantEmployeeDTO() {
    }

    public RestaurantEmployeeDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }
}
