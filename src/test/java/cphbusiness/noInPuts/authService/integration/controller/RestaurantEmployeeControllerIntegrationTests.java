package cphbusiness.noInPuts.authService.integration.controller;

import cphbusiness.noInPuts.authService.model.Restaurant;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import cphbusiness.noInPuts.authService.repository.RestaurantRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testcontainer-flyway")
public class RestaurantEmployeeControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);

    @BeforeEach
    public void setUp() {
        // Arrange
        // Creating a restaurant and employee and saving it to the database
        Faker faker = new Faker();
        Restaurant restaurant = new Restaurant(faker.restaurant().name());
        Restaurant restaurantEntity = restaurantRepository.save(restaurant);
        RestaurantEmployee restaurantEmployee = new RestaurantEmployee("employee_user", argon2PasswordEncoder.encode("Password1!"), restaurantEntity);
        restaurantEmployeeRepository.save(restaurantEmployee);
    }

    @Test
    public void loginShouldReturnWithID() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with the employee user credentials
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"id\":1,\"username\":\"employee_user\", \"password\": null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with a blank username
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturnUnsupportedMediaTypeWhenNotParsingJson() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void loginShouldReturnBadRequestWhenCredentialsAreWrong() throws Exception {
        // Act and Assert
        // Sending a post request to the login endpoint with the wrong password
        this.mockMvc.perform(post("/api/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password2!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
