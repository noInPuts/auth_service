package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.model.SpamCheck;
import cphbusiness.noInPuts.authService.repository.SpamCheckRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class SpamCheckService {

    private final SpamCheckRepository spamCheckRepository;

    private static final int MAX_ATTEMPT = 5;

    @Autowired
    public SpamCheckService(SpamCheckRepository spamCheckRepository, HttpServletRequest request) {
        this.spamCheckRepository = spamCheckRepository;
    }

    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public boolean isBlocked(String ip) {
        Date currentDate = new Date(System.currentTimeMillis() - 60000);
        List<SpamCheck> tries = spamCheckRepository.findAllByIpAndTimestampIsGreaterThan(ip, currentDate);
        if(tries.size() >= MAX_ATTEMPT) {
            return true;
        }

        SpamCheck loginRequest = new SpamCheck(ip);
        spamCheckRepository.save(loginRequest);

        return false;
    }
}