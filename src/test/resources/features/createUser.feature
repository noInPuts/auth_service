Feature: Creating a new user account

  Scenario: User account creation
    Given I want to create a user with username as "my_user" and the password as "ThisIsMyPassword!1"
    When I make a POST request to "/api/user/create" with the following body:
      | username | password           |
      | my_user  | ThisIsMyPassword!1 |
    Then a user is created in the database with the following properties:
      | username | id | password           |
      | my_user  | 1  | ThisIsMyPassword!1 |

  Scenario: User account creation with weak password
    Given I want to create a user with username as "my_user" and the weak password as "weak"
    When I make a request to "/api/user/create" with the following body:
      | username | password |
      | my_user  | weak     |
    Then A user is not created in the database with the following properties:
      | username | id | password |
      | my_user  | 1  | weak     |