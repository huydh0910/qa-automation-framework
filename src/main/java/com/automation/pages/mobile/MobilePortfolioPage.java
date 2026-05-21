package com.automation.pages.mobile;

import com.automation.drivers.MobileDriverFactory;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MobilePortfolioPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(getClass());

    // Locators — update with actual resource-ids or accessibility labels from the app
    private static final By PORTFOLIO_TAB      = AppiumBy.accessibilityId("portfolio-tab");
    private static final By PORTFOLIO_HEADER   = AppiumBy.accessibilityId("portfolio-header");
    private static final By TOTAL_VALUE        = AppiumBy.accessibilityId("total-portfolio-value");
    private static final By HOLDINGS_LIST      = AppiumBy.accessibilityId("holdings-list-item");
    private static final By LOGOUT_BUTTON      = AppiumBy.accessibilityId("logout-button");
    private static final By USER_MENU          = AppiumBy.accessibilityId("user-menu");

    public MobilePortfolioPage() {
        this.driver = (AppiumDriver) MobileDriverFactory.getMobileDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public MobilePortfolioPage navigateToPortfolio() {
        log.info("Tapping Portfolio tab");
        waitForClickable(PORTFOLIO_TAB).click();
        return this;
    }

    public boolean isPortfolioHeaderVisible() {
        return isDisplayed(PORTFOLIO_HEADER);
    }

    public String getTotalPortfolioValue() {
        return waitForVisible(TOTAL_VALUE).getText();
    }

    public List<String> getHoldingNames() {
        return driver.findElements(HOLDINGS_LIST)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getHoldingsCount() {
        return driver.findElements(HOLDINGS_LIST).size();
    }

    public void logout() {
        waitForClickable(USER_MENU).click();
        waitForClickable(LOGOUT_BUTTON).click();
        log.info("Logged out from mobile app");
    }

    private WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
