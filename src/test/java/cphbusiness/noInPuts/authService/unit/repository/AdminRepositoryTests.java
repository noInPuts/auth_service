package cphbusiness.noInPuts.authService.unit.repository;

import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@ActiveProfiles("testcontainer-flyway")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdminRepositoryTests {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    public void saveAdmin() {
        // Creating an admin user and saving it to the database
        Admin admin = adminRepository.save(new Admin("admin", "password"));

        // Asserting that the admin user is not null and that the username and password is correct
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertEquals("password", admin.getPassword());
    }

    @Test
    public void getUserShouldReturnOptionalOfUser() {
        // Creating an admin user and saving it to the database
        adminRepository.save(new Admin("admin", "password"));
        Optional<Admin> adminUser = adminRepository.findByUsername("admin");

        // Asserting that the admin user is present and that the username is correct
        assertTrue(adminUser.isPresent());
        assertEquals("admin", adminUser.get().getUsername());
    }
}
