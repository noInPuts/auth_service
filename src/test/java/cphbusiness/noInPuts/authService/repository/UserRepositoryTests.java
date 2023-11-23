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
        User user = userRepository.save(new User("test_user", "password"));

        assertNotNull(user);
        assertEquals("test_user", user.getUsername());
        assertEquals("password", user.getPassword());
    }

    @Test
    public void getUserShouldReturnOptionalOfUser() {
        userRepository.save(new User("test_user", "password"));
        Optional<User> user = userRepository.findByUsername("test_user");

        assertTrue(user.isPresent());
        assertEquals("test_user", user.get().getUsername());
    }
}
