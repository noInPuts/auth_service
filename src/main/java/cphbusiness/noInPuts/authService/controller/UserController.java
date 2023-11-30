package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.*;
import cphbusiness.noInPuts.authService.service.JwtService;
import cphbusiness.noInPuts.authService.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://167.71.45.53:3000"}, maxAge = 3600, allowCredentials = "true")
@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Endpoint for creating a user account
    @PostMapping(value = "/api/user/create", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: Swagger documentation
    // TODO: Spam check
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse) {

        // Create user account, returns 409 if user already exists or 400 if password is weak
        UserDTO userDTO;
        try {
            // Creates user and returns userDTO
            userDTO = userService.createUser(POSTuserDTO);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (WeakPasswordException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Sets JWT token cookie
        servletResponse.addCookie(getJwtCookie(userDTO.getId(), userDTO.getUsername()));

        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    // Endpoint for logging in to a user account
    @PostMapping(value = "/api/user/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> login(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse) {

        // Check for correct credentials
        UserDTO userDTO;
        try {
            userDTO = userService.login(POSTuserDTO);
        } catch (WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Sets JWT token cookie
        servletResponse.addCookie(getJwtCookie(userDTO.getId(), userDTO.getUsername()));

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/api/user/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> logout(@Valid @CookieValue("jwt-token") String jwtToken, HttpServletResponse servletResponse) {

        // Check if jwtToken is empty
        if(jwtToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Logging user out with jwtService
        try {
            jwtService.logout(jwtToken);
        } catch (AlreadyLoggedOutException e) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        // Delete jwt-token cookie
        Cookie deleteCookie = new Cookie("jwt-token", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        servletResponse.addCookie(deleteCookie);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie getJwtCookie(Long id, String username) {
        // Generate JWT token for user
        String jwtToken = jwtService.tokenGenerator(id, username, "user");
        Cookie cookie = new Cookie("jwt-token", jwtToken);
        cookie.setMaxAge(2 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }

}
