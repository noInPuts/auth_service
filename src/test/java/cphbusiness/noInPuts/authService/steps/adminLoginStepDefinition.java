package cphbusiness.noInPuts.authService.steps;

import cphbusiness.noInPuts.authService.CucumberIntegrationTest;
import cphbusiness.noInPuts.authService.model.Admin;
import cphbusiness.noInPuts.authService.repository.AdminRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class adminLoginStepDefinition extends CucumberIntegrationTest {

    // TODO: More tests

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    private MvcResult result;

    @Given("I have an admin account in the DB with username {string} and password {string}")
    public void i_have_an_admin_account_in_the_db_with_username_and_password(String username, String password) {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);
        adminRepository.save(new Admin(username, argon2PasswordEncoder.encode(password)));
    }

    @When("I make a admin login POST request to {string} with the following body")
    public void admin_login_post_request(String endpoint, String content) throws Exception {
        result = this.mockMvc.perform(post(endpoint).content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("The response status code should be {int} and i should receive a token and my admin details:")
    public void the_reponse_status_should_be(int statusCode, DataTable dataTable) throws UnsupportedEncodingException {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        MockHttpServletResponse response = result.getResponse();
        String responseBody = response.getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONObject adminUserJson = jsonResponse.getJSONObject("user");

        assertEquals(statusCode, response.getStatus());
        assertNotNull(response.getCookie("jwt-token"));
        assertEquals(dataList.get(0).get("username"), adminUserJson.getString("username"));
        assertEquals(Long.parseLong(dataList.get(0).get("id")), adminUserJson.getLong("id"));
    }
}
