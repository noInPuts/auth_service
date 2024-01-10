package cphbusiness.noInPuts.authService.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDTO {
    private long id;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    public LoginUserDTO(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public LoginUserDTO() {
    }

    public LoginUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
