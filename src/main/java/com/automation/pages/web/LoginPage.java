package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    // -- Locators ----------------------------------------------------------
    private static final By USERNAME_INPUT  = By.id("username");
    private static final By PASSWORD_INPUT  = By.id("password");
    private static final By LOGIN_BUTTON    = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE   = By.cssSelector(".alert-danger, .error-message");
    private static final By LOGOUT_LINK     = By.cssSelector("a[href*='logout']");

    // -- Actions -----------------------------------------------------------

    public LoginPage open(String baseUrl) {
        navigateTo(baseUrl + "/user/login");
        log.info("Opened login page");
        return this;
    }

    /**
     * Reusable login action shared across all web test cases.
     */
    public HomePage loginAs(String username, String password) {
        log.info("Logging in as: {}", username);
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
        return new HomePage();
    }

    public LoginPage attemptLogin(String username, String password) {
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
        return this;
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public void logout() {
        click(LOGOUT_LINK);
        log.info("Logged out");
    }
}
