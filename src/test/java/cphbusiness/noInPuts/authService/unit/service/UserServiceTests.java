package cphbusiness.noInPuts.authService.unit.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import cphbusiness.noInPuts.authService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);

    @Test
    public void createUserShouldReturnWithID() throws UserAlreadyExistsException, WeakPasswordException {
        // Arrange
        // Mocking the userRepository
        User user = new User("test_user", "password");
        user.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        // Calling the createUser method with the userDTO
        String username = "test_user";
        UserDTO createdUserDTO = userService.createUser(username, "Password1!");

        // Assert
        // Asserting that the createdUserDTO is not null and that the username and id is correct
        assertEquals(createdUserDTO.getUsername(), username);
        assertEquals(createdUserDTO.getId(), 1);
    }

    @Test
    public void createUserShouldThrowExceptionWhenUserAlreadyExist() {
        // Arrange
        // Mocking the userRepository
        User user = new User("user", "Password1!");
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        // Act and Assert
        // Assert that the createUser method throws UserAlreadyExistsException when the user already exists
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser("test_user", "Password1!"));
    }

    @Test
    public void createUserShouldThrowExceptionWhenPasswordIsToWeak() {
        // Act and Assert
        // Assert that the createUser method throws WeakPasswordException when the password is to weak
        assertThrows(WeakPasswordException.class, () -> userService.createUser("test_user", "weak"));
    }

    @Test
    public void loginShouldReturnUserWithID() throws WrongCredentialsException, UserDoesNotExistException {
        // Arrange
        // Mocking the userRepository
        User userEntity = new User("test_user", argon2PasswordEncoder.encode("Password1!"));
        userEntity.setId(1);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        // Act
        // Calling the login method with the userDTO
        UserDTO user = userService.login("test_user", "Password1!");

        // Assert
        // Asserting that the user is not null and that the username and id is correct
        assertEquals(1, user.getId());
    }

    @Test
    public void loginShouldThrowExceptionWhenUserDoNotExists() {
        // Arrange
        // Mocking the userRepository
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        // Act and Assert
        // Asserting that the login method throws an UserDoesNotExistException when the user does not exists
        assertThrows(UserDoesNotExistException.class, () -> userService.login("test_user", "Password1!"));
    }

    @Test
    public void loginShouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange
        // Mocking the userRepository
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User("test_user", argon2PasswordEncoder.encode("Password1!"))));

        // Act and Assert
        // Asserting that the login method throws an WrongCredentialsException when the password is wrong
        assertThrows(WrongCredentialsException.class, () -> userService.login("test_user", "Password2!"));
    }

    @Test
    public void loginBenchmark() throws WrongCredentialsException, UserDoesNotExistException {
        // Arrange
        // Mocking the userRepository
        User userEntity = new User("test_user", argon2PasswordEncoder.encode("Password1!"));
        userEntity.setId(1);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        // Act
        // Calling the login method with the userDTO
        UserDTO user = userService.login("test_user", "Password1!");

        // Assert
        // Asserting that the user is not null and that the username and id is correct
        assertEquals(1, user.getId());
    }
}
