# =============================================================================
# FEATURE: Web UI — Challenge Management (CTFLearner)
# Application : https://ctflearn.com
# Layer       : Web UI — E2E
# Framework   : Java / Selenium + TestNG  |  Playwright (TypeScript)
# Automation  : com.automation.web.ChallengeE2ETest  |  tests/web/challenge.e2e.spec.ts
# =============================================================================

@web @challenge @e2e
Feature: Web UI — Challenge Management
  As a logged-in user of CTFLearner
  I want to create a challenge
  So that I can view my created challenges in My Challenge section

  Background:
    Given the user is logged in to CTFLearner with valid credentials

  # ---------------------------------------------------------------------------
  # TC-WEB-005 | E2E | P1
  # ---------------------------------------------------------------------------
  @e2e @positive @P1 @TC-WEB-005
  Scenario: Successfully create a challenge and verify it appears in My Challenges
    Given the user navigates to the Challenges section
    And the user clicks the "Create Challenge" button
    When the user fills in the challenge title with a unique name
    And the user selects "Binary" as the category
    And the user selects "Easy" as the difficulty level
    And the user fills in the challenge description
    And the user fills in the challenge flag
    And the user fills in the points value "10"
    And the user submits the challenge form
    Then the challenge creation should succeed
    And the created challenge should appear in the "My Challenge" section
    When the user logs out
    Then the user should be returned to the login or home page

  # ---------------------------------------------------------------------------
  # TC-WEB-006 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-WEB-006
  Scenario: Challenge form submission blocked when title is empty
    Given the user navigates to the Challenges section
    And the user clicks the "Create Challenge" button
    When the user leaves the title field empty
    And the user fills in the challenge description
    And the user fills in the challenge flag
    And the user submits the challenge form
    Then a validation error should be displayed for the title field
    And the user should remain on the Create Challenge form page

  # ---------------------------------------------------------------------------
  # TC-WEB-007 | Negative | P2
  # ---------------------------------------------------------------------------
  @negative @P2 @TC-WEB-007
  Scenario: Challenge form submission blocked when all required fields are empty
    Given the user navigates to the Challenges section
    And the user clicks the "Create Challenge" button
    When the user submits the challenge form without filling any field
    Then validation errors should be displayed for all required fields
    And no network request should be sent to create the challenge

  # ---------------------------------------------------------------------------
  # TC-WEB-008 | Boundary | P3
  # ---------------------------------------------------------------------------
  @boundary @P3 @TC-WEB-008
  Scenario Outline: Challenge title at character length boundaries
    Given the user navigates to the Create Challenge form
    When the user fills in the title field with <length> characters
    And the user fills all other required fields
    And the user submits the challenge form
    Then the form should <result>

    Examples:
      | length | result                              |
      | 1      | accept the title and submit         |
      | 255    | accept the title and submit         |
      | 256    | show a validation error or truncate |
