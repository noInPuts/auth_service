package cphbusiness.noInPuts.authService.service;

import cphbusiness.noInPuts.authService.dto.AdminDTO;
import cphbusiness.noInPuts.authService.exception.UserDoesNotExistException;
import cphbusiness.noInPuts.authService.exception.WrongCredentialsException;
import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
    }

    @Override
    public AdminDTO login(AdminDTO adminDTO) throws WrongCredentialsException, UserDoesNotExistException {

        // Getting the admin user from the database
        Optional<Admin> optionalAdminUser = adminRepository.findByUsername(adminDTO.getUsername());

        // If the admin user exists, check if the password matches
        if (optionalAdminUser.isPresent()) {

            // Getting the admin user from the optional
            Admin adminUser = optionalAdminUser.get();

            // If the password matches, return the admin user
            if (argon2PasswordEncoder.matches(adminDTO.getPassword(), adminUser.getPassword())) {
                return new AdminDTO(adminUser.getId(), adminUser.getUsername(), null);
            } else {
                throw new WrongCredentialsException("Wrong password");
            }
        } else {
            throw new UserDoesNotExistException("User does not exist");
        }
    }
}
