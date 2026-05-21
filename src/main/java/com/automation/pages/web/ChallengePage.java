package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class ChallengePage extends BasePage {

    private static final By CREATE_CHALLENGE_BTN = By.cssSelector("a[href*='create'], button.create-challenge");
    private static final By CHALLENGE_TITLE      = By.id("title");
    private static final By CHALLENGE_CATEGORY   = By.id("category");
    private static final By CHALLENGE_DIFFICULTY = By.id("difficulty");
    private static final By CHALLENGE_DESCRIPTION = By.id("description");
    private static final By CHALLENGE_FLAG        = By.id("flag");
    private static final By CHALLENGE_POINTS      = By.id("points");
    private static final By SUBMIT_BUTTON         = By.cssSelector("button[type='submit'], input[type='submit']");
    private static final By SUCCESS_MESSAGE       = By.cssSelector(".alert-success, .success-message");
    private static final By VALIDATION_ERROR      = By.cssSelector(".alert-danger, .field-error, .error");

    public ChallengePage clickCreateChallenge() {
        click(CREATE_CHALLENGE_BTN);
        log.info("Clicked Create Challenge button");
        return this;
    }

    public ChallengePage fillTitle(String title) {
        type(CHALLENGE_TITLE, title);
        return this;
    }

    public ChallengePage selectCategory(String category) {
        click(By.xpath("//select[@id='category']/option[text()='" + category + "']"));
        return this;
    }

    public ChallengePage selectDifficulty(String difficulty) {
        click(By.xpath("//select[@id='difficulty']/option[text()='" + difficulty + "']"));
        return this;
    }

    public ChallengePage fillDescription(String description) {
        type(CHALLENGE_DESCRIPTION, description);
        return this;
    }

    public ChallengePage fillFlag(String flag) {
        type(CHALLENGE_FLAG, flag);
        return this;
    }

    public ChallengePage fillPoints(String points) {
        type(CHALLENGE_POINTS, points);
        return this;
    }

    public MyChallengesPage submitChallenge() {
        log.info("Submitting challenge form");
        click(SUBMIT_BUTTON);
        return new MyChallengesPage();
    }

    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(SUCCESS_MESSAGE);
    }

    public boolean isValidationErrorDisplayed() {
        return isDisplayed(VALIDATION_ERROR);
    }

    public String getValidationErrorText() {
        return getText(VALIDATION_ERROR);
    }
}
