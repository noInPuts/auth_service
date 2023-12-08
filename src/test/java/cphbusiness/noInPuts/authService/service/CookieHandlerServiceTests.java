package cphbusiness.noInPuts.authService.service;

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
        Cookie authCookie = cookieHandlerService.getAuthCookie("dummyToken");

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
        Cookie loginCookie = cookieHandlerService.getLoginStatusCookie();

        // Asserting Cookie
        assertEquals("login-status", loginCookie.getName());
        assertEquals("true", loginCookie.getValue());
        assertEquals(24*60*60, loginCookie.getMaxAge());
        assertTrue(loginCookie.isHttpOnly());
        assertTrue(loginCookie.getSecure());
        assertEquals("Strict", loginCookie.getAttribute("SameSite"));
        assertEquals("/", loginCookie.getPath());
    }

    @Test
    public void logoutCookie() {
        Cookie logoutCookie = cookieHandlerService.getLogoutStatusCookie();

        // Asserting Cookie
        assertEquals("login-status", logoutCookie.getName());
        assertNull(logoutCookie.getValue());
        assertEquals(0, logoutCookie.getMaxAge());
    }

    @Test
    public void deleteAuthCookie() {
        Cookie deleteAuthCookie = cookieHandlerService.getDeleteAuthCookie();

        // Asserting Cookie
        assertEquals("jwt-token", deleteAuthCookie.getName());
        assertNull(deleteAuthCookie.getValue());
        assertEquals(0, deleteAuthCookie.getMaxAge());
    }
}
