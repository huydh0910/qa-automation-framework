package com.automation.api;

import com.automation.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiClient {

    protected final RequestSpecification requestSpec;

    protected BaseApiClient() {
        ConfigManager config = ConfigManager.getInstance();
        RestAssured.baseURI = config.get("api.base.url");

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.get("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    protected RequestSpecification withBearerToken(String token) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token);
    }

    protected RequestSpecification baseRequest() {
        return RestAssured.given().spec(requestSpec);
    }
}
