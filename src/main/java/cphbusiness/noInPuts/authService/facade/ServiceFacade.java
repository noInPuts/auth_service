package cphbusiness.noInPuts.authService.facade;

import cphbusiness.noInPuts.authService.dto.*;
import cphbusiness.noInPuts.authService.exception.*;
import jakarta.servlet.http.HttpServletRequest;

public interface ServiceFacade {
    AdminLoginDTO adminLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException;
    RestaurantEmployeeLoginDTO restaurantEmployeeLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException;
    UserCreateDTO userCreateAccount(String username, String password) throws UserAlreadyExistsException, WeakPasswordException;
    UserLoginDTO userLogin(String username, String password) throws WrongCredentialsException, UserDoesNotExistException;
    UserLogoutDTO userLogout(String token) throws AlreadyLoggedOutException;
    boolean isClientBlocked(HttpServletRequest request);
}
