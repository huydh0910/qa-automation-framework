package com.automation.web;

import com.automation.base.BaseTest;
import com.automation.pages.web.HomePage;
import com.automation.pages.web.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "TC-US01-001 | Positive: valid credentials log in successfully")
    public void loginWithValidCredentials() {
        HomePage homePage = new LoginPage()
                .open(getBaseUrl())
                .loginAs(config.get("web.username"), config.get("web.password"));

        Assert.assertTrue(homePage.isLoggedIn(),
                "User should be on home page after successful login");
    }

    @Test(description = "TC-US01-002 | Negative: wrong password displays error message")
    public void loginWithInvalidPasswordShowsError() {
        LoginPage loginPage = new LoginPage()
                .open(getBaseUrl())
                .attemptLogin(config.get("web.username"), "WrongPassword@999");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should appear after invalid login attempt");
    }

    @Test(description = "TC-US01-003 | Negative: empty fields display validation message")
    public void loginWithEmptyFieldsShowsValidation() {
        LoginPage loginPage = new LoginPage()
                .open(getBaseUrl())
                .attemptLogin("", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Validation message should appear when both fields are empty");
    }
}
