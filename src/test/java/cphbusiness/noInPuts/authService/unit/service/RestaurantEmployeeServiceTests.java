package cphbusiness.noInPuts.authService.unit.service;

import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.Restaurant;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import cphbusiness.noInPuts.authService.service.RestaurantEmployeeService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RestaurantEmployeeServiceTests {

    @Autowired
    private RestaurantEmployeeService restaurantEmployeeService;

    @MockBean
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);


    @Test
    public void loginShouldReturnEmployeeObject() throws UserDoesNotExistException, WrongCredentialsException {
        // Arrange
        // Mocking RestaurantEmployeeRepository
        Map<String, String> userAndRestaurantInfo = mockRestaurantEmployeeRepository();

        // Act
        // Calling the login method with the restaurant employee user credentials
        RestaurantEmployeeDTO restaurantEmployee = restaurantEmployeeService.login(userAndRestaurantInfo.get("username"), userAndRestaurantInfo.get("password"));

        // Assert
        // Asserting that the restaurant employee is not null and that the username and restaurant name is correct
        assertEquals(restaurantEmployee.getUsername(), userAndRestaurantInfo.get("username"));
        assertNull(restaurantEmployee.getPassword());
        assertEquals(restaurantEmployee.getRestaurant().getName(), userAndRestaurantInfo.get("restaurantName"));
    }

    @Test
    public void loginShouldThrowUserNotFoundExceptionWhenNoUserIsExistsInDB() {
        // Act and Assert
        // Asserting that the login method throws UserDoesNotExistException when no user is found in the DB
        assertThrows(UserDoesNotExistException.class, () -> {
            restaurantEmployeeService.login("employee_user", "employee_pass");
        });
    }

    @Test
    public void loginShouldThrowWrongCredentianlsWhenWrongPasswordIsProvided() {
        // Arrange
        // Mocking RestaurantEmployeeRepository
        Map<String, String> userCredentials = mockRestaurantEmployeeRepository();

        // Act and Assert
        // Asserting that the login method throws WrongCredentialsException when wrong password is provided
        assertThrows(WrongCredentialsException.class, () -> {
            restaurantEmployeeService.login(userCredentials.get("username"), "wrong_password");
        });
    }

    private Map<String, String> mockRestaurantEmployeeRepository() {
        // Arrange
        // Generating fake data and mocking the RestaurantEmployeeRepository
        Faker faker = new Faker();
        String username = "employee_user";
        String password = "employee_pass";
        String restaurantName = faker.restaurant().name();
        when(restaurantEmployeeRepository.findByUsername(username)).thenReturn(Optional.of(new RestaurantEmployee(1L, username, argon2PasswordEncoder.encode(password), new Restaurant(1L, restaurantName, null))));

        // Creating a map with the user credentials and restaurant name
        Map<String, String> userCredentials = new HashMap<>();
        userCredentials.put("username", username);
        userCredentials.put("password", password);
        userCredentials.put("restaurantName", restaurantName);

        return userCredentials;
    }
}
