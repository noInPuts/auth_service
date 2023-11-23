package cphbusiness.noInPuts.authService.dto;

import java.util.List;

public class RestaurantDTO {

    private Long id;
    private String name;
    private List<RestaurantEmployeeDTO> employees;

    public RestaurantDTO(Long id, String name, List<RestaurantEmployeeDTO> employees) {
        this.id = id;
        this.name = name;
        this.employees = employees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RestaurantEmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<RestaurantEmployeeDTO> employees) {
        this.employees = employees;
    }
}
