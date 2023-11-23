package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.Restaurant;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

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
        Faker faker = new Faker();
        String username = "employee_user";
        String password = "employee_pass";

        String restaurantName = faker.restaurant().name();

        when(restaurantEmployeeRepository.findByUsername(username)).thenReturn(Optional.of(new RestaurantEmployee(1L, username, argon2PasswordEncoder.encode(password), new Restaurant(1L, restaurantName, null))));

        RestaurantEmployeeDTO restaurantEmployee = restaurantEmployeeService.login(username, password);

        assertEquals(restaurantEmployee.getUsername(), username);
        assertNull(restaurantEmployee.getPassword());
        assertEquals(restaurantEmployee.getRestaurant().getName(), restaurantName);
    }

    @Test
    public void loginShouldThrowUserNotFoundExceptionWhenNoUserIsExistsInDB() {
        assertThrows(UserDoesNotExistException.class, () -> {
            restaurantEmployeeService.login("employee_user", "employee_pass");
        });
    }

    @Test
    public void loginShouldThrowWrongCredentianlsWhenWrongPasswordIsProvided() {
        Faker faker = new Faker();
        String username = "employee_user";
        String password = "employee_pass";

        String restaurantName = faker.restaurant().name();

        when(restaurantEmployeeRepository.findByUsername(username)).thenReturn(Optional.of(new RestaurantEmployee(1L, username, argon2PasswordEncoder.encode(password), new Restaurant(1L, restaurantName, null))));


        assertThrows(WrongCredentialsException.class, () -> {
            restaurantEmployeeService.login(username, "wrong_password");
        });
    }
}
