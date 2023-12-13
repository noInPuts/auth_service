package cphbusiness.noInPuts.authService.dto;

import jakarta.servlet.http.Cookie;

public class UserLoginDTO {
    private UserDTO user;
    private Cookie jwtCookie;

    private Cookie loginCookie;

    public UserLoginDTO(Cookie jwtCookie, Cookie loginCookie, UserDTO user) {
        this.user = user;
        this.jwtCookie = jwtCookie;
        this.loginCookie = loginCookie;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Cookie getJwtCookie() {
        return jwtCookie;
    }

    public void setJwtCookie(Cookie jwtCookie) {
        this.jwtCookie = jwtCookie;
    }

    public Cookie getLoginCookie() {
        return loginCookie;
    }

    public void setLoginCookie(Cookie loginCookie) {
        this.loginCookie = loginCookie;
    }
}
