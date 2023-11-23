package cphbusiness.noInPuts.authService.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminDTO {
    private Long id;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

    public AdminDTO() {
    }

    public AdminDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AdminDTO(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
