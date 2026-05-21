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

public class MobileLoginPage {

    private final AppiumDriver driver;
    private final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(getClass());

    // Locators — update with actual resource-ids or accessibility labels from the app
    private static final By EMAIL_FIELD    = AppiumBy.accessibilityId("email-input");
    private static final By PASSWORD_FIELD = AppiumBy.accessibilityId("password-input");
    private static final By LOGIN_BUTTON   = AppiumBy.accessibilityId("login-button");
    private static final By ERROR_LABEL    = AppiumBy.accessibilityId("error-message");

    public MobileLoginPage() {
        this.driver = (AppiumDriver) MobileDriverFactory.getMobileDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public MobilePortfolioPage loginWith(String email, String password) {
        log.info("Mobile login with: {}", email);
        waitForVisible(EMAIL_FIELD).sendKeys(email);
        waitForVisible(PASSWORD_FIELD).sendKeys(password);
        waitForClickable(LOGIN_BUTTON).click();
        return new MobilePortfolioPage();
    }

    public MobileLoginPage attemptLogin(String email, String password) {
        waitForVisible(EMAIL_FIELD).sendKeys(email);
        waitForVisible(PASSWORD_FIELD).sendKeys(password);
        waitForClickable(LOGIN_BUTTON).click();
        return this;
    }

    public boolean isErrorDisplayed() {
        try {
            return driver.findElement(ERROR_LABEL).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorText() {
        return waitForVisible(ERROR_LABEL).getText();
    }

    private WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
}
