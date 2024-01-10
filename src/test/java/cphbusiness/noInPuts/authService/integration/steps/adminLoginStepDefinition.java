package cphbusiness.noInPuts.authService.integration.steps;

import cphbusiness.noInPuts.authService.integration.CucumberIntegrationTest;
import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import cphbusiness.noInPuts.authService.service.RabbitMessagePublisher;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class adminLoginStepDefinition extends CucumberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    private MvcResult result;

    @Given("I have an admin account in the DB with username {string} and password {string}")
    public void i_have_an_admin_account_in_the_db_with_username_and_password(String username, String password) {
        // Arrange
        // Creating an admin user and saving it to the database
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
        adminRepository.save(new Admin(username, argon2PasswordEncoder.encode(password)));
    }

    @When("I make a admin login POST request to {string} with the following body")
    public void admin_login_post_request(String endpoint, String content) throws Exception {
        // Act
        // Making a POST request to the admin login endpoint with the admin user credentials
        result = this.mockMvc.perform(post(endpoint).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("The response status code should be {int} and i should receive a token and my admin details:")
    public void the_reponse_status_should_be(int statusCode, DataTable dataTable) throws UnsupportedEncodingException {
        // Assert
        // Asserting that the response status code is 200 and that the response body contains a token and the admin details
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        MockHttpServletResponse response = result.getResponse();
        String responseBody = response.getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseBody);

        assertEquals(statusCode, response.getStatus());
        assertNotNull(response.getCookie("jwt-token"));
        assertEquals(dataList.get(0).get("username"), jsonResponse.getString("username"));
        assertEquals(Long.parseLong(dataList.get(0).get("id")), jsonResponse.getLong("id"));
    }
}
