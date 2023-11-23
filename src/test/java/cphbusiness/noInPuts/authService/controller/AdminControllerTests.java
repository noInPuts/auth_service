package cphbusiness.noInPuts.authService.controller;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.service.AdminService;
import cphbusiness.noInPuts.authService.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtService jwtService;

    @Test
    public void loginShouldReturnAdmin() throws Exception {
        when(adminService.login(any(AdminDTO.class))).thenReturn(new AdminDTO(1L, "admin", null));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), any(String.class))).thenReturn("jwtToken");

        this.mockMvc.perform(post("/admin/login").content("{ \"username\": \"admin\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"user\":{\"id\":1,\"username\":\"admin\"}}"))
                .andExpect(cookie().exists("jwt-token"));
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenParsingWrongUserCredentianls() throws Exception {
        when(adminService.login(any(AdminDTO.class))).thenThrow(new WrongCredentialsException("Wrong password."));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");

        this.mockMvc.perform(post("/admin/login").content("{ \"username\": \"admin\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn401UnauthorizedWhenUserDoesNotExists() throws Exception {
        when(adminService.login(any(AdminDTO.class))).thenThrow(new UserDoesNotExistException("User is not found in the db"));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("user"))).thenReturn("dummyToken");

        this.mockMvc.perform(post("/admin/login").content("{ \"username\": \"test_user\", \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginShouldReturn400BadRequestWhenParsingBadRequest() throws Exception {
        mockAdminServiceAndJwtService();

        this.mockMvc.perform(post("/admin/login").content("{ \"password\": \"Password1!\" }").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturn415UnsupportedeMediaTypeWhenParsingInvalidJson() throws Exception {
        mockAdminServiceAndJwtService();

        this.mockMvc.perform(post("/admin/login").content("not json").characterEncoding("UTF-8"))
                .andExpect(status().isUnsupportedMediaType());
    }

    private void mockAdminServiceAndJwtService() throws UserAlreadyExistsException, WrongCredentialsException, WeakPasswordException, UserDoesNotExistException {
        when(adminService.login(any(AdminDTO.class))).thenReturn(new AdminDTO(1L, "admin", null));
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), eq("admin"))).thenReturn("dummyToken");
    }
}
