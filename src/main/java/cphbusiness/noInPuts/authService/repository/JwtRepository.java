package cphbusiness.noInPuts.authService.repository;

import cphbusiness.noInPuts.authService.model.Jwt;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<Jwt, String> {
}
