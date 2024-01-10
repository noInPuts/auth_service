package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.UserDTO;
import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
    }

    public UserDTO createUser(String username, String password, String email) throws UserAlreadyExistsException, WeakPasswordException {

        // Check if user is already registered
        Optional<User> checkIfUserExist = userRepository.findByUsername(username);
        if (checkIfUserExist.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Check if password is strong enough (1 Capital letter, 1 number, 1 special character)
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new WeakPasswordException("Password is not strong enough");
        }

        // Save the user to the database
        User user = userRepository.save(new User(username, argon2PasswordEncoder.encode(password), email));

        return new UserDTO(user.getId(), user.getUsername());
    }

    public UserDTO login(String username, String password) throws WrongCredentialsException, UserDoesNotExistException {

        // Check if user exists
        Optional<User> user = userRepository.findByUsername(username);

        // If the user exists, check if the password matches
        if (user.isPresent()) {

            // If the password matches, return the user, else throw an exception
            if (!argon2PasswordEncoder.matches(password, user.get().getPassword())) {
                throw new WrongCredentialsException("Wrong password.");
            }
        } else {
            throw new UserDoesNotExistException("User not found.");
        }

        return new UserDTO(user.get().getId(), user.get().getUsername());
    }
}
