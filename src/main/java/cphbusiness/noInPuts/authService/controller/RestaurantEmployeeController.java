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

    @PostMapping(value = "/restaurantEmployee/login", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody RestaurantEmployeeDTO postRestaurantEmployeeDTO, HttpServletResponse servletResponse) {
        RestaurantEmployeeDTO restaurantEmployeeDTO;

        try {
            restaurantEmployeeDTO = restaurantEmployeeService.login(postRestaurantEmployeeDTO.getUsername(), postRestaurantEmployeeDTO.getPassword());
        } catch(WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String jwtToken = jwtService.tokenGenerator(restaurantEmployeeDTO.getId(), restaurantEmployeeDTO.getUsername(), "employee");

        Map<String, Object> response = new HashMap<>();
        response.put("user", restaurantEmployeeDTO);

        Cookie cookie = new Cookie("jwt-token", jwtToken);
        cookie.setMaxAge(2 * 24 * 60 * 60);
        cookie.setHttpOnly(true);

        // TODO: research setSecure

        servletResponse.addCookie(cookie);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
