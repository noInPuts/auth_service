package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.dto.AdminLoginDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 3600)
@RestController("/api/auth/admin")
public class AdminController {

    private final ServiceFacade serviceFacade;

    @Autowired
    public AdminController(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    // Endpoint for logging in to an admin account
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AdminDTO> login(@Valid @RequestBody AdminDTO postAdminDTO, HttpServletResponse servletResponse) {

        // Creating a cookie and adminDTO
        Cookie jwtCookie;
        AdminDTO adminAccount;

        try {
            // Gets admin account, if it doesn't exist or the password is wrong, return 401 or 404
            AdminLoginDTO result = serviceFacade.adminLogin(postAdminDTO.getUsername(), postAdminDTO.getPassword());
            jwtCookie = result.getJwtCookie();
            adminAccount = result.getAdminAccount();
        } catch (WrongCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (UserDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Adds cookie to response and returns adminDTO
        servletResponse.addCookie(jwtCookie);
        return new ResponseEntity<>(adminAccount, HttpStatus.OK);
    }
}
