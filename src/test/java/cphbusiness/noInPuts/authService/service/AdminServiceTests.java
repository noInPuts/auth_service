package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
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
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"))));

        AdminDTO adminUser = adminService.login(new AdminDTO("admin", "Password1!"));

        assertEquals("admin", adminUser.getUsername());
        assertEquals(1, adminUser.getId());
        assertNull(adminUser.getPassword());
    }


    @Test
    public void loginShouldReturnUserWithID() throws WrongCredentialsException, UserDoesNotExistException {
        Admin adminEntity = new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"));
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(adminEntity));

        AdminDTO adminUser = adminService.login(new AdminDTO("admin", "Password1!"));

        assertEquals(1, adminUser.getId());
        assertEquals("admin", adminUser.getUsername());
    }

    @Test
    public void loginShouldThrowExceptionWhenUserDoNotExists() {
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> adminService.login(new AdminDTO("test_user", "Password1!")));
    }

    @Test
    public void loginShouldThrowExceptionWhenPasswordIsWrong() {
        Admin adminEntity = new Admin(1L, "admin", argon2PasswordEncoder.encode("Password1!"));
        when(adminRepository.findByUsername(any(String.class))).thenReturn(Optional.of(adminEntity));

        assertThrows(WrongCredentialsException.class, () -> adminService.login(new AdminDTO("admin", "Password2!")));

    }
}
