package cphbusiness.noInPuts.authService.service;


import cphbusiness.noInPuts.authService.exception.UserAlreadyExistsException;
import cphbusiness.noInPuts.authService.exception.WeakPasswordException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataInitializerService {

    // This class persist data to the database (Only for demonstration purposes)
    private final UserService userService;

    @Autowired
    public DataInitializerService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        // Persist data to the database
        try {
            userService.createUser("user", "Password1!", "email@email.com");
        } catch (UserAlreadyExistsException | WeakPasswordException e) {
            System.out.println("Admin already exists");
            e.printStackTrace();
        }
    }
}
