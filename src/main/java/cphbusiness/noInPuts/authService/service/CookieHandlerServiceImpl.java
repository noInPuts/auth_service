package cphbusiness.noInPuts.authService.service;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class CookieHandlerServiceImpl implements CookieHandlerService {
    @Override
    public Cookie getAuthCookie(String jwtToken) {
        Cookie cookie = new Cookie("jwt-token", jwtToken);

        return setCookieAttributes(cookie);
    }

    @Override
    public Cookie getLoginStatusCookie() {
        Cookie cookie = new Cookie("login-status", "true");

        return setCookieAttributes(cookie);
    }

    @Override
    public Cookie getLogoutStatusCookie() {
        // Create cookie, that deletes the login-status cookie
        Cookie deleteLoginStatus = new Cookie("login-status", null);
        deleteLoginStatus.setMaxAge(0);
        deleteLoginStatus.setPath("/");

        return deleteLoginStatus;
    }

    @Override
    public Cookie getDeleteAuthCookie() {
        // Create cookie, that deletes the jwt-token cookie
        Cookie deleteJwtCookie = new Cookie("jwt-token", null);
        deleteJwtCookie.setMaxAge(0);
        deleteJwtCookie.setPath("/");

        return deleteJwtCookie;
    }

    private Cookie setCookieAttributes(Cookie cookie) {
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Strict");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);

        return cookie;
    }
}
