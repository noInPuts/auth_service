package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.service.JwtService;
import cphbusiness.noInPuts.authService.service.RabbitMessagePublisher;
import cphbusiness.noInPuts.authService.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RabbitMessagePublisher rabbitMessagePublisher;

    @Test
    public void createUserShouldReturnUserWithID() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the create endpoint with the user credentials
        this.mockMvc.perform(post("/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"test_user\", \"password\": null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request with missing entry
        this.mockMvc.perform(post("/user/create").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the create endpoint with wrong content type
        this.mockMvc.perform(post("/user/create").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingEmptyUsername() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the create endpoint with a blank username
        this.mockMvc.perform(post("/user/create").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn400BadRequestWhenParsingEmptyPassword() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the create endpoint with a blank password
        this.mockMvc.perform(post("/user/create").content("{ \"username\": \"user_test\", \"password\": \"\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturn409ConflictWhenUsernameAlreadyExists() throws Exception {
        // Mocking the userService and jwtService
        when(userService.createUser(any(UserDTO.class))).thenThrow(new UserAlreadyExistsException("Username already exists."));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");

        // Sending a post request to the create endpoint with the same user credentials
        this.mockMvc.perform(post("/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isConflict());
    }

    @Test
    public void loginShouldReturnUserWithID() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the login endpoint with the user credentials
        this.mockMvc.perform(post("/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"test_user\", \"password\": null}"));
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenParsingWrongUserCredentianls() throws Exception {
        // Mocking the userService and jwtService
        when(userService.login(any(UserDTO.class))).thenThrow(new WrongCredentialsException("Wrong password."));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");

        // Sending a post request to the login endpoint with the wrong user credentials
        this.mockMvc.perform(post("/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenUserDoesNotExists() throws Exception {
        // Mocking the userService and jwtService
        when(userService.login(any(UserDTO.class))).thenThrow(new UserDoesNotExistException("User is not found in the db"));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");

        // Sending a post request to the login endpoint with wrong username
        this.mockMvc.perform(post("/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the login endpoint with missing entry
        this.mockMvc.perform(post("/user/login").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        // Mocking the userService and jwtService
        mockUserServiceAndJwtService();

        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/user/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void logoutShouldReturnBadRequestWhenNotParsingJWTToken() throws Exception {
        // Sending a post request to the logout endpoint with wrong content type
        this.mockMvc.perform(post("/user/logout").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logout() throws Exception {
        // Creating a dummy cookie
        Cookie cookie = new Cookie("jwt-token", "dummyToken");

        // Sending a post request to the logout endpoint with the cookie
        this.mockMvc.perform(post("/user/logout").cookie(cookie))
                .andExpect(status().isOk());
    }

    private void mockUserServiceAndJwtService() throws UserAlreadyExistsException, WrongCredentialsException, WeakPasswordException, UserDoesNotExistException {
        // Mocking the userService and jwtService
        when(userService.createUser(any(UserDTO.class))).thenReturn(new UserDTO(1, "test_user"));
        when(userService.login(any(UserDTO.class))).thenReturn(new UserDTO(1, "test_user"));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");
    }
}
