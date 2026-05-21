# =============================================================================
# FEATURE: Web UI — User Authentication (CTFLearner)
# Application : https://ctflearn.com
# Layer       : Web UI
# Framework   : Java / Selenium + TestNG  |  Playwright (TypeScript)
# Automation  : com.automation.web.LoginTest  |  tests/web/login.spec.ts
# =============================================================================

@web @login
Feature: Web UI — User Authentication
  As a registered user of CTFLearner
  I want to log in to my account
  So that I can access challenges and manage my profile

  Background:
    Given the user navigates to the CTFLearner login page

  # ---------------------------------------------------------------------------
  # TC-WEB-001 | Positive | P1
  # ---------------------------------------------------------------------------
  @positive @P1 @TC-WEB-001
  Scenario: Successful login with valid credentials
    When the user enters a valid username and valid password
    And the user clicks the Login button
    Then the user should be redirected to the home page
    And the navigation bar should display the logged-in user state

  # ---------------------------------------------------------------------------
  # TC-WEB-002 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-WEB-002
  Scenario: Login fails with an incorrect password
    When the user enters a valid username and an incorrect password "WrongPassword@999"
    And the user clicks the Login button
    Then an error message should be displayed on the login page
    And the user should remain on the login page

  # ---------------------------------------------------------------------------
  # TC-WEB-003 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-WEB-003
  Scenario: Login fails when both fields are empty
    When the user leaves the username and password fields empty
    And the user clicks the Login button
    Then a validation message should be displayed for the empty fields
    And the login form should not be submitted

  # ---------------------------------------------------------------------------
  # TC-WEB-004 | Security | P1
  # ---------------------------------------------------------------------------
  @security @P1 @TC-WEB-004
  Scenario: Unauthenticated user cannot access a protected page
    Given the user is not logged in
    When the user navigates directly to the Create Challenge page URL
    Then the user should be redirected to the login page
    And the Create Challenge form should not be accessible
