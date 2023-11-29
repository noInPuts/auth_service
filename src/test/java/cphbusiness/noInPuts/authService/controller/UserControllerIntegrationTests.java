package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import cphbusiness.noInPuts.authService.service.RabbitMessagePublisher;
import cphbusiness.noInPuts.authService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUserShouldReturnAccountWithID() throws Exception {
        // Sending a post request to the create endpoint with the user credentials
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"id\":1,\"username\":\"test_user\", \"password\": null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void createUserShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        // Sending a post request to the create endpoint with a blank username
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserShouldReturnUnsupportedMediaTypeWhenNotParsingJson() throws Exception {
        // Sending a post request to the create endpoint with wrong content type
        this.mockMvc.perform(post("/api/user/create").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void createUserShouldReturnConfictWhenUserAlreadyExists() throws Exception {
        // Creating a user and saving it to the database
        User user = new User("test_user", "Password1!");
        userRepository.save(user);

        // Sending a post request to the create endpoint with the same user credentials
        this.mockMvc.perform(post("/api/user/create").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isConflict());
    }

    @Test
    public void loginShouldReturnWithID() throws Exception {
        // Creating a user and saving it to the database
        userService.createUser(new UserDTO("test_user", "Password1!"));

        // Sending a post request to the login endpoint with the user credentials
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"id\":1,\"username\":\"test_user\", \"password\": null}"));
    }

    @Test
    public void loginShouldReturnWithID2() throws Exception {
        // Creating a user and saving it to the database
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
        User user = new User("test_user", argon2PasswordEncoder.encode("Password1!"));
        userRepository.save(user);

        // Sending a post request to the login endpoint with the user credentials
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"test_user\", \"password\": null}"));
    }

    @Test
    public void loginShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        // Sending a post request to the login endpoint with a blank username
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturnUnsupportedMediaTypeWhenNotParsingJson() throws Exception {
        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/api/user/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void loginShouldReturnBadRequestWhenCredentialsAreWrong() throws Exception {
        // Creating a user and saving it to the database
        User user = new User("test_user", "Password1!");
        userRepository.save(user);

        // Sending a post request to the login endpoint with the wrong password
        this.mockMvc.perform(post("/api/user/login").content("{ \"username\": \"\", \"password\": \"Password2!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
