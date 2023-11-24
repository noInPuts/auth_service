package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.AlreadyLoggedOutException;
import cphbusiness.noInPuts.authService.repository.JwtRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTests {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtRepository jwtRepository;

    @Test
    public void generateTokenShouldReturnToken() throws NoSuchAlgorithmException {
        // Generating a private key and setting it to the jwtService
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA512");
        String mockedPrivateKey = String.valueOf(keyGenerator.generateKey());

        // Setting the private key to the jwtService
        ReflectionTestUtils.setField(jwtService, "pKey", mockedPrivateKey);

        // Creating a userDTO and generating a token
        UserDTO userDTO = new UserDTO("test_user", "Password1!");
        String jwtToken = jwtService.tokenGenerator(userDTO.getId(), userDTO.getUsername(), "user");

        // Asserting that the token is not null and that it is a string
        assertEquals(String.class, jwtToken.getClass());
    }

    @Test
    public void logoutShouldReturnAlreadyLoggedOutException() throws AlreadyLoggedOutException {
        // Creating a token and logging out
        String jwtToken = jwtService.tokenGenerator(1L, "test_user", "user");
        jwtService.logout(jwtToken);

        // Asserting that the logout method throws, when the jwt token is already blacklisted
        assertThrows(AlreadyLoggedOutException.class, () -> jwtService.logout(jwtToken));
    }

    @Test
    public void logout() throws AlreadyLoggedOutException {
        // Creating a token and logging out
        String jwtToken = jwtService.tokenGenerator(1L, "test_user", "user");
        jwtService.logout(jwtToken);

        // Asserting that the token is blacklisted
        assertTrue(jwtRepository.existsById(jwtToken));
    }
}
