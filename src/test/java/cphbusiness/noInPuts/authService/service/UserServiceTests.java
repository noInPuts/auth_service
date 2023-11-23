package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
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
        User user = new User("test_user", "password");
        user.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO userDTO = new UserDTO("test_user", "Password1!");
        UserDTO createdUserDTO = userService.createUser(userDTO);

        assertEquals(createdUserDTO.getUsername(), userDTO.getUsername());
        assertEquals(createdUserDTO.getId(), 1);
    }

    @Test
    public void createUserShouldThrowExceptionWhenUserAlreadyExist() {
        User user = new User("user", "Password1!");
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        UserDTO userDTO = new UserDTO("test_user", "Password1!");

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDTO));
    }

    @Test
    public void createUserShouldThrowExceptionWhenPasswordIsToWeak() {
        // Create userDTO with weak password
        UserDTO userDTO = new UserDTO("test_user", "weak");

        assertThrows(WeakPasswordException.class, () -> userService.createUser(userDTO));
    }

    @Test
    public void loginShouldReturnUserWithID() throws WrongCredentialsException, UserDoesNotExistException {
        User userEntity = new User("test_user", argon2PasswordEncoder.encode("Password1!"));
        userEntity.setId(1);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(userEntity));

        UserDTO user = userService.login(new UserDTO("test_user", "Password1!"));

        assertEquals(1, user.getId());
    }

    @Test
    public void loginShouldThrowExceptionWhenUserDoNotExists() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> userService.login(new UserDTO("test_user", "Password1!")));
    }

    @Test
    public void loginShouldThrowExceptionWhenPasswordIsWrong() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User("test_user", argon2PasswordEncoder.encode("Password1!"))));

        assertThrows(WrongCredentialsException.class, () -> userService.login(new UserDTO("test_user", "Password2!")));

    }
}
