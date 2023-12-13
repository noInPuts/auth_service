package cphbusiness.noInPuts.authService.unit.facade;

import cphbusiness.noInPuts.authService.dto.*;
import cphbusiness.noInPuts.authService.exception.*;
import cphbusiness.noInPuts.authService.facade.ServiceFacade;
import cphbusiness.noInPuts.authService.service.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ServiceFacadeTests {

    @Autowired
    private ServiceFacade serviceFacade;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CookieHandlerService cookieHandlerService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UserService userService;

    @MockBean
    private RestaurantEmployeeService restaurantEmployeeService;

    @BeforeEach
    public void setup() {
        when(jwtService.tokenGenerator(any(Long.class), any(String.class), any(String.class))).thenReturn("dummyToken");
        when(cookieHandlerService.getAuthCookie(any(String.class))).thenReturn(new Cookie("jwt-token", "dummyCookie"));
    }

    @Test
    public void adminLoginTest() throws UserDoesNotExistException, WrongCredentialsException {
        // Mocking adminService
        when(adminService.login(any(String.class), any(String.class))).thenReturn(new AdminDTO(1L, "admin", null));

        AdminLoginDTO adminLoginDTO = serviceFacade.adminLogin("admin", "Password1!");
        AdminDTO adminDTO = adminLoginDTO.getAdminAccount();
        Cookie cookie = adminLoginDTO.getJwtCookie();

        // Asserting adminDTO
        assertEquals(1, adminDTO.getId());
        assertEquals("admin", adminDTO.getUsername());
        assertNull(adminDTO.getPassword());

        // Asserting Cookie
        assertEquals("jwt-token", cookie.getName());
        assertEquals("dummyCookie", cookie.getValue());
    }

    @Test
    public void restaurantEmployeeLoginTest() throws UserDoesNotExistException, WrongCredentialsException {
        // Mocking restaurantService
        when(restaurantEmployeeService.login(any(String.class), any(String.class))).thenReturn(new RestaurantEmployeeDTO(1L, "employee_user", null, null));

        RestaurantEmployeeLoginDTO restaurantEmployeeLoginDTO = serviceFacade.restaurantEmployeeLogin("employee_user", "Password1!");
        RestaurantEmployeeDTO restaurantEmployeeDTO = restaurantEmployeeLoginDTO.getRestaurantEmployee();
        Cookie cookie = restaurantEmployeeLoginDTO.getJwtCookie();

        // Asserting adminDTO
        assertEquals(1, restaurantEmployeeDTO.getId());
        assertEquals("employee_user", restaurantEmployeeDTO.getUsername());
        assertNull(restaurantEmployeeDTO.getPassword());

        // Asserting Cookie
        assertEquals("jwt-token", cookie.getName());
        assertEquals("dummyCookie", cookie.getValue());
    }

    @Test
    public void createUserAccountTest() throws UserAlreadyExistsException, WeakPasswordException {
        // Mocking the userService
        when(userService.createUser(any(String.class), any(String.class))).thenReturn(new UserDTO(1L, "user"));
        when(cookieHandlerService.getLoginStatusCookie()).thenReturn(new Cookie("login-status", "true"));

        UserCreateDTO userCreateDTO = serviceFacade.userCreateAccount("user", "Password1!");
        Cookie jwtCookie = userCreateDTO.getJwtCookie();
        Cookie loginCookie = userCreateDTO.getLoginCookie();

        // Asserting userDTO
        assertEquals(1, userCreateDTO.getUser().getId());
        assertEquals("user", userCreateDTO.getUser().getUsername());
        assertNull(userCreateDTO.getUser().getPassword());

        // Asserting Cookie
        assertEquals("jwt-token", jwtCookie.getName());
        assertEquals("dummyCookie", jwtCookie.getValue());
        assertEquals("login-status", loginCookie.getName());
        assertEquals("true", loginCookie.getValue());
    }

    @Test
    public void logoutTest() throws AlreadyLoggedOutException {
        // Mocking the cookieHandlerService
        Cookie fakeLogoutCookie = new Cookie("login-status", null);
        fakeLogoutCookie.setMaxAge(0);
        Cookie fakeAuthCookie = new Cookie("jwt-token", null);
        fakeAuthCookie.setMaxAge(0);
        when(cookieHandlerService.getDeleteAuthCookie()).thenReturn(fakeAuthCookie);
        when(cookieHandlerService.getLogoutStatusCookie()).thenReturn(fakeLogoutCookie);

        UserLogoutDTO userLogoutDTO = serviceFacade.userLogout("DummyToken");
        Cookie authCookie = userLogoutDTO.getJwtCookie();
        Cookie logoutCookie = userLogoutDTO.getLogoutCookie();

        // Asserting Cookie
        assertEquals("login-status", logoutCookie.getName());
        assertNull(logoutCookie.getValue());
        assertEquals(0, logoutCookie.getMaxAge());
        assertEquals("jwt-token", authCookie.getName());
        assertNull(authCookie.getValue());
        assertEquals(0, authCookie.getMaxAge());


    }
}
