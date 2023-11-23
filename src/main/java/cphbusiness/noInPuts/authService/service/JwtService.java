package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.exception.AlreadyLoggedOutException;

public interface JwtService {
    String tokenGenerator(Long id, String username, String role);
    void logout(String token) throws AlreadyLoggedOutException;
}
