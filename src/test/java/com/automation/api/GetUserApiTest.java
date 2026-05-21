package com.automation.api;

import com.automation.api.endpoints.UserEndpoint;
import com.automation.api.models.GetUserResponse;
import com.automation.base.BaseApiTest;
import com.automation.utils.TokenManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class GetUserApiTest extends BaseApiTest {

    private UserEndpoint userEndpoint;

    @BeforeClass
    public void init() {
        userEndpoint = new UserEndpoint();
    }

    @Test(description = "API-GET-001 | Positive: retrieve existing user returns 200 with full data")
    public void getExistingUserReturns200() {
        Response response = userEndpoint.getUser(2, TokenManager.getToken());

        response.then()
                .statusCode(200)
                .body("data.id",         equalTo(2))
                .body("data.email",      notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name",  notNullValue())
                .body("data.avatar",     notNullValue())
                .body("support.url",     notNullValue());
    }

    @Test(description = "API-GET-002 | Positive: response body maps to GetUserResponse model")
    public void getExistingUserDeserializesCorrectly() {
        GetUserResponse body = userEndpoint.getUserData(2, TokenManager.getToken());

        Assert.assertNotNull(body.getData());
        Assert.assertEquals(body.getData().getId(), 2);
        Assert.assertTrue(body.getData().getEmail().contains("@"),
                "Email should be a valid format");
        Assert.assertNotNull(body.getSupport());
    }

    @Test(description = "API-GET-003 | Positive: retrieve a different valid user (id=1)")
    public void getAnotherValidUserReturns200() {
        userEndpoint.getUser(1, TokenManager.getToken())
                .then()
                .statusCode(200)
                .body("data.id", equalTo(1));
    }

    @Test(description = "API-GET-004 | Negative: non-existent user id returns 404")
    public void getNonExistentUserReturns404() {
        userEndpoint.getUser(9999, TokenManager.getToken())
                .then()
                .statusCode(404);
    }

    @Test(description = "API-GET-005 | Negative: user id zero returns 404 or 400")
    public void getUserIdZeroReturnsError() {
        Response response = userEndpoint.getUser(0, TokenManager.getToken());
        int status = response.statusCode();
        Assert.assertTrue(status == 404 || status == 400,
                "Status should be 4xx for user id 0, got: " + status);
    }
}
