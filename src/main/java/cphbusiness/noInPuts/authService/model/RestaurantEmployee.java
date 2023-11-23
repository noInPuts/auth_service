package cphbusiness.noInPuts.authService.model;

import jakarta.persistence.*;

@Entity
public class RestaurantEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public RestaurantEmployee() {
    }

    public RestaurantEmployee(String username, String password, Restaurant restaurant) {
        this.restaurant = restaurant;
        this.username = username;
        this.password = password;
    }

    public RestaurantEmployee(Long id, String username, String password, Restaurant restaurant) {
        this.id = id;
        this.restaurant = restaurant;
        this.username = username;
        this.password = password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
