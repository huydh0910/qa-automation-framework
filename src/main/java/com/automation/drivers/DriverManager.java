package com.automation.drivers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class DriverManager {

    private static final Logger log = LogManager.getLogger(DriverManager.class);

    // ThreadLocal ensures each parallel test thread has its own isolated driver
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            throw new IllegalStateException("WebDriver not initialised. Call initDriver() first.");
        }
        return driverThreadLocal.get();
    }

    public static void initDriver() {
        String browser  = System.getProperty("browser", "chrome");
        boolean remote  = Boolean.parseBoolean(System.getProperty("remote", "false"));
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver driver = remote
                ? WebDriverFactory.createRemoteDriver(browser)
                : WebDriverFactory.createLocalDriver(browser, headless);

        driverThreadLocal.set(driver);
        log.info("WebDriver initialised — browser: {}, remote: {}, headless: {}", browser, remote, headless);
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("WebDriver quit and removed from thread-local");
        }
    }
}
