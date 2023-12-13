package cphbusiness.noInPuts.authService.facade;

import cphbusiness.noInPuts.authService.dto.*;
import cphbusiness.noInPuts.authService.exception.*;
import cphbusiness.noInPuts.authService.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceFacadeImpl implements ServiceFacade {

    private final AdminService adminService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RestaurantEmployeeService restaurantEmployeeService;
    private final CookieHandlerService cookieHandlerService;
    private final SpamCheckServiceImpl spamCheckServiceImpl;

    @Autowired
    public ServiceFacadeImpl(SpamCheckServiceImpl spamCheckServiceImpl, AdminService adminService, UserService userService, JwtService jwtService, RestaurantEmployeeService restaurantEmployeeService, CookieHandlerService cookieHandlerService) {
        this.adminService = adminService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.restaurantEmployeeService = restaurantEmployeeService;
        this.cookieHandlerService = cookieHandlerService;
        this.spamCheckServiceImpl = spamCheckServiceImpl;
    }


    @Override
    public AdminLoginDTO adminLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException {
        // Create adminDTO
        AdminDTO adminUser;

        // Verify Login and get the admin entity
        adminUser = adminService.login(username, password);

        // Creates a JWT token and adds it to a cookie
        String jwtToken = jwtService.tokenGenerator(adminUser.getId(), adminUser.getUsername(), "admin");
        Cookie cookie = cookieHandlerService.getAuthCookie(jwtToken);

        return new AdminLoginDTO(cookie, adminUser);
    }

    @Override
    public RestaurantEmployeeLoginDTO restaurantEmployeeLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException {
        // Create RestaurantEmployeeDTO
        RestaurantEmployeeDTO restaurantEmployee;

        // Verify Login and get the restaurant employee entity
        restaurantEmployee = restaurantEmployeeService.login(username, password);

        // Creates a JWT token and adds it to a cookie
        String jwtToken = jwtService.tokenGenerator(restaurantEmployee.getId(), restaurantEmployee.getUsername(), "employee");
        Cookie cookie = cookieHandlerService.getAuthCookie(jwtToken);

        return new RestaurantEmployeeLoginDTO(cookie, restaurantEmployee);
    }

    @Override
    public UserCreateDTO userCreateAccount(String username, String password) throws UserAlreadyExistsException, WeakPasswordException {

        // Creates user and returns userDTO
        UserDTO userDTO = userService.createUser(username, password);

        // Create a JWT Cookie
        String jwtToken = jwtService.tokenGenerator(userDTO.getId(), userDTO.getUsername(), "user");
        Cookie jwtCookie = cookieHandlerService.getAuthCookie(jwtToken);

        // Create a Login Cookie
        Cookie loginCookie = cookieHandlerService.getLoginStatusCookie();

        return new UserCreateDTO(jwtCookie, loginCookie, userDTO);
    }

    @Override
    public UserLoginDTO userLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException {

        // Verify Login and get the user entity
        UserDTO userDTO = userService.login(username, password);

        // Create a JWT Cookie
        String jwtToken = jwtService.tokenGenerator(userDTO.getId(), userDTO.getUsername(), "user");
        Cookie jwtCookie = cookieHandlerService.getAuthCookie(jwtToken);

        // Create a Login Cookie
        Cookie loginCookie = cookieHandlerService.getLoginStatusCookie();

        return new UserLoginDTO(jwtCookie, loginCookie, userDTO);
    }

    @Override
    public UserLogoutDTO userLogout(String token) throws AlreadyLoggedOutException {
        // Log the user out
        jwtService.logout(token);

        return new UserLogoutDTO(cookieHandlerService.getDeleteAuthCookie(), cookieHandlerService.getLogoutStatusCookie());
    }

    @Override
    public boolean isClientBlocked(HttpServletRequest request) {
        String ip = spamCheckServiceImpl.getIp(request);
        return spamCheckServiceImpl.isBlocked(ip);
    }
}
