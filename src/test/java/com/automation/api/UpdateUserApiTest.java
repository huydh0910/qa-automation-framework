package com.automation.api;

import com.automation.api.endpoints.UserEndpoint;
import com.automation.api.models.UpdateUserResponse;
import com.automation.base.BaseApiTest;
import com.automation.utils.TokenManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class UpdateUserApiTest extends BaseApiTest {

    private UserEndpoint userEndpoint;

    @BeforeClass
    public void init() {
        userEndpoint = new UserEndpoint();
    }

    @Test(description = "API-PUT-001 | Positive: PUT update user returns 200 with updated fields")
    public void putUpdateUserReturns200WithUpdatedData() {
        Response response = userEndpoint.updateUser(
                2, "Janet QA", "Senior QA Engineer", TokenManager.getToken()
        );

        response.then()
                .statusCode(200)
                .body("name",      equalTo("Janet QA"))
                .body("job",       equalTo("Senior QA Engineer"))
                .body("updatedAt", notNullValue());
    }

    @Test(description = "API-PUT-002 | Positive: PUT response maps to UpdateUserResponse model")
    public void putResponseDeserializesCorrectly() {
        UpdateUserResponse body = userEndpoint.updateUserAndGetResponse(
                2, "Test User", "QA Engineer", TokenManager.getToken()
        );

        Assert.assertEquals(body.getName(), "Test User");
        Assert.assertEquals(body.getJob(), "QA Engineer");
        Assert.assertNotNull(body.getUpdatedAt(), "updatedAt should not be null");
    }

    @Test(description = "API-PATCH-003 | Positive: PATCH partial update returns 200")
    public void patchPartialUpdateReturns200() {
        userEndpoint.patchUser(2, null, "Principal QA", TokenManager.getToken())
                .then()
                .statusCode(200)
                .body("job",       equalTo("Principal QA"))
                .body("updatedAt", notNullValue());
    }

    @Test(description = "API-PUT-004 | Negative: PUT with empty body returns 200 (Reqres accepts empty body)")
    public void putEmptyBodyReturnsNoServerError() {
        Response response = userEndpoint.updateUser(2, "", "", TokenManager.getToken());
        int status = response.statusCode();
        Assert.assertNotEquals(status, 500,
                "Server should not return 500 on empty-body PUT");
    }

    @Test(description = "API-PUT-005 | Negative: PUT to non-existent user — Reqres returns 200 (mock API behaviour)")
    public void putNonExistentUserBehaviourIsDocumented() {
        // Reqres returns 200 for any PUT — documenting actual behaviour
        Response response = userEndpoint.updateUser(
                9999, "Ghost", "Nobody", TokenManager.getToken()
        );
        Assert.assertNotEquals(response.statusCode(), 500,
                "No internal server error should occur");
        log.info("PUT /users/9999 returned: {}", response.statusCode());
    }
}
