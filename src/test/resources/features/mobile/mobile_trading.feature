# =============================================================================
# FEATURE: Mobile — Trading App Login & Portfolio
# Application : Mobile Trading App
# Layer       : Mobile Native (Appium) | Mobile Browser Emulation (Playwright)
# Framework   : Java / Appium + TestNG  |  Playwright device emulation
# Automation  : com.automation.mobile.MobileLoginTest / MobilePortfolioE2ETest
#               tests/mobile/mobile.e2e.spec.ts
#
# NOTE ON MOBILE SCOPE DIFFERENCE:
#   Java/Appium  → Tests run on a native Android/iOS application (.apk/.ipa)
#   Playwright   → Tests run in a mobile-viewport browser (Pixel 5 / iPhone 12)
#   Both frameworks verify the same user flows from a business perspective.
# =============================================================================

@mobile
Feature: Mobile — Trading App Login and Portfolio
  As a mobile user of the trading application
  I want to log in and view my investment portfolio
  So that I can monitor my investments on the go

  # ---------------------------------------------------------------------------
  # TC-MOB-001 | Positive | P1
  # ---------------------------------------------------------------------------
  @login @positive @P1 @TC-MOB-001
  Scenario: Successful login with valid credentials navigates to portfolio
    Given the mobile trading app is launched
    When the user enters a valid email address
    And the user enters the correct password
    And the user taps the "Login" button
    Then the user should be navigated to the portfolio or home screen
    And no error message should be displayed

  # ---------------------------------------------------------------------------
  # TC-MOB-002 | Negative | P1
  # ---------------------------------------------------------------------------
  @login @negative @P1 @TC-MOB-002
  Scenario: Login fails with an incorrect password
    Given the mobile trading app is launched
    When the user enters a valid email address
    And the user enters an incorrect password "WrongPass@999"
    And the user taps the "Login" button
    Then an error message should be displayed on the login screen
    And the user should remain on the login screen

  # ---------------------------------------------------------------------------
  # TC-MOB-003 | Negative | P2
  # ---------------------------------------------------------------------------
  @login @negative @P2 @TC-MOB-003
  Scenario: Login fails when both credentials are empty
    Given the mobile trading app is launched
    When the user leaves the email and password fields empty
    And the user taps the "Login" button
    Then a validation message should be displayed
    And no authentication request should be sent

  # ---------------------------------------------------------------------------
  # TC-MOB-004 | E2E | P1
  # ---------------------------------------------------------------------------
  @portfolio @e2e @positive @P1 @TC-MOB-004
  Scenario: Full mobile E2E flow — Login, view portfolio, logout
    Given the mobile trading app is launched
    When the user logs in with valid credentials
    Then the portfolio screen should be displayed
    When the user navigates to the Portfolio section
    Then the total portfolio value should be visible and non-empty
    And the holdings list should contain at least one item
    When the user logs out of the application
    Then the user should be returned to the login screen

  # ---------------------------------------------------------------------------
  # TC-MOB-005 | Positive | P2
  # ---------------------------------------------------------------------------
  @portfolio @positive @P2 @TC-MOB-005
  Scenario: Portfolio screen displays holdings after successful login
    Given the user is logged in to the mobile trading app
    When the user navigates to the Portfolio tab
    Then the portfolio holdings list should be visible
    And the holdings list should contain at least one holding item
    And each holding should display the asset name and value

  # ---------------------------------------------------------------------------
  # TC-MOB-006 | Reliability | P3
  # ---------------------------------------------------------------------------
  @portfolio @reliability @P3 @TC-MOB-006
  Scenario: Pull-to-refresh reloads portfolio data
    Given the user is on the Portfolio screen
    When the user performs a pull-to-refresh gesture
    Then a loading indicator should appear
    And the portfolio data should reload successfully
    And no error message should be displayed after refresh

  # ---------------------------------------------------------------------------
  # TC-MOB-007 | Security | P2
  # ---------------------------------------------------------------------------
  @login @security @P2 @TC-MOB-007
  Scenario: Account is locked after multiple consecutive failed login attempts
    Given the mobile trading app is launched
    When the user enters a valid email and wrong password 5 times consecutively
    Then the account should be locked or a CAPTCHA challenge should appear
    And an informative lockout message should be displayed

  # ---------------------------------------------------------------------------
  # TC-MOB-008 | UI | P3
  # ---------------------------------------------------------------------------
  @ui @P3 @TC-MOB-008
  Scenario: Mobile viewport matches the expected device dimensions
    Given the app is running on a mobile device or emulator
    Then the screen width should be at most 430 pixels
    And the screen height should be at least 600 pixels
