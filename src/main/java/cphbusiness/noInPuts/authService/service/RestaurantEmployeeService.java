package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;

public interface RestaurantEmployeeService {
    RestaurantEmployeeDTO login(String username, String password) throws UserDoesNotExistException, WrongCredentialsException;
}
