package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.UserCreateDTO;
import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.dto.UserLoginDTO;
import cphbusiness.noInPuts.authService.dto.UserLogoutDTO;
import cphbusiness.noInPuts.authService.exception.*;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://167.71.45.53:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController("/api/auth/user")
public class UserController {

    private final ServiceFacade serviceFacade;

    @Autowired
    public UserController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    // Endpoint for creating a user account
    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse) {

        // Create user account, returns 409 if user already exists or 400 if password is weak
        UserCreateDTO userCreateDTO;
        try {
            // Creates user and returns userDTO
            userCreateDTO = serviceFacade.userCreateAccount(POSTuserDTO.getUsername(), POSTuserDTO.getPassword());
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (WeakPasswordException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Sets JWT token cookie
        servletResponse.addCookie(userCreateDTO.getJwtCookie());
        servletResponse.addCookie(userCreateDTO.getLoginCookie());

        return new ResponseEntity<>(userCreateDTO.getUser(), HttpStatus.CREATED);
    }

    // Endpoint for logging in to a user account
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> login(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse, HttpServletRequest servletRequest) {

        boolean blocked = serviceFacade.isClientBlocked(servletRequest);
        if(blocked) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }
        // Check for correct credentials
        UserLoginDTO userLoginDTO;
        try {
            userLoginDTO = serviceFacade.userLogin(POSTuserDTO.getUsername(), POSTuserDTO.getPassword());
        } catch (WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Sets JWT token cookie
        servletResponse.addCookie(userLoginDTO.getJwtCookie());
        servletResponse.addCookie(userLoginDTO.getLoginCookie());

        return new ResponseEntity<>(userLoginDTO.getUser(), HttpStatus.OK);
    }

    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> logout(@Valid @CookieValue("jwt-token") String jwtToken, HttpServletResponse servletResponse) {

        // Check if jwtToken is empty
        if(jwtToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserLogoutDTO userLogoutDTO;

        // Logging user out with jwtService
        try {
            userLogoutDTO = serviceFacade.userLogout(jwtToken);
        } catch (AlreadyLoggedOutException e) {
            return new ResponseEntity<>(HttpStatus.OK);
        }


        // Add cookies (Removing them technically)
        servletResponse.addCookie(userLogoutDTO.getLogoutCookie());
        servletResponse.addCookie(userLogoutDTO.getJwtCookie());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
