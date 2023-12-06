package cphbusiness.noInPuts.authService.facade;

import cphbusiness.noInPuts.authService.service.AdminService;
import cphbusiness.noInPuts.authService.service.JwtService;
import cphbusiness.noInPuts.authService.service.RestaurantEmployeeService;
import cphbusiness.noInPuts.authService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceFacadeImpl implements ServiceFacade {

    private final AdminService adminService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RestaurantEmployeeService restaurantEmployeeService;

    @Autowired
    public ServiceFacadeImpl(AdminService adminService, UserService userService, JwtService jwtService, RestaurantEmployeeService restaurantEmployeeService) {
        this.adminService = adminService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.restaurantEmployeeService = restaurantEmployeeService;
    }


}
