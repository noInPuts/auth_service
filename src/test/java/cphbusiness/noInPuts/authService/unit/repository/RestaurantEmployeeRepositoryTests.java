package cphbusiness.noInPuts.authService.unit.repository;

import cphbusiness.noInPuts.authService.model.Restaurant;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import cphbusiness.noInPuts.authService.repository.RestaurantRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("testcontainer-flyway")
public class RestaurantEmployeeRepositoryTests {

    @Autowired
    private RestaurantEmployeeRepository restaurantEmployeeRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void getRestaurantEmployeesByRestaurantId() {
        // Arrange
        // Creating object that can generate fake data and persisting a restaurant employee object
        Faker faker = new Faker();
        Restaurant restaurantEntity = peristRestaurant(faker);
        RestaurantEmployee restaurantEmployeeEntity = persistRestaurantEmployee(restaurantEntity);

        // Act
        // Getting the restaurant employee object from the DB by the restaurant id
        List<RestaurantEmployee> restaurantEmployees = restaurantEmployeeRepository.findAllByRestaurantId(restaurantEntity.getId());

        // Assert
        // Asserting that the restaurant employee object is not null and that the username is correct
        assertNotNull(restaurantEmployeeEntity);
        assertEquals(restaurantEmployeeEntity.getUsername(), restaurantEmployees.get(0).getUsername());
    }

    @Test
    public void getRestaurantEmployeeById() {
        // Arrange
        // Creating object that can generate fake data and persisting a restaurant employee object
        Faker faker = new Faker();
        Restaurant restaurantEntity = peristRestaurant(faker);
        RestaurantEmployee restaurantEmployeeEntity = persistRestaurantEmployee(restaurantEntity);

        // Act
        // Getting the restaurant employee object from the DB by the restaurant employee id
        Optional<RestaurantEmployee> restaurantEmployee = restaurantEmployeeRepository.findById(restaurantEmployeeEntity.getId());

        // Assert
        // Asserting that the restaurant employee object is present and that the username is correct
        assertTrue(restaurantEmployee.isPresent());
        assertEquals(restaurantEmployeeEntity.getUsername(), restaurantEmployee.get().getUsername());
    }

    @Test
    public void getRestaurantEmployeeByUsername() {
        // Arrange
        // Creating object that can generate fake data and persisting a restaurant employee object
        Faker faker = new Faker();
        Restaurant restaurantEntity = peristRestaurant(faker);
        RestaurantEmployee restaurantEmployeeEntity = persistRestaurantEmployee(restaurantEntity);

        // Act
        // Getting the restaurant employee object from the DB by the restaurant employee username
        Optional<RestaurantEmployee> restaurantEmployee = restaurantEmployeeRepository.findByUsername(restaurantEmployeeEntity.getUsername());

        // Assert
        // Asserting that the restaurant employee object is present and that the username is correct
        assertTrue(restaurantEmployee.isPresent());
        assertEquals(restaurantEmployeeEntity.getUsername(), restaurantEmployee.get().getUsername());
    }

    private Restaurant peristRestaurant(Faker faker) {
        // Arrange
        // Creating a restaurant object with a random restaurant name and saving it in the DB
        Restaurant restaurant = new Restaurant(faker.restaurant().name());

        return restaurantRepository.save(restaurant);
    }

    private RestaurantEmployee persistRestaurantEmployee(Restaurant restaurantEntity) {
        // Arrange
        // Creating a restaurant employee object with a random name and the restaurant object created above and saving it in the DB
        RestaurantEmployee restaurantEmployee = new RestaurantEmployee("employee_user", "password", restaurantEntity);

        return restaurantEmployeeRepository.save(restaurantEmployee);
    }
}
