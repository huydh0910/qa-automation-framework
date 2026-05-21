package com.automation.utils;

import com.automation.drivers.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "screenshots/";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {}

    public static String capture(String testName) {
        try {
            byte[] bytes = ((TakesScreenshot) DriverManager.getDriver())
                    .getScreenshotAs(OutputType.BYTES);

            String fileName = testName + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path path = Paths.get(SCREENSHOT_DIR, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);

            log.info("Screenshot saved: {}", path);
            return path.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return "";
        }
    }
}
