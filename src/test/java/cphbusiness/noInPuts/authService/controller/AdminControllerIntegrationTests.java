package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);

    @Test
    public void loginShouldReturnWithID() throws Exception {
        // Creating an admin user and saving it to the database
        Admin adminUser = new Admin("admin", argon2PasswordEncoder.encode("Password1!"));
        adminRepository.save(adminUser);

        // Sending a post request to the login endpoint with the admin user credentials
        this.mockMvc.perform(post("/api/admin/login").content("{ \"username\": \"admin\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"id\":1,\"username\":\"admin\", \"password\":null}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        // Sending a post request to the login endpoint with a blank username
        this.mockMvc.perform(post("/api/admin/login").content("{ \"username\": \"\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturnUnsupportedMediaTypeWhenNotParsingJson() throws Exception {
        // Sending a post request to the login endpoint with wrong content type
        this.mockMvc.perform(post("/api/admin/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void loginShouldReturnBadRequestWhenCredentialsAreWrong() throws Exception {
        // Creating an admin user and saving it to the database
        Admin adminUser = new Admin("admin", argon2PasswordEncoder.encode("Password1!"));
        adminRepository.save(adminUser);

        // Sending a post request to the login endpoint with the wrong password
        this.mockMvc.perform(post("/api/admin/login").content("{ \"username\": \"\", \"password\": \"Password2!\" }").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

}
