package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private static final By NAV_CHALLENGES    = By.cssSelector("a[href*='challenges'], a[href*='challenge']");
    private static final By USER_MENU         = By.cssSelector(".user-menu, .navbar-user, #user-dropdown");
    private static final By LOGOUT_LINK       = By.cssSelector("a[href*='logout'], a[href*='signout']");
    private static final By WELCOME_MESSAGE   = By.cssSelector(".welcome, .dashboard-header, h1");

    public boolean isLoggedIn() {
        return isDisplayed(USER_MENU) || isDisplayed(LOGOUT_LINK);
    }

    public String getWelcomeText() {
        return getText(WELCOME_MESSAGE);
    }

    public ChallengePage navigateToChallenges() {
        log.info("Navigating to Challenges");
        click(NAV_CHALLENGES);
        return new ChallengePage();
    }

    public MyChallengesPage navigateToMyChallenges() {
        log.info("Navigating to My Challenges");
        click(By.cssSelector("a[href*='my-challenge'], a[href*='mychallenges']"));
        return new MyChallengesPage();
    }

    public void logout() {
        click(USER_MENU);
        click(LOGOUT_LINK);
        log.info("Logged out from homepage");
    }
}
