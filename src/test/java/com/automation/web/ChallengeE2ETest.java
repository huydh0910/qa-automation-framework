package com.automation.web;

import com.automation.base.BaseTest;
import com.automation.pages.web.ChallengePage;
import com.automation.pages.web.HomePage;
import com.automation.pages.web.LoginPage;
import com.automation.pages.web.MyChallengesPage;
import com.automation.utils.TestDataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChallengeE2ETest extends BaseTest {

    @Test(description = "E2E-WEB-001 | Create a challenge and verify it appears in My Challenges")
    public void createChallengeAndVerifyInMyChallenge() {
        String challengeTitle = TestDataGenerator.uniqueChallengeName();

        // Step 1 — Login
        HomePage homePage = new LoginPage()
                .open(getBaseUrl())
                .loginAs(config.get("web.username"), config.get("web.password"));
        Assert.assertTrue(homePage.isLoggedIn(), "Login failed — home page not shown");

        // Step 2 — Navigate to Challenges → Create Challenge
        ChallengePage challengePage = homePage.navigateToChallenges()
                .clickCreateChallenge();

        // Step 3 — Fill and submit the challenge form
        MyChallengesPage myChallengesPage = challengePage
                .fillTitle(challengeTitle)
                .selectCategory("Binary")
                .selectDifficulty("Easy")
                .fillDescription("Automated test challenge: " + challengeTitle)
                .fillFlag("FLAG{automation_" + System.currentTimeMillis() + "}")
                .fillPoints("10")
                .submitChallenge();

        // Step 4 — Verify challenge appears in My Challenges
        Assert.assertTrue(myChallengesPage.isChallengeDisplayed(challengeTitle),
                "Created challenge '" + challengeTitle + "' was not found in My Challenges");

        // Step 5 — Logout
        homePage.logout();
    }

    @Test(description = "E2E-WEB-002 | Negative: submit challenge form with empty title shows validation error")
    public void createChallengeWithEmptyTitleShowsError() {
        // Login and navigate to create challenge form
        new LoginPage()
                .open(getBaseUrl())
                .loginAs(config.get("web.username"), config.get("web.password"));

        ChallengePage challengePage = new HomePage()
                .navigateToChallenges()
                .clickCreateChallenge();

        // Fill all required fields except title, then submit
        challengePage
                .fillDescription("Challenge with no title")
                .fillFlag("FLAG{no_title_test}")
                .submitChallenge();

        // Verify validation error is shown and we have NOT left the form page
        Assert.assertTrue(challengePage.isValidationErrorDisplayed(),
                "Validation error should be displayed when title is missing");
    }
}
