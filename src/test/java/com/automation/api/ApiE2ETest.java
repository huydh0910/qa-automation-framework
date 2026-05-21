package com.automation.api;

import com.automation.api.endpoints.AuthEndpoint;
import com.automation.api.endpoints.UserEndpoint;
import com.automation.api.models.GetUserResponse;
import com.automation.api.models.UpdateUserResponse;
import com.automation.base.BaseApiTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiE2ETest extends BaseApiTest {

    @Test(description = "E2E-API-001 | Full flow: Login → Get User → Update User")
    public void fullApiE2EFlow() {
        AuthEndpoint authEndpoint = new AuthEndpoint();
        UserEndpoint userEndpoint  = new UserEndpoint();

        // Step 1 — Login and retrieve token
        String token = authEndpoint.getToken(
                config.get("api.email"),
                config.get("api.password")
        );
        Assert.assertNotNull(token, "Step 1 failed — token was null");
        log.info("E2E Step 1 passed: token obtained");

        // Step 2 — Get user details
        GetUserResponse userResponse = userEndpoint.getUserData(2, token);
        Assert.assertNotNull(userResponse.getData(), "Step 2 failed — user data was null");
        Assert.assertEquals(userResponse.getData().getId(), 2,
                "Step 2 failed — user id mismatch");
        log.info("E2E Step 2 passed: user data retrieved — {}",
                userResponse.getData().getFirstName());

        // Step 3 — Update user details
        String updatedName = "Janet E2E " + System.currentTimeMillis();
        UpdateUserResponse updateResponse = userEndpoint.updateUserAndGetResponse(
                2, updatedName, "Automation Lead", token
        );
        Assert.assertEquals(updateResponse.getName(), updatedName,
                "Step 3 failed — updated name mismatch");
        Assert.assertNotNull(updateResponse.getUpdatedAt(),
                "Step 3 failed — updatedAt was null");
        log.info("E2E Step 3 passed: user updated — name={}, updatedAt={}",
                updateResponse.getName(), updateResponse.getUpdatedAt());
    }
}
