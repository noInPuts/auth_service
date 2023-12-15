package cphbusiness.noInPuts.authService.integration.steps;

import cphbusiness.noInPuts.authService.integration.CucumberIntegrationTest;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class createUserStepDefinition extends CucumberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private MvcResult result;

    @Given("I want to create a user with username as \"my_user\" and the password as \"ThisIsMyPassword!1\"")
    public void i_want_to_create_a_user_with_username_as_my_user_and_the_password_as_ThisIsMyPassword_1() {
    }

    @When("I make a POST request to {string} with the following body:")
    public void i_make_a_post_request_to_with_the_following_body_parameters(String endpoint, DataTable dataTable) throws Exception {
        // Act
        // Converting the DataTable to a List of Maps
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        // Making a POST request to the createUser endpoint with the user credentials
        this.mockMvc.perform(post(endpoint).content("{ \"username\": \"" + dataList.get(0).get("username") + "\", \"password\": \"" + dataList.get(0).get("password") + "\"}").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isCreated());

    }

    @Then("a user is created in the database with the following properties:")
    public void a_user_is_created_in_the_database_with_the_following_properties(io.cucumber.datatable.DataTable dataTable) {
        // Assert
        // Converting the DataTable to a List of Maps
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        // Asserting that the user is created in the database with the correct properties
        Optional<User> userOptional = userRepository.findByUsername(dataList.get(0).get("username"));
        assertTrue(userOptional.isPresent());

        User user = userOptional.get();
        assertEquals(dataList.get(0).get("username"), user.getUsername());
        assertEquals(Long.parseLong(dataList.get(0).get("id")), user.getId());
    }

    @Given("I want to create a user with username as \"my_user\" and the weak password as \"weak\"")
    public void i_want_to_create_a_user_with_username_as_my_user_and_the_weak_password_as_weak() {
    }

    @When("I make a request to {string} with the following body:")
    public void i_make_a_request_to_with_the_following_body(String endpoint, DataTable dataTable) throws Exception {
        // Act
        // Converting the DataTable to a List of Maps
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        // Making a POST request to the createUser endpoint with the user credentials
        result = this.mockMvc.perform(post(endpoint).content("{ \"username\": \"" + dataList.get(0).get("username") + "\", \"password\": \"" + dataList.get(0).get("password") + "\"}").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andReturn();
    }

    @Then("A user is not created in the database with the following properties:")
    public void a_user_is_not_created_in_the_database_with_the_following_properties(io.cucumber.datatable.DataTable dataTable) {
        // Assert
        // Converting the DataTable to a List of Maps
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        // Asserting that the user is not created in the database
        Optional<User> userOptional = userRepository.findByUsername(dataList.get(0).get("username"));
        assertTrue(userOptional.isEmpty());
        assertEquals(400, result.getResponse().getStatus());
    }
}
