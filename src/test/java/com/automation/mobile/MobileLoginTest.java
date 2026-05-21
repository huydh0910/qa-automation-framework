package com.automation.mobile;

import com.automation.base.BaseMobileTest;
import com.automation.pages.mobile.MobileLoginPage;
import com.automation.pages.mobile.MobilePortfolioPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MobileLoginTest extends BaseMobileTest {

    @Test(description = "MOB-LOGIN-001 | Positive: valid credentials log in and show portfolio")
    public void loginWithValidCredentialsShowsPortfolio() {
        MobilePortfolioPage portfolioPage = new MobileLoginPage()
                .loginWith(config.get("mobile.email"), config.get("mobile.password"));

        Assert.assertTrue(portfolioPage.isPortfolioHeaderVisible(),
                "Portfolio header should be visible after successful login");
    }

    @Test(description = "MOB-LOGIN-002 | Negative: invalid password shows error message")
    public void loginWithInvalidPasswordShowsError() {
        MobileLoginPage loginPage = new MobileLoginPage()
                .attemptLogin(config.get("mobile.email"), "WrongPass@999");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should appear after invalid login");
    }

    @Test(description = "MOB-LOGIN-003 | Negative: empty credentials show error")
    public void loginWithEmptyCredentialsShowsError() {
        MobileLoginPage loginPage = new MobileLoginPage()
                .attemptLogin("", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should appear when credentials are empty");
    }
}
