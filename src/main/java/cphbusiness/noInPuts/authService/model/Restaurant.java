package cphbusiness.noInPuts.authService.model;

import jakarta.persistence.*;

import java.util.List;

@Table(name = "restaurants")
@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RestaurantEmployee> employees;

    public Restaurant() {
    }

    public Restaurant(Long id, String name, List<RestaurantEmployee> employees) {
        this.id = id;
        this.name = name;
        this.employees = employees;
    }

    public Restaurant(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RestaurantEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<RestaurantEmployee> employees) {
        this.employees = employees;
    }
}
