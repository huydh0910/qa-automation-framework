# =============================================================================
# FEATURE: API — Authentication (Reqres)
# Application : https://reqres.in/api
# Layer       : REST API
# Endpoint    : POST /login
# Framework   : Java / RestAssured + TestNG  |  Playwright request fixture
# Automation  : com.automation.api.LoginApiTest  |  tests/api/login.api.spec.ts
# =============================================================================

@api @auth
Feature: API — User Authentication
  As an API consumer of Reqres
  I want to authenticate via POST /login
  So that I can receive a Bearer token for subsequent API requests

  # ---------------------------------------------------------------------------
  # TC-API-001 | Positive | P1
  # ---------------------------------------------------------------------------
  @positive @P1 @TC-API-001
  Scenario: Valid credentials return a Bearer token
    Given the API base URL is "https://reqres.in/api"
    When the client sends a POST request to "/login" with valid email and password
    Then the response status code should be 200
    And the response body should contain a non-empty "token" field

  # ---------------------------------------------------------------------------
  # TC-API-002 | Positive | P1
  # ---------------------------------------------------------------------------
  @positive @P1 @TC-API-002
  Scenario: Login response body matches the expected schema
    Given the API base URL is "https://reqres.in/api"
    When the client sends a POST request to "/login" with valid email and password
    Then the response status code should be 200
    And the response body should contain a "token" field of type string
    And the response body should not contain an "error" field

  # ---------------------------------------------------------------------------
  # TC-API-003 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-API-003
  Scenario: Missing password field returns 400 with error message
    Given the API base URL is "https://reqres.in/api"
    When the client sends a POST request to "/login" with valid email and empty password
    Then the response status code should be 400
    And the response body should contain the error "Missing password"

  # ---------------------------------------------------------------------------
  # TC-API-004 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-API-004
  Scenario: Missing email field returns 400 with error message
    Given the API base URL is "https://reqres.in/api"
    When the client sends a POST request to "/login" with empty email and valid password
    Then the response status code should be 400
    And the response body should contain a non-empty "error" field

  # ---------------------------------------------------------------------------
  # TC-API-005 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-API-005
  Scenario: Unregistered email returns 400
    Given the API base URL is "https://reqres.in/api"
    When the client sends a POST request to "/login" with unregistered email "ghost@reqres.in" and any password
    Then the response status code should be 400
    And the response body should contain a non-empty "error" field
