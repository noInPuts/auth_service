package cphbusiness.noInPuts.authService.service;

import jakarta.servlet.http.HttpServletRequest;

public interface SpamCheckService {
    String getIp(HttpServletRequest request);
    boolean isBlocked(String ip);
}
