package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;

public interface AdminService {
    AdminDTO login(String username, String password) throws WrongCredentialsException, UserDoesNotExistException;
}
