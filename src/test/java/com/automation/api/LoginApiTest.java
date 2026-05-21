package com.automation.api;

import com.automation.api.endpoints.AuthEndpoint;
import com.automation.api.models.LoginResponse;
import com.automation.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class LoginApiTest extends BaseApiTest {

    private AuthEndpoint authEndpoint;

    @BeforeClass
    public void init() {
        authEndpoint = new AuthEndpoint();
    }

    @Test(description = "API-LOGIN-001 | Positive: valid credentials return a token")
    public void loginWithValidCredentialsReturnsToken() {
        Response response = authEndpoint.login(
                config.get("api.email"),
                config.get("api.password")
        );

        response.then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(emptyString()));

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
        log.info("Login successful — token: {}", token);
    }

    @Test(description = "API-LOGIN-002 | Positive: login response maps to LoginResponse model")
    public void loginResponseDeserializesCorrectly() {
        LoginResponse response = authEndpoint.loginAndGetResponse(
                config.get("api.email"),
                config.get("api.password")
        );

        Assert.assertNotNull(response.getToken(), "Token should be present in LoginResponse");
        Assert.assertNull(response.getError(), "Error field should be null on successful login");
    }

    @Test(description = "API-LOGIN-003 | Negative: missing password returns 400 with error")
    public void loginWithMissingPasswordReturns400() {
        authEndpoint.login(config.get("api.email"), "")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test(description = "API-LOGIN-004 | Negative: missing email returns 400 with error")
    public void loginWithMissingEmailReturns400() {
        authEndpoint.login("", config.get("api.password"))
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing email or username"));
    }

    @Test(description = "API-LOGIN-005 | Negative: unregistered email returns 400")
    public void loginWithUnregisteredEmailReturns400() {
        authEndpoint.login("ghost@reqres.in", "password123")
                .then()
                .statusCode(400)
                .body("error", notNullValue());
    }
}
