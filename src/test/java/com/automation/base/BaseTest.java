package com.automation.base;

import com.automation.config.ConfigManager;
import com.automation.drivers.DriverManager;
import com.automation.utils.ReportManager;
import com.automation.utils.ScreenshotUtil;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

public class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        ReportManager.getReport();
        log.info("Test suite started");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        config = ConfigManager.getInstance();

        DriverManager.initDriver();
        WebDriver driver = DriverManager.getDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getInt("implicit.wait")));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getInt("page.load.timeout")));
        driver.manage().window().maximize();

        String testName   = result.getMethod().getMethodName();
        String testDesc   = result.getMethod().getDescription();
        ReportManager.createTest(testName, testDesc != null ? testDesc : "");
        log.info("=== START: {} ===", testName);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        ExtentTest test = ReportManager.getTest();
        String testName = result.getMethod().getMethodName();

        if (test != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                String screenshot = ScreenshotUtil.capture(testName);
                test.fail(result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                test.pass("Test passed");
            } else {
                test.log(Status.SKIP, "Test skipped");
            }
        }

        DriverManager.quitDriver();
        log.info("=== END: {} | status: {} ===", testName, result.getStatus());
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        ReportManager.flush();
        log.info("Test suite finished — report generated at reports/index.html");
    }

    protected String getBaseUrl() {
        return config.get("base.url");
    }
}
