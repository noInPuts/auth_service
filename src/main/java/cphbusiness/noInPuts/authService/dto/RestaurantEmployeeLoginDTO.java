package cphbusiness.noInPuts.authService.dto;

import jakarta.servlet.http.Cookie;

public class RestaurantEmployeeLoginDTO {
    private RestaurantEmployeeDTO restaurantEmployee;
    private Cookie jwtCookie;

    public RestaurantEmployeeLoginDTO(Cookie jwtCookie, RestaurantEmployeeDTO restaurantEmployee) {
        this.restaurantEmployee = restaurantEmployee;
        this.jwtCookie = jwtCookie;
    }

    public RestaurantEmployeeDTO getRestaurantEmployee() {
        return restaurantEmployee;
    }

    public void setRestaurantEmployee(RestaurantEmployeeDTO restaurantEmployee) {
        this.restaurantEmployee = restaurantEmployee;
    }

    public Cookie getJwtCookie() {
        return jwtCookie;
    }

    public void setJwtCookie(Cookie jwtCookie) {
        this.jwtCookie = jwtCookie;
    }
}
