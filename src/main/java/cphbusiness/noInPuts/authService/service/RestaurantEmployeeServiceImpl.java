package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.RestaurantDTO;
import cphbusiness.noInPuts.authService.dto.RestaurantEmployeeDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.RestaurantEmployee;
import cphbusiness.noInPuts.authService.repository.RestaurantEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantEmployeeServiceImpl implements RestaurantEmployeeService {

    private final RestaurantEmployeeRepository restaurantEmployeeRepository;

    // TODO: make this own class and autowire it?
    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    public RestaurantEmployeeServiceImpl(RestaurantEmployeeRepository restaurantEmployeeRepository) {
        this.restaurantEmployeeRepository = restaurantEmployeeRepository;
        this.argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
    }

    public RestaurantEmployeeDTO login(String username, String password) throws UserDoesNotExistException, WrongCredentialsException {
        // Getting the restaurant employee from the database
        Optional<RestaurantEmployee> restaurantEmployeeOptional = restaurantEmployeeRepository.findByUsername(username);

        // If the restaurant employee exists, check if the password matches
        if(restaurantEmployeeOptional.isPresent()) {
            RestaurantEmployee restaurantEmployee = restaurantEmployeeOptional.get();

            // If the password matches, return the restaurant employee else throw an exception
            if(!argon2PasswordEncoder.matches(password, restaurantEmployee.getPassword())) {
                throw new WrongCredentialsException("Wrong password");
            }

            RestaurantDTO restaurantDTO = new RestaurantDTO(restaurantEmployee.getRestaurant().getId(), restaurantEmployee.getRestaurant().getName(), null);
            return new RestaurantEmployeeDTO(restaurantEmployee.getId(), restaurantEmployee.getUsername(), null, restaurantDTO);
        } else {
            throw new UserDoesNotExistException("User does not exist");
        }
    }
}
