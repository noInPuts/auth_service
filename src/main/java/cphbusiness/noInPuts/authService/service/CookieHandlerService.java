package cphbusiness.noInPuts.authService.service;

import jakarta.servlet.http.Cookie;

public interface CookieHandlerService {
    Cookie getAuthCookie(String jwtToken);
    Cookie getLoginStatusCookie();
    Cookie getLogoutStatusCookie();
    Cookie getDeleteAuthCookie();
}
