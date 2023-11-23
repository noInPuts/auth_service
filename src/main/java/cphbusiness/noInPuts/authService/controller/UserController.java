package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.*;
import cphbusiness.noInPuts.authService.service.JwtService;
import cphbusiness.noInPuts.authService.service.RabbitMessagePublisher;
import cphbusiness.noInPuts.authService.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600, allowCredentials = "true")
@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    //private final RabbitMessagePublisher rabbitMessagePublisher;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        //this.rabbitMessagePublisher = rabbitMessagePublisher;
    }

    // Endpoint for creating a user account
    @PostMapping(value = "/user/create", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: Swagger documentation
    // TODO: Spam check
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse) {

        // Catch exception if user already exists else create entity in DB
        UserDTO userDTO;
        try {
            userDTO = userService.createUser(POSTuserDTO);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (WeakPasswordException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Genereate JWT token for user
        String jwtToken = jwtService.tokenGenerator(userDTO.getId(), userDTO.getUsername(), "user");

        // Send out createdUserEvent to RabbitMQ/Apache Camel
        //rabbitMessagePublisher.createdUserEvent("User created: " + userDTO.getUsername());

        // Creates response entity with userDTO and JWT token
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDTO);
        Cookie cookie = new Cookie("jwt-token", jwtToken);
        //cookie.setHttpOnly(true);
        cookie.setMaxAge(2 * 24 * 60 * 60);
        cookie.setPath("/");

        servletResponse.addCookie(cookie);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Endpoint for logging in to a user account
    @PostMapping(value = "/user/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserDTO POSTuserDTO, HttpServletResponse servletResponse) {

        // Check for correct credentials
        UserDTO userDTO;
        try {
            userDTO = userService.login(POSTuserDTO);
        } catch (WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Generate JWT token for user
        String jwtToken = jwtService.tokenGenerator(userDTO.getId(), userDTO.getUsername(), "user");

        // Creates response entity with userDTO and JWT token
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDTO);

        Cookie cookie = new Cookie("jwt-token", jwtToken);

        // cookie expires in 2 days
        cookie.setMaxAge(2 * 24 * 60 * 60);
        cookie.setPath("/");
        // TODO: Set TLS/SSL certificate
        //cookie.setSecure(true);
        //cookie.setHttpOnly(true);
        servletResponse.addCookie(cookie);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/user/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> logout(@Valid @CookieValue("jwt-token") String jwtToken, HttpServletResponse servletResponse) {

        if(jwtToken.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            jwtService.logout(jwtToken);
        } catch (AlreadyLoggedOutException e) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        Cookie deleteCookie = new Cookie("jwt-token", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");

        servletResponse.addCookie(deleteCookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
