# =============================================================================
# FEATURE: API — User Management (Reqres)
# Application : https://reqres.in/api
# Layer       : REST API
# Endpoints   : GET /users/{id}  |  PUT /users/{id}  |  PATCH /users/{id}
# Framework   : Java / RestAssured + TestNG  |  Playwright request fixture
# Automation  : com.automation.api.GetUserApiTest / UpdateUserApiTest / ApiE2ETest
#               tests/api/getUser.api.spec.ts / updateUser.api.spec.ts / api.e2e.spec.ts
# =============================================================================

@api @user
Feature: API — User Management
  As an authenticated API consumer of Reqres
  I want to retrieve and update user information
  So that I can view and modify user profiles via the API

  Background:
    Given the client has authenticated via POST "/login" and holds a valid Bearer token

  # ===========================================================================
  # GET /users/{id}
  # ===========================================================================

  # ---------------------------------------------------------------------------
  # TC-API-006 | Positive | P1
  # ---------------------------------------------------------------------------
  @get @positive @P1 @TC-API-006
  Scenario: Retrieve an existing user by ID returns full user data
    When the client sends a GET request to "/users/2" with the Bearer token
    Then the response status code should be 200
    And the response body should contain a "data" object with fields:
      | field      | type   |
      | id         | number |
      | email      | string |
      | first_name | string |
      | last_name  | string |
      | avatar     | string |
    And the response body should contain a "support" object

  # ---------------------------------------------------------------------------
  # TC-API-007 | Positive | P1
  # ---------------------------------------------------------------------------
  @get @positive @P1 @TC-API-007
  Scenario: Retrieved user data id matches the requested user id
    When the client sends a GET request to "/users/2" with the Bearer token
    Then the response status code should be 200
    And the "data.id" field in the response should equal 2
    And the "data.email" field should contain the "@" character

  # ---------------------------------------------------------------------------
  # TC-API-008 | Positive | P2
  # ---------------------------------------------------------------------------
  @get @positive @P2 @TC-API-008
  Scenario: Retrieve a different valid user returns correct data
    When the client sends a GET request to "/users/1" with the Bearer token
    Then the response status code should be 200
    And the "data.id" field in the response should equal 1

  # ---------------------------------------------------------------------------
  # TC-API-009 | Negative | P1
  # ---------------------------------------------------------------------------
  @get @negative @P1 @TC-API-009
  Scenario: GET request for a non-existent user returns 404
    When the client sends a GET request to "/users/9999" with the Bearer token
    Then the response status code should be 404

  # ---------------------------------------------------------------------------
  # TC-API-010 | Negative | P2
  # ---------------------------------------------------------------------------
  @get @negative @P2 @TC-API-010
  Scenario: GET request with user id 0 returns a client error
    When the client sends a GET request to "/users/0" with the Bearer token
    Then the response status code should be between 400 and 499

  # ===========================================================================
  # PUT /users/{id}
  # ===========================================================================

  # ---------------------------------------------------------------------------
  # TC-API-011 | Positive | P1
  # ---------------------------------------------------------------------------
  @put @positive @P1 @TC-API-011
  Scenario: PUT request successfully updates user name and job
    When the client sends a PUT request to "/users/2" with the Bearer token and body:
      | name | Janet Updated |
      | job  | Senior QA     |
    Then the response status code should be 200
    And the response body "name" field should equal "Janet Updated"
    And the response body "job" field should equal "Senior QA"
    And the response body should contain a non-empty "updatedAt" timestamp

  # ---------------------------------------------------------------------------
  # TC-API-012 | Positive | P2
  # ---------------------------------------------------------------------------
  @put @positive @P2 @TC-API-012
  Scenario: PUT response body matches the expected update schema
    When the client sends a PUT request to "/users/2" with the Bearer token and any valid body
    Then the response status code should be 200
    And the response body should contain fields "name", "job", and "updatedAt"
    And the "updatedAt" field should be a valid ISO 8601 timestamp

  # ===========================================================================
  # PATCH /users/{id}
  # ===========================================================================

  # ---------------------------------------------------------------------------
  # TC-API-013 | Positive | P2
  # ---------------------------------------------------------------------------
  @patch @positive @P2 @TC-API-013
  Scenario: PATCH request partially updates only the provided fields
    When the client sends a PATCH request to "/users/2" with the Bearer token and body:
      | job | Principal QA |
    Then the response status code should be 200
    And the response body "job" field should equal "Principal QA"
    And the response body should contain a non-empty "updatedAt" timestamp

  # ===========================================================================
  # Negative cases
  # ===========================================================================

  # ---------------------------------------------------------------------------
  # TC-API-014 | Negative | P2
  # ---------------------------------------------------------------------------
  @put @negative @P2 @TC-API-014
  Scenario: PUT with an empty request body does not cause a server error
    When the client sends a PUT request to "/users/2" with the Bearer token and an empty body
    Then the response status code should not be 500

  # ---------------------------------------------------------------------------
  # TC-API-015 | Negative | P3
  # ---------------------------------------------------------------------------
  @put @negative @P3 @TC-API-015
  Scenario: PUT to a non-existent user does not cause a server error
    When the client sends a PUT request to "/users/9999" with the Bearer token and a valid body
    Then the response status code should not be 500

  # ===========================================================================
  # E2E API Flow
  # ===========================================================================

  # ---------------------------------------------------------------------------
  # TC-API-016 | E2E | P1
  # ---------------------------------------------------------------------------
  @e2e @positive @P1 @TC-API-016
  Scenario: Full API E2E flow — Login, Get User, Update User
    Given the client sends a POST request to "/login" with valid credentials
    And the response contains a valid Bearer token
    When the client sends a GET request to "/users/2" with the Bearer token
    Then the response status code should be 200
    And the response should contain user data for id 2
    When the client sends a PUT request to "/users/2" with an updated name and job
    Then the response status code should be 200
    And the updated "name" and "job" fields should reflect the request body
    And the "updatedAt" timestamp should be present
