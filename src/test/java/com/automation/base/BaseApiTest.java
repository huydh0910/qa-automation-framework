package com.automation.base;

import com.automation.api.endpoints.AuthEndpoint;
import com.automation.config.ConfigManager;
import com.automation.utils.ReportManager;
import com.automation.utils.TokenManager;
import com.aventstack.extentreports.Status;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseApiTest {

    protected final Logger log = LogManager.getLogger(getClass());
    protected ConfigManager config;
    protected AuthEndpoint authEndpoint;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        ReportManager.getReport();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        config       = ConfigManager.getInstance();
        authEndpoint = new AuthEndpoint();

        String testName = result.getMethod().getMethodName();
        String testDesc = result.getMethod().getDescription();
        ReportManager.createTest(testName, testDesc != null ? testDesc : "");
        log.info("=== START API TEST: {} ===", testName);

        // Obtain a fresh token per test method
        String token = authEndpoint.getToken(
                config.get("api.email"),
                config.get("api.password")
        );
        TokenManager.setToken(token);
        log.info("Token obtained for test thread");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        var test = ReportManager.getTest();
        if (test != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                test.fail(result.getThrowable());
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                test.pass("API test passed");
            } else {
                test.log(Status.SKIP, "Test skipped");
            }
        }
        TokenManager.clearToken();
        log.info("=== END API TEST: {} | status: {} ===",
                result.getMethod().getMethodName(), result.getStatus());
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        ReportManager.flush();
    }
}
