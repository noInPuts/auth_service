package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.exception.AlreadyLoggedOutException;
import cphbusiness.noInPuts.authService.model.Jwt;
import cphbusiness.noInPuts.authService.repository.JwtRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;


@Service
public class JwtServiceImpl implements JwtService {

    private final JwtRepository jwtRepository;

    @Value("${jwt.secret}")
    private String pKey;

    @Autowired
    public JwtServiceImpl(JwtRepository jwtRepository) {
        this.jwtRepository = jwtRepository;
    }

    public String tokenGenerator(Long id, String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(pKey.getBytes());

        return Jwts.builder()
                .header()
                .add("id", id)
                .add("username", username)
                .add("role", role)
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                // Expires after 2 days of issue
                .expiration(new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public void logout(String jwtToken) throws AlreadyLoggedOutException {
        Optional<Jwt> jwt = jwtRepository.findById(jwtToken);

        if(jwt.isPresent()) {
            throw new AlreadyLoggedOutException("User is already logged out");
        } else {
            jwtRepository.save(new Jwt(jwtToken));
        }
    }
}
