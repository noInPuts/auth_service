package cphbusiness.noInPuts.authService.repository;

import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantEmployeeRepository extends CrudRepository<RestaurantEmployee, Long> {
    // TODO: Fully implement this method
    List<RestaurantEmployee> findAllByRestaurantId(Long id);
    Optional<RestaurantEmployee> findByUsername(String username);
}
