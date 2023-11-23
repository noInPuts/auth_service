package cphbusiness.noInPuts.authService.repository;

import cphbusiness.noInPuts.authService.model.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class AdminRepositoryTests {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    public void saveAdmin() {
        Admin admin = adminRepository.save(new Admin("admin", "password"));

        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertEquals("password", admin.getPassword());
    }

    @Test
    public void getUserShouldReturnOptionalOfUser() {
        adminRepository.save(new Admin("admin", "password"));
        Optional<Admin> adminUser = adminRepository.findByUsername("admin");

        assertTrue(adminUser.isPresent());
        assertEquals("admin", adminUser.get().getUsername());
    }
}
