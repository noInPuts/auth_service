package cphbusiness.noInPuts.authService.steps;

import cphbusiness.noInPuts.authService.CucumberIntegrationTest;
import cphbusiness.noInPuts.authService.model.User;
import cphbusiness.noInPuts.authService.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.Cookie;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import io.cucumber.datatable.DataTable;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class userLoginStepDefinition extends CucumberIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private String username;
    private Long id;
    private String password = null;
    private String jwtToken;
    private MvcResult result;
    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(16, 32, 1, 128 * 1024, 5);

    @Given("I want to login, onto the {string} account with {string} as the password")
    public void i_want_to_login_onto_the_test_user(String username, String password) {
        User user = new User(username, argon2PasswordEncoder.encode(password));
        userRepository.save(user);
    }

    @When("I make a login POST request to {string} with the following body:")
    public void post_request_to_login_endpoint(String endpoint, DataTable dataTable) throws Exception {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        MvcResult result = this.mockMvc.perform(post(endpoint).content("{ \"username\": \"" + dataList.get(0).get("username") + "\", \"password\": \"" + dataList.get(0).get("password") +"\" }").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Cookie cookie = result.getResponse().getCookie("jwt-token");

        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONObject userJson = jsonResponse.getJSONObject("user");
        assert cookie != null;
        jwtToken = cookie.getValue();
        username = userJson.getString("username");
        id = userJson.getLong("id");
        if (jsonResponse.has("password")) {
            password = jsonResponse.getString("password");
        }

    }

    @Then("I should receive the user object and JWT:")
    public void receiving_the_object_and_jwt(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

        assertEquals(username, dataList.get(0).get("username"));
        assertEquals(id, Long.parseLong(dataList.get(0).get("id")));
        assertNotNull(jwtToken);
        assertNull(password);
    }

    @Given("I want to make login request, with the following credentials {string} {string}")
    public void i_want_to_login_onto_the_test_user_account_with_pAssword_1_as_the_password(String username, String password) {
        User user = new User(username, argon2PasswordEncoder.encode(password));
        userRepository.save(user);
    }

    @When("I make a login POST request with wrong password to {string} with the following body:")
    public void i_make_a_login_post_request_to_with_the_following_body(String endpoint, DataTable dataTable) throws Exception {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);

         result = this.mockMvc.perform(post(endpoint).content("{ \"username\": \"" + dataList.get(0).get("username") + "\", \"password\": \"" + dataList.get(0).get("password") +"\" }").contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Then("I should receive a 401 status code")
    public void i_should_receive_a_401_status_code() throws Exception {
        assertEquals(401, result.getResponse().getStatus());
    }
}
