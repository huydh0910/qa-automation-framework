package com.automation.api.endpoints;

import com.automation.api.BaseApiClient;
import com.automation.api.models.GetUserResponse;
import com.automation.api.models.UpdateUserRequest;
import com.automation.api.models.UpdateUserResponse;
import io.restassured.response.Response;

public class UserEndpoint extends BaseApiClient {

    private static final String USERS_PATH = "/users/{id}";

    // -- GET ---------------------------------------------------------------

    public Response getUser(int userId, String token) {
        return withBearerToken(token)
                .pathParam("id", userId)
                .get(USERS_PATH);
    }

    public GetUserResponse getUserData(int userId, String token) {
        return getUser(userId, token)
                .then()
                .statusCode(200)
                .extract()
                .as(GetUserResponse.class);
    }

    // -- PUT ---------------------------------------------------------------

    public Response updateUser(int userId, String name, String job, String token) {
        UpdateUserRequest body = UpdateUserRequest.builder()
                .name(name)
                .job(job)
                .build();

        return withBearerToken(token)
                .pathParam("id", userId)
                .body(body)
                .put(USERS_PATH);
    }

    public UpdateUserResponse updateUserAndGetResponse(int userId, String name, String job, String token) {
        return updateUser(userId, name, job, token)
                .then()
                .statusCode(200)
                .extract()
                .as(UpdateUserResponse.class);
    }

    // -- PATCH -------------------------------------------------------------

    public Response patchUser(int userId, String name, String job, String token) {
        UpdateUserRequest body = UpdateUserRequest.builder()
                .name(name)
                .job(job)
                .build();

        return withBearerToken(token)
                .pathParam("id", userId)
                .body(body)
                .patch(USERS_PATH);
    }
}
