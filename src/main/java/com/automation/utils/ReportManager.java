package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportManager {

    private static final Logger log = LogManager.getLogger(ReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ReportManager() {}

    public static synchronized ExtentReports getReport() {
        if (extentReports == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reports/index.html");
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("QA Automation Report");
            spark.config().setReportName("Test Execution Report");

            extentReports = new ExtentReports();
            extentReports.attachReporter(spark);
            extentReports.setSystemInfo("Framework", "Selenium + TestNG + RestAssured");
            extentReports.setSystemInfo("Environment", System.getProperty("env", "qa"));
            log.info("ExtentReports initialised");
        }
        return extentReports;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getReport().createTest(testName, description);
        testThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
