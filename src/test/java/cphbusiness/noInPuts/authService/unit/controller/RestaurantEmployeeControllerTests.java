package cphbusiness.noInPuts.authService.unit.controller;

import cphbusiness.noInPuts.authService.controller.RestaurantEmployeeController;
import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeLoginDTO;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantEmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RestaurantEmployeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceFacade serviceFacade;

    @Test
    public void loginShouldReturnEmployeeObjectAndJWT() throws Exception {
        // Arrange
        // Mocking the serviceFacade restaurantEmployeeLogin method
        RestaurantEmployeeDTO restaurantEmployeeDTO = new RestaurantEmployeeDTO(1L, "employee_user", null, null);
        Cookie jwtCookie = new Cookie("jwt-token", "dummyCookie");
        when(serviceFacade.restaurantEmployeeLogin("employee_user", "Password1!")).thenReturn(new RestaurantEmployeeLoginDTO(jwtCookie, restaurantEmployeeDTO));

        // Act and Assert
        // Sending a post request to the login endpoint with the employee user credentials
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ \"username\": \"employee_user\", \"password\": null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Act and Assert
        // Sending a post request with missing entry
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenWrongCredentials() throws Exception {
        // Arrange
        // Mocking the serviceFacade restaurantEmployeeLogin method
        when(serviceFacade.restaurantEmployeeLogin("employee_user", "Password1!"))
                .thenThrow(new WrongCredentialsException("Wrong credentials"));

        // Act and Assert
        // Sending a post request to the login endpoint with the wrong employee user credentials
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenUserDoesNotExist() throws Exception {
        // Arrange
        // Mocking the serviceFacade restaurantEmployeeLogin method
        when(serviceFacade.restaurantEmployeeLogin("employee_user", "Password1!")).thenThrow(new UserDoesNotExistException("User does not exist"));

        // Act and Assert
        // Sending a post request to the login endpoint with wrong username and password
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
