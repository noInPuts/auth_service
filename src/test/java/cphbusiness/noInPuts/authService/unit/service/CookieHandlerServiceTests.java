package cphbusiness.noInPuts.authService.unit.service;

import cphbusiness.noInPuts.authService.service.CookieHandlerService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CookieHandlerServiceTests {

    @Autowired
    private CookieHandlerService cookieHandlerService;

    @Test
    public void createAuthCookieTest() {
        // Act
        Cookie authCookie = cookieHandlerService.getAuthCookie("dummyToken");

        // Assert
        // Asserting Cookie
        assertEquals("jwt-token", authCookie.getName());
        assertEquals("dummyToken", authCookie.getValue());
        assertEquals(24*60*60, authCookie.getMaxAge());
        assertTrue(authCookie.isHttpOnly());
        assertTrue(authCookie.getSecure());
        assertEquals("Strict", authCookie.getAttribute("SameSite"));
        assertEquals("/", authCookie.getPath());
    }

    @Test
    public void createLoginCookieTest() {
        // Act
        Cookie loginCookie = cookieHandlerService.getLoginStatusCookie();

        // Assert
        // Asserting Cookie
        assertEquals("login-status", loginCookie.getName());
        assertEquals("true", loginCookie.getValue());
        assertEquals(24*60*60, loginCookie.getMaxAge());
        assertFalse(loginCookie.isHttpOnly());
        assertTrue(loginCookie.getSecure());
        assertEquals("Strict", loginCookie.getAttribute("SameSite"));
        assertEquals("/", loginCookie.getPath());
    }

    @Test
    public void logoutCookie() {
        // Act
        Cookie logoutCookie = cookieHandlerService.getLogoutStatusCookie();

        // Assert
        // Asserting Cookie
        assertEquals("login-status", logoutCookie.getName());
        assertNull(logoutCookie.getValue());
        assertEquals(0, logoutCookie.getMaxAge());
    }

    @Test
    public void deleteAuthCookie() {
        // Act
        Cookie deleteAuthCookie = cookieHandlerService.getDeleteAuthCookie();

        // Assert
        // Asserting Cookie
        assertEquals("jwt-token", deleteAuthCookie.getName());
        assertNull(deleteAuthCookie.getValue());
        assertEquals(0, deleteAuthCookie.getMaxAge());
    }
}
