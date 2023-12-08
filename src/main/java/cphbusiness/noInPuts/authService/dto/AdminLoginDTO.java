package cphbusiness.noInPuts.authService.dto;

import jakarta.servlet.http.Cookie;

public class AdminLoginDTO {
    private Cookie jwtCookie;
    private AdminDTO adminAccount;

    public AdminLoginDTO(Cookie jwtCookie, AdminDTO adminAccount) {
        this.jwtCookie = jwtCookie;
        this.adminAccount = adminAccount;
    }

    public Cookie getJwtCookie() {
        return jwtCookie;
    }

    public void setJwtCookie(Cookie jwtCookie) {
        this.jwtCookie = jwtCookie;
    }

    public AdminDTO getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(AdminDTO adminAccount) {
        this.adminAccount = adminAccount;
    }
}
