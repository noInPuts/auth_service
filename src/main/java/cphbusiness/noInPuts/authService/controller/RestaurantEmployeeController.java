package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeLoginDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/restaurantEmployee")
@CrossOrigin(maxAge = 3600)
public class RestaurantEmployeeController {

    private final ServiceFacade serviceFacade;

    @Autowired
    public RestaurantEmployeeController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    // Endpoint for logging in to a restaurant employee account
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RestaurantEmployeeDTO> login(@Valid @RequestBody RestaurantEmployeeDTO postRestaurantEmployeeDTO, HttpServletResponse servletResponse) {

        // Gets restaurant employee account, if it doesn't exist or the password is wrong, return 401
        RestaurantEmployeeLoginDTO restaurantEmployeeLoginDTO;
        try {
            restaurantEmployeeLoginDTO = serviceFacade.restaurantEmployeeLogin(postRestaurantEmployeeDTO.getUsername(), postRestaurantEmployeeDTO.getPassword());
        } catch(WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        servletResponse.addCookie(restaurantEmployeeLoginDTO.getJwtCookie());
        return new ResponseEntity<>(restaurantEmployeeLoginDTO.getRestaurantEmployee(), HttpStatus.OK);
    }
}
