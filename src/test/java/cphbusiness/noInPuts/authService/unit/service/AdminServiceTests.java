package cphbusiness.noInPuts.authService.unit.service;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import cphbusiness.noInPuts.authService.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AdminServiceTests {

    @Autowired
    private AdminService adminService;

    @MockBean
    private AdminRepository adminRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);

    @Test
    public void loginShouldReturnAdmin() throws UserDoesNotExistException, WrongCredentialsException {
        // Arrange
        // Mocking the adminRepository
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"))));

        // Act
        // Calling the login method with the admin user credentials
        AdminDTO adminUser = adminService.login("admin", "Password1!");

        // Assert
        // Asserting that the admin user is not null and that the username and id is correct
        assertEquals("admin", adminUser.getUsername());
        assertEquals(1, adminUser.getId());
        assertNull(adminUser.getPassword());
    }


    @Test
    public void loginShouldReturnUserWithID() throws WrongCredentialsException, UserDoesNotExistException {
        // Arrange
        // Mocking the adminRepository
        Admin adminEntity = new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"));
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(adminEntity));

        // Act
        // Calling the login method with the admin user credentials
        AdminDTO adminUser = adminService.login("admin", "Password1!");

        // Assert
        // Asserting that the admin user is not null and that the username and id is correct
        assertEquals(1, adminUser.getId());
        assertEquals("admin", adminUser.getUsername());
    }

    @Test
    public void loginShouldThrowExceptionWhenUserDoNotExists() {
        // Arrange
        // Mocking the adminRepository
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        // Act and Assert
        // Asserting that the login method throws an UserDoesNotExistException when the user does not exists
        assertThrows(UserDoesNotExistException.class, () -> adminService.login("test_user", "Password1!"));
    }

    @Test
    public void loginShouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange
        // Mocking the adminRepository
        Admin adminEntity = new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"));
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(adminEntity));

        // Act and Assert
        // Asserting that the login method throws an WrongCredentialsException when the password is wrong
        assertThrows(WrongCredentialsException.class, () -> adminService.login("admin", "Password2!"));
    }
}
