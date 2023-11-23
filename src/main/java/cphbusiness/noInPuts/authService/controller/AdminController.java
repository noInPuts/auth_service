package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.service.AdminService;
import cphbusiness.noInPuts.authService.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(maxAge = 3600)
@RestController
public class AdminController {

    private final AdminService adminService;
    private final JwtService jwtService;

    @Autowired
    public AdminController(AdminService adminService, JwtService jwtService) {
        this.adminService = adminService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/admin/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AdminDTO postAdminDTO, HttpServletResponse servletResponse) throws UserDoesNotExistException {
        AdminDTO adminUser;
        try {
            adminUser = adminService.login(postAdminDTO);
        } catch (WrongCredentialsException | UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String JwtToken = jwtService.tokenGenerator(adminUser.getId(), adminUser.getUsername(), "admin");

        // TODO: Remove map
        Map<String, Object> response = new HashMap<>();
        response.put("user", adminUser);

        Cookie cookie = new Cookie("jwt-token", JwtToken);
        cookie.setHttpOnly(true);

        // Cookie is set to expire in 24 hours
        cookie.setMaxAge(24 * 60 * 60);
        servletResponse.addCookie(cookie);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
