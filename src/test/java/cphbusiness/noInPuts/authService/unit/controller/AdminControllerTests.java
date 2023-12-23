package cphbusiness.noInPuts.authService.unit.controller;

import cphbusiness.noInPuts.authService.controller.AdminController;
import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.dto.AdminLoginDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceFacade serviceFacade;


    @Test
    public void loginShouldReturnAdmin() throws Exception {
        // Arrange
        // Mocking serviceFacade adminLogin method
        mockAdminLoginMethod();

        // Act and Assert
        // Sending a post request to the login endpoint with the admin user credentials
        this.mockMvc.perform(post("/api/auth/admin/login").content("{ \"username\": \"admin\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"admin\", \"password\":null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenParsingWrongUserCredentials() throws Exception {
        // Arrange
        // Mocking serviceFacade adminLogin method
        when(serviceFacade.adminLogin(any(String.class), any(String.class))).thenThrow(new WrongCredentialsException("Wrong password."));

        // Act and Assert
        // Sending a post request to the login endpoint with the wrong admin user credentials
        this.mockMvc.perform(post("/api/auth/admin/login").content("{ \"username\": \"admin\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn404NotFoundWhenUserDoesNotExists() throws Exception {
        // Arrange
        // Mocking serviceFacade adminLogin method
        when(serviceFacade.adminLogin(any(String.class), any(String.class))).thenThrow(new UserDoesNotExistException("User is not found in the db"));

        // Act and Assert
        // Sending a post request to the login endpoint with wrong username
        this.mockMvc.perform(post("/api/auth/admin/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Arrange
        // Mocking adminService and jwtService
        mockAdminLoginMethod();

        // Act and Assert
        // Sending a post request to the login endpoint with missing entry
        this.mockMvc.perform(post("/api/auth/admin/login").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        // Arrange
        // Mocking adminService and jwtService
        mockAdminLoginMethod();

        // Act and Assert
        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/api/auth/admin/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    private void mockAdminLoginMethod() throws WrongCredentialsException, UserDoesNotExistException {
        // Arrange
        // Creating fake AdminDTO & fake Cookie
        AdminDTO adminDTO = new AdminDTO(1L, "admin", null);
        Cookie jwtCookie = new Cookie("jwt-token", "dummyToken");

        // Act and Assert
        // Mocking serviceFacade
        when(serviceFacade.adminLogin(any(String.class), any(String.class))).thenReturn(new AdminLoginDTO(jwtCookie, adminDTO));
    }
}
