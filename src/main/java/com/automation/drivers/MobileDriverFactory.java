package com.automation.drivers;

import com.automation.config.ConfigManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creates Appium drivers for Android and iOS.
 * Requires Appium Server running at appium.server.url (default: http://localhost:4723).
 */
public class MobileDriverFactory {

    private static final Logger log = LogManager.getLogger(MobileDriverFactory.class);
    private static final ThreadLocal<WebDriver> mobileDriverThreadLocal = new ThreadLocal<>();

    private MobileDriverFactory() {}

    public static WebDriver getMobileDriver() {
        if (mobileDriverThreadLocal.get() == null) {
            throw new IllegalStateException("Mobile driver not initialised. Call initMobileDriver() first.");
        }
        return mobileDriverThreadLocal.get();
    }

    public static void initMobileDriver(String platform) {
        WebDriver driver = platform.equalsIgnoreCase("ios")
                ? createIOSDriver()
                : createAndroidDriver();
        mobileDriverThreadLocal.set(driver);
        log.info("Mobile driver initialised for platform: {}", platform);
    }

    public static void quitMobileDriver() {
        WebDriver driver = mobileDriverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            mobileDriverThreadLocal.remove();
            log.info("Mobile driver quit and removed from thread-local");
        }
    }

    private static AndroidDriver createAndroidDriver() {
        ConfigManager config = ConfigManager.getInstance();
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp(config.get("mobile.app.path"))
                .setDeviceName(config.get("android.device.name", "emulator-5554"))
                .setPlatformVersion(config.get("android.platform.version", "12.0"))
                .setAutomationName("UiAutomator2")
                .setNoReset(false);

        try {
            return new AndroidDriver(new URL(config.get("appium.server.url", "http://localhost:4723")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    private static IOSDriver createIOSDriver() {
        ConfigManager config = ConfigManager.getInstance();
        XCUITestOptions options = new XCUITestOptions()
                .setApp(config.get("mobile.app.path"))
                .setDeviceName(config.get("ios.device.name", "iPhone 14"))
                .setPlatformVersion(config.get("ios.platform.version", "16.0"))
                .setAutomationName("XCUITest");

        try {
            return new IOSDriver(new URL(config.get("appium.server.url", "http://localhost:4723")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }
}
