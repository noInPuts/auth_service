package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;

public interface UserService {
    UserDTO createUser(String username, String password, String email) throws UserAlreadyExistsException, WeakPasswordException;
    UserDTO login(String username, String password) throws WrongCredentialsException, UserDoesNotExistException;
}
