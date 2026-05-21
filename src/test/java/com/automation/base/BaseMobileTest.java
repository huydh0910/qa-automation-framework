package com.automation.base;

import com.automation.config.ConfigManager;
import com.automation.drivers.MobileDriverFactory;
import com.automation.utils.ReportManager;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseMobileTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        ReportManager.getReport();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        config = ConfigManager.getInstance();
        String platform = System.getProperty("platform", "android");

        MobileDriverFactory.initMobileDriver(platform);

        String testName = result.getMethod().getMethodName();
        String testDesc = result.getMethod().getDescription();
        ReportManager.createTest(testName, testDesc != null ? testDesc : "");
        log.info("=== START MOBILE TEST: {} | platform: {} ===", testName, platform);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        var test = ReportManager.getTest();
        if (test != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                test.fail(result.getThrowable());
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                test.pass("Mobile test passed");
            } else {
                test.log(Status.SKIP, "Skipped");
            }
        }
        MobileDriverFactory.quitMobileDriver();
        log.info("=== END MOBILE TEST: {} ===", result.getMethod().getMethodName());
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        ReportManager.flush();
    }
}
