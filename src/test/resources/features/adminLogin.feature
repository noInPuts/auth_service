Feature: Login feature for admin
  Scenario: Admin login with valid credentials
    Given I have an admin account in the DB with username "admin" and password "PassWord!1"
    When I make a admin login POST request to "/api/admin/login" with the following body
      """
      {
        "username": "admin",
        "password": "PassWord!1"
      }
      """
    Then The response status code should be 200 and i should receive a token and my admin details:
      | username | id |
      | admin    | 1  |