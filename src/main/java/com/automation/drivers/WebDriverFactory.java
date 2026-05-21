package com.automation.drivers;

import com.automation.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class WebDriverFactory {

    private static final Logger log = LogManager.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {}

    public static WebDriver createLocalDriver(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case "chrome"  -> createChrome(headless);
            case "firefox" -> createFirefox(headless);
            case "edge"    -> createEdge(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    public static WebDriver createRemoteDriver(String browser) {
        ConfigManager config = ConfigManager.getInstance();
        String hubUrl = config.get("browserstack.url");
        try {
            return switch (browser.toLowerCase()) {
                case "chrome"  -> new RemoteWebDriver(new URL(hubUrl), buildChromeOptions(false));
                case "firefox" -> new RemoteWebDriver(new URL(hubUrl), buildFirefoxOptions(false));
                case "edge"    -> new RemoteWebDriver(new URL(hubUrl), buildEdgeOptions(false));
                default -> throw new IllegalArgumentException("Unsupported remote browser: " + browser);
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack hub URL: " + hubUrl, e);
        }
    }

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(buildChromeOptions(headless));
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(buildFirefoxOptions(headless));
    }

    private static WebDriver createEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver(buildEdgeOptions(headless));
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        if (headless) options.addArguments("--headless=new");
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("--headless");
        return options;
    }

    private static EdgeOptions buildEdgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        return options;
    }
}
