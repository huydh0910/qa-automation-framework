package com.automation.api.endpoints;

import com.automation.api.BaseApiClient;
import com.automation.api.models.LoginRequest;
import com.automation.api.models.LoginResponse;
import io.restassured.response.Response;

public class AuthEndpoint extends BaseApiClient {

    private static final String LOGIN_PATH = "/login";

    public Response login(String email, String password) {
        LoginRequest body = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        return baseRequest()
                .body(body)
                .post(LOGIN_PATH);
    }

    public LoginResponse loginAndGetResponse(String email, String password) {
        return login(email, password)
                .then()
                .statusCode(200)
                .extract()
                .as(LoginResponse.class);
    }

    /**
     * Convenience method — returns the token directly.
     * Throws AssertionError if login fails.
     */
    public String getToken(String email, String password) {
        return loginAndGetResponse(email, password).getToken();
    }
}
