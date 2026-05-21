package com.automation.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TestDataGenerator {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TestDataGenerator() {}

    public static String uniqueChallengeName() {
        return "QA_Challenge_" + LocalDateTime.now().format(TS);
    }

    public static String uniqueEmail() {
        return "qa_" + LocalDateTime.now().format(TS) + "@automation.test";
    }

    public static String uniqueString(String prefix) {
        return prefix + "_" + LocalDateTime.now().format(TS);
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String longString(int length) {
        return "A".repeat(length);
    }
}
