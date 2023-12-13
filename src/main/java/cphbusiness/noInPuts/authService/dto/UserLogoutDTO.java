package cphbusiness.noInPuts.authService.dto;

import jakarta.servlet.http.Cookie;

public class UserLogoutDTO {
    private Cookie jwtCookie;
    private Cookie logoutCookie;

    public UserLogoutDTO(Cookie jwtCookie, Cookie logoutCookie) {
        this.jwtCookie = jwtCookie;
        this.logoutCookie = logoutCookie;
    }

    public Cookie getJwtCookie() {
        return jwtCookie;
    }

    public void setJwtCookie(Cookie jwtCookie) {
        this.jwtCookie = jwtCookie;
    }

    public Cookie getLogoutCookie() {
        return logoutCookie;
    }

    public void setLogoutCookie(Cookie logoutCookie) {
        this.logoutCookie = logoutCookie;
    }
}
