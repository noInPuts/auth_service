package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.model.Restaurant;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import cphbusiness.noInPuts.authService.repository.RestaurantRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestaurantEmployeeControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);


    @Test
    public void loginShouldReturnWithID() throws Exception {
        Faker faker = new Faker();
        Restaurant restaurant = new Restaurant(faker.restaurant().name());
        Restaurant restaurantEntity = restaurantRepository.save(restaurant);
        RestaurantEmployee restaurantEmployee = new RestaurantEmployee("employee_user", argon2PasswordEncoder.encode("Password1!"), restaurantEntity);
        restaurantEmployeeRepository.save(restaurantEmployee);

        this.mockMvc.perform(post("/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"user\": { \"id\":1,\"username\":\"employee_user\", \"password\": null}}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        this.mockMvc.perform(post("/restaurantEmployee/login").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturnUnsupportedMediaTypeWhenNotParsingJson() throws Exception {
        this.mockMvc.perform(post("/restaurantEmployee/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void loginShouldReturnBadRequestWhenCredentialsAreWrong() throws Exception {
        Faker faker = new Faker();
        Restaurant restaurant = new Restaurant(faker.restaurant().name());
        Restaurant restaurantEntity = restaurantRepository.save(restaurant);
        RestaurantEmployee restaurantEmployee = new RestaurantEmployee("employee_user", argon2PasswordEncoder.encode("Password1!"), restaurantEntity);
        restaurantEmployeeRepository.save(restaurantEmployee);

        this.mockMvc.perform(post("/restaurantEmployee/login").content("{ \"username\": \"employee_user\", \"password\": \"Password2!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
