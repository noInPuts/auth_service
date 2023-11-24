package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.service.JwtService;
import cphbusiness.noInPuts.authService.service.RestaurantEmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(maxAge = 3600)
public class RestaurantEmployeeController {

    private final RestaurantEmployeeService restaurantEmployeeService;

    private final JwtService jwtService;

    @Autowired
    public RestaurantEmployeeController(RestaurantEmployeeService restaurantEmployeeService, JwtService jwtService) {
        this.restaurantEmployeeService = restaurantEmployeeService;
        this.jwtService = jwtService;
    }

    // Endpoint for logging in to a restaurant employee account
    @PostMapping(value = "/restaurantEmployee/login", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RestaurantEmployeeDTO> login(@Valid @RequestBody RestaurantEmployeeDTO postRestaurantEmployeeDTO, HttpServletResponse servletResponse) {

        // Gets restaurant employee account, if it doesn't exist or the password is wrong, return 401
        RestaurantEmployeeDTO restaurantEmployeeDTO;
        try {
            restaurantEmployeeDTO = restaurantEmployeeService.login(postRestaurantEmployeeDTO.getUsername(), postRestaurantEmployeeDTO.getPassword());
        } catch(WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Creates a JWT token and adds it to a cookie
        String jwtToken = jwtService.tokenGenerator(restaurantEmployeeDTO.getId(), restaurantEmployeeDTO.getUsername(), "employee");
        Cookie cookie = new Cookie("jwt-token", jwtToken);
        cookie.setMaxAge(2 * 24 * 60 * 60);
        // TODO: Enable when HTTPS is enabled
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        servletResponse.addCookie(cookie);

        return new ResponseEntity<>(restaurantEmployeeDTO, HttpStatus.OK);
    }
}
