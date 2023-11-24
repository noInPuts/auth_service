package cphbusiness.noInPuts.authService.repository;

import cphbusiness.noInPuts.authService.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    UserRepository userRepository;

    @Test
    public void saveUserTest() {
        // Creating an admin user and saving it to the database
        User user = userRepository.save(new User("test_user", "password"));

        // Asserting that the admin user is not null and that the username and password is correct
        assertNotNull(user);
        assertEquals("test_user", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void getUserShouldReturnOptionalOfUser() {
        // Creating an admin user and saving it to the database
        userRepository.save(new User("test_user", "password"));

        // Getting the user from the database by the username
        Optional<User> user = userRepository.findByUsername("test_user");

        // Asserting that the user is present and that the username is correct
        assertTrue(user.isPresent());
        assertEquals("test_user", user.get().getUsername());
    }
}
