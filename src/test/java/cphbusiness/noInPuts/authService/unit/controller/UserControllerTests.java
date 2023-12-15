package cphbusiness.noInPuts.authService.unit.controller;

import cphbusiness.noInPuts.authService.controller.UserController;
import cphbusiness.noInPuts.authService.dto.UserCreateDTO;
import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.dto.UserLoginDTO;
import cphbusiness.noInPuts.authService.dto.UserLogoutDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import cphbusiness.noInPuts.authService.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceFacade serviceFacade;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    public void mockUserServiceAndJwtService() throws UserAlreadyExistsException, WrongCredentialsException, WeakPasswordException, UserDoesNotExistException {
        // Arrange
        // Mocking serviceFacade
        Cookie jwtCookie = new Cookie("jwt-token", "dummyToken");
        Cookie loginCookie = new Cookie("login-status", "true");
        UserDTO user = new UserDTO(1, "test_user");

        when(serviceFacade.userLogin(any(String.class), any(String.class))).thenReturn(new UserLoginDTO(jwtCookie, loginCookie, user));
        when(serviceFacade.userCreateAccount(any(String.class), any(String.class))).thenReturn(new UserCreateDTO(jwtCookie, loginCookie, user));
    }

    @Test
    public void createUserShouldReturnUserWithID() throws Exception {
        // Act and Assert
        // Sending a post request to the create endpoint with the user credentials
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"test_user\", \"password\": null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Act and Assert
        // Sending a post request with missing entry
        this.mockMvc.perform(post("/api/user/create").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        // Act and Assert
        // Sending a post request to the create endpoint with wrong content type
        this.mockMvc.perform(post("/api/user/create").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingEmptyUsername() throws Exception {
        // Act and Assert
        // Sending a post request to the create endpoint with a blank username
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingEmptyPassword() throws Exception {
        // Act and Assert
        // Sending a post request to the create endpoint with a blank password
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"user_test\", \"password\": \"\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn409ConflictWhenUsernameAlreadyExists() throws Exception {
        // Arrange
        // Mocking the serviceFacade
        when(serviceFacade.userCreateAccount(any(String.class), any(String.class))).thenThrow(new UserAlreadyExistsException("Username already exists."));

        // Act and Assert
        // Sending a post request to the create endpoint with the same user credentials
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isConflict());
    }

    @Test
    public void loginShouldReturnUserWithID() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with the user credentials
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"test_user\", \"password\": null}"));
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenParsingWrongUserCredentianls() throws Exception {
        // Arrange
        // Mocking the serviceFacade
        when(serviceFacade.userLogin(any(String.class), any(String.class))).thenThrow(new WrongCredentialsException("Wrong password."));

        // Act and Assert
        // Sending a post request to the login endpoint with the wrong user credentials
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenUserDoesNotExists() throws Exception {
        // Arrange
        // Mocking the userService and jwtService
        when(serviceFacade.userLogin(any(String.class), any(String.class))).thenThrow(new UserDoesNotExistException("User is not found in the db"));

        // Act and Assert
        // Sending a post request to the login endpoint with wrong username
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with missing entry
        this.mockMvc.perform(post("/api/user/login").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/api/user/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void logoutShouldReturnBadRequestWhenNotParsingJWTToken() throws Exception {
        // Act and Assert
        // Sending a post request to the logout endpoint with wrong content type
        this.mockMvc.perform(post("/api/user/logout").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logout() throws Exception {
        // Act
        // Mocking serviceFacade logout method and creating dummy cookie
        Cookie jwtCookie = new Cookie("jwt-token", "dummyToken");
        Cookie loginStatusCookie = new Cookie("login-status", null);
        when(serviceFacade.userLogout(any(String.class))).thenReturn(new UserLogoutDTO(jwtCookie, loginStatusCookie));
        Cookie cookie = new Cookie("jwt-token", "dummyToken");

        // Act and Assert
        // Sending a post request to the logout endpoint with the cookie
        this.mockMvc.perform(post("/api/user/logout").cookie(cookie))
                .andExpect(status().isOk());
    }

}
