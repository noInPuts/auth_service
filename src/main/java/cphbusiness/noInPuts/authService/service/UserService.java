package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;

public interface UserService {
    UserDTO createUser(UserDTO userDTO) throws UserAlreadyExistsException, WeakPasswordException;
    UserDTO login(UserDTO userDTO) throws WrongCredentialsException, UserDoesNotExistException;
}
