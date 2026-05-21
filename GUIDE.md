# Framework Guide — Java (Selenium + TestNG + RestAssured + Appium)
**Version:** 1.0 | **Project:** qa-automation-framework | **Last Updated:** 2026-05-21

> This guide explains not just **how** to use the framework but **why** every technology and
> pattern was chosen. Read it once end-to-end before writing your first test.

---

## Table of Contents

1. [Why This Tech Stack?](#1-why-this-tech-stack)
2. [Prerequisites & Installation](#2-prerequisites--installation)
3. [Project Architecture](#3-project-architecture)
4. [Configuration System](#4-configuration-system)
5. [Driver Management](#5-driver-management)
6. [Page Object Model](#6-page-object-model)
7. [API Testing with RestAssured](#7-api-testing-with-restassured)
8. [Mobile Testing with Appium](#8-mobile-testing-with-appium)
9. [Writing Your First Test (Step-by-Step)](#9-writing-your-first-test-step-by-step)
10. [Running Tests](#10-running-tests)
11. [Understanding Reports](#11-understanding-reports)
12. [CI/CD with Jenkins](#12-cicd-with-jenkins)
13. [BrowserStack Integration](#13-browserstack-integration)
14. [Troubleshooting](#14-troubleshooting)
15. [Best Practices Checklist](#15-best-practices-checklist)

---

## 1. Why This Tech Stack?

Understanding the "why" behind each tool prevents cargo-cult usage — using a tool
just because it's popular, without knowing what problem it actually solves.

---

### 1.1 Java 11

**Problem it solves:** Test automation needs a statically-typed, enterprise-supported language
that scales from 10 tests to 10,000 tests without hidden runtime errors.

**Why Java specifically:**
- **Static typing** — type mismatches (e.g. passing a `String` where an `int` is expected)
  are caught at compile time, not at 2 AM during a production regression run
- **Mature ecosystem** — Selenium, TestNG, RestAssured, Appium all have first-class Java
  support; libraries are battle-tested and documented
- **Long-term support** — Java 11 LTS means security patches until 2026+; important for
  CI environments that cannot update frequently
- **IDE support** — IntelliJ IDEA and Eclipse provide refactoring, code navigation, and
  step-through debugging that scripting languages lack

> **Why Java 11 and not Java 17/21?**
> Java 11 is the most widely supported LTS across CI runners, Docker images, and Jenkins
> agents as of 2024. Java 17+ adds features (records, sealed classes) but creates
> compatibility friction with older Appium/Selenium transitive dependencies.

---

### 1.2 Selenium WebDriver 4.18

**Problem it solves:** Controlling a real browser programmatically — clicking, typing,
navigating — just like a human would.

**Why Selenium WebDriver:**
- **W3C standard** — from v4 onwards, Selenium implements the official W3C WebDriver
  specification. Every browser vendor (Chrome, Firefox, Safari) ships a driver that
  complies with this spec, so your tests are not tied to a single vendor
- **CDP (Chrome DevTools Protocol) support** — Selenium 4 can intercept network requests,
  mock responses, and emulate devices using CDP, which v3 could not do
- **Cross-browser** — the same `WebDriver` interface works on Chrome, Firefox, Edge, and
  Safari; only the driver binary changes
- **Industry standard** — the largest pool of existing knowledge, Stack Overflow answers,
  and community support of any browser automation tool

> **Why not Playwright for the Java layer?**
> Playwright has a Java API (`playwright-java`) but the ecosystem is newer, documentation
> is thinner, and the Appium integration story for native mobile is non-existent in
> Playwright. For a framework that must cover Web + API + Native Mobile in one codebase,
> Selenium + Appium is the proven combination.

---

### 1.3 TestNG 7.9

**Problem it solves:** Organising, executing, and reporting on hundreds of tests with
dependency management, parallel execution, and flexible configuration.

**Why TestNG over JUnit 5:**

| Feature | TestNG | JUnit 5 |
|---------|--------|---------|
| Parallel execution | Native, XML-configured | Requires plugins |
| Test grouping (`@Test(groups=...)`) | Native | Via tags + launcher |
| Dependency between tests | `@Test(dependsOnMethods=...)` | Not native |
| Data-driven (`@DataProvider`) | Native | `@MethodSource` (more verbose) |
| Suite XML configuration | `testng.xml` — powerful | Not native |
| `@BeforeSuite` / `@AfterSuite` | Native | `@BeforeAll` at class level only |

For test automation (not unit testing), TestNG's **suite XML** is the key advantage:
you can define parallel execution strategy, thread count, and test groupings in one
XML file without writing any Java code.

---

### 1.4 Maven

**Problem it solves:** Dependency management, build lifecycle, and environment-specific
test execution — all declarative (XML, not scripts).

**Why Maven over Gradle:**
- **Stability** — Maven's build lifecycle has been the same for 15 years. Gradle changes
  APIs frequently
- **Convention over configuration** — Maven's standard directory layout
  (`src/main/java`, `src/test/java`) means any Java developer reads the project
  without a README
- **Profile system** — `<profiles>` in `pom.xml` let you switch between test suites
  (`-Pweb`, `-Papi`, `-Pmobile`) without scripts or if-else blocks
- **Surefire plugin** — the `maven-surefire-plugin` bridges Maven and TestNG; you pass
  `-Dtest=ClassName#method` or `-Denv=staging` without modifying code

---

### 1.5 RestAssured 5.4

**Problem it solves:** Sending HTTP requests and asserting responses in a readable,
chainable way — without boilerplate `HttpClient` code.

**Why RestAssured:**
```java
// Without RestAssured (raw Java HttpClient — 20+ lines of boilerplate):
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://reqres.in/api/login"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"...\",\"password\":\"...\"}"))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
assertEquals(200, response.statusCode());
// ...parse body manually with Jackson...

// With RestAssured (5 lines, human-readable):
given()
    .contentType(ContentType.JSON)
    .body(new LoginRequest("eve.holt@reqres.in", "cityslicka"))
.when()
    .post("/login")
.then()
    .statusCode(200)
    .body("token", notNullValue());
```

RestAssured's **Given / When / Then** syntax mirrors BDD test cases directly,
making tests readable by non-developers.

---

### 1.6 Appium 8.6

**Problem it solves:** Automating native iOS and Android applications without modifying
the app's source code.

**Why Appium:**
- **No code injection** — unlike Espresso (Android-only, requires app modification),
  Appium controls the app from the outside via OS accessibility APIs
- **Cross-platform** — one Appium server supports both Android (UiAutomator2) and iOS
  (XCUITest) with a shared Java client
- **WebDriver protocol** — Appium extends the WebDriver protocol, so the same locator
  concepts (by ID, accessibility ID, XPath) apply as in web testing
- **Real device + emulator** — same code runs on a physical device or an Android/iOS
  emulator/simulator

---

### 1.7 Page Object Model (POM)

**Problem it solves:** When UI locators change, you should fix them in **one place**,
not in every test file that uses them.

**The principle:**
```
Without POM:                    With POM:
test_login.java                 LoginPage.java
  driver.findElement(By.id("username")).sendKeys("user");    fillUsername("user");
  driver.findElement(By.id("username")).sendKeys("user");    ^-- one definition
  driver.findElement(By.id("username")).sendKeys("user");         everywhere
```

In this framework:
- **Page classes** (`LoginPage`, `HomePage`, etc.) own locators and actions
- **Test classes** (`LoginTest`, etc.) own assertions and test flow
- If the `#username` field is renamed to `#email`, you change **one line** in
  `LoginPage.java`, and all tests immediately pick up the fix

---

### 1.8 Lombok

**Problem it solves:** Java requires verbose boilerplate for simple data classes
(getters, setters, constructors). Lombok generates this at compile time.

```java
// Without Lombok — 30+ lines for a simple request model:
public class LoginRequest {
    private String email;
    private String password;
    public LoginRequest(String email, String password) { ... }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    // + Builder, equals, hashCode, toString...
}

// With Lombok — 5 lines:
@Getter
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
// Usage: LoginRequest.builder().email("...").password("...").build();
```

Lombok annotations are processed at compile time (not runtime), so there is
**zero performance overhead**.

---

### 1.9 ThreadLocal Pattern

**Problem it solves:** When multiple tests run in parallel, they must each have their
own browser instance and API token. Without isolation, Thread A's `driver.quit()` would
crash Thread B mid-test.

**How it works:**
```java
// ThreadLocal is like a HashMap<Thread, T>
// Each thread stores its own value and never sees another thread's value

private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

// Thread 1 sets its driver:
driverThreadLocal.set(new ChromeDriver());

// Thread 2 sets its driver independently:
driverThreadLocal.set(new FirefoxDriver());

// Thread 1 gets back ONLY its Chrome driver, never Thread 2's Firefox:
WebDriver driver = driverThreadLocal.get(); // returns ChromeDriver for Thread 1
```

This is why `DriverManager.initDriver()` is called in `@BeforeMethod` (once per test)
and `DriverManager.quitDriver()` in `@AfterMethod` — each test thread initialises
and cleans up its own driver independently.

---

## 2. Prerequisites & Installation

### 2.1 Required Software

| Software | Version | Download | Verify |
|----------|---------|----------|--------|
| JDK | 11 LTS | https://adoptium.net | `java -version` |
| Maven | 3.8+ | https://maven.apache.org | `mvn -version` |
| Git | 2.x | https://git-scm.com | `git --version` |
| Chrome | Latest | https://google.com/chrome | — |
| IntelliJ IDEA | Community | https://jetbrains.com | — |
| Appium Server | 2.x | `npm install -g appium` | `appium -v` |
| Android Studio | Latest | Optional — for mobile tests | — |

### 2.2 Clone and Install

```bash
# 1. Clone the repository
git clone https://github.com/huydh0910/qa-automation-framework.git
cd qa-automation-framework

# 2. Compile and download all dependencies (no tests yet)
mvn clean install -DskipTests

# 3. Verify the build succeeds — you should see BUILD SUCCESS
```

### 2.3 IDE Setup (IntelliJ IDEA)

1. Open IntelliJ → **File → Open** → select the `qa-automation-framework` folder
2. IntelliJ detects `pom.xml` and imports it as a Maven project automatically
3. Install the **Lombok plugin**: `Settings → Plugins → search "Lombok" → Install`
4. Enable annotation processing: `Settings → Build → Compiler → Annotation Processors → Enable`
5. Right-click any `*Test.java` file → **Run** to verify the setup

---

## 3. Project Architecture

### 3.1 Layer Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        TEST LAYER                           │
│   LoginTest  ChallengeE2ETest  LoginApiTest  MobileLoginTest│
│          (src/test/java/com/automation/...)                 │
└──────────────────────────┬──────────────────────────────────┘
                           │ uses
┌──────────────────────────▼──────────────────────────────────┐
│                      BASE TEST LAYER                        │
│        BaseTest  BaseApiTest  BaseMobileTest                │
│   (@BeforeMethod: init driver/token, @AfterMethod: teardown)│
└────────┬─────────────────┬────────────────────┬────────────┘
         │                 │                    │
   ┌─────▼──────┐   ┌──────▼──────┐   ┌────────▼───────┐
   │  PAGE LAYER│   │  API LAYER  │   │  MOBILE LAYER  │
   │  (POM)     │   │(RestAssured)│   │  (Appium)      │
   │  LoginPage │   │ AuthEndpoint│   │MobileLoginPage │
   │  HomePage  │   │ UserEndpoint│   │MobilePortfolio │
   └─────┬──────┘   └──────┬──────┘   └────────┬───────┘
         │                 │                    │
   ┌─────▼─────────────────▼────────────────────▼────────┐
   │                  FOUNDATION LAYER                    │
   │  ConfigManager  DriverManager  TokenManager          │
   │  ReportManager  ScreenshotUtil  TestDataGenerator    │
   └──────────────────────────────────────────────────────┘
```

### 3.2 The Test Execution Flow

When you run `mvn test -Pweb -Denv=qa`, here is the exact sequence:

```
1. Maven reads pom.xml → activates 'web' profile
2. Surefire plugin reads testng-web.xml
3. TestNG creates a thread pool (1 thread by default, 4 for parallel suite)
4. For each @Test method:
   a. @BeforeSuite → ReportManager.getReport() — creates the HTML report skeleton
   b. @BeforeMethod → ConfigManager.getInstance() — loads qa.properties
                    → DriverManager.initDriver() — launches Chrome
                    → ReportManager.createTest() — creates a test entry
   c. @Test runs — your test code executes
   d. @AfterMethod → ScreenshotUtil.capture() — if test failed, take screenshot
                   → ReportManager records Pass/Fail
                   → DriverManager.quitDriver() — closes browser
5. @AfterSuite → ReportManager.flush() — writes reports/index.html
```

---

## 4. Configuration System

### 4.1 How ConfigManager Works

`ConfigManager` is a **Singleton** — meaning only one instance ever exists in memory.
This prevents loading properties files multiple times across hundreds of tests.

```java
// src/main/java/com/automation/config/ConfigManager.java

public class ConfigManager {
    private static ConfigManager instance; // the single shared instance

    // Private constructor — no one can call 'new ConfigManager()' from outside
    private ConfigManager() {
        String env = System.getProperty("env", "qa"); // reads -Denv=qa from command line
        loadFile("config/common.properties");         // shared settings (timeouts, paths)
        loadFile("config/" + env + ".properties");    // qa.properties / dev.properties / staging.properties
        // Later file OVERWRITES values from earlier file — env-specific beats common
    }

    // The only way to get the instance
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {                       // first call creates it
            instance = new ConfigManager();
        }
        return instance;                              // all subsequent calls return same object
    }
}
```

### 4.2 Property Resolution Priority (highest wins)

```
Priority 1: -Dkey=value (command line)         mvn test -Dbrowser=firefox
Priority 2: env-specific file                  qa.properties
Priority 3: common.properties                  shared defaults
```

This means you can **override any setting from the command line** without touching files —
essential for CI where you parameterise the browser or environment.

### 4.3 Environment Files Explained

```
src/test/resources/config/
├── common.properties    ← timeouts, screenshot path, BrowserStack URL
├── dev.properties       ← dev server URLs + dev test accounts
├── qa.properties        ← qa server URLs + qa test accounts   (DEFAULT)
└── staging.properties   ← staging URLs + staging test accounts
```

**To add a new config key:**
1. Add it to `common.properties` with a safe default
2. Override in specific env files if the value differs per environment
3. Read it in code: `config.get("my.new.key")`

---

## 5. Driver Management

### 5.1 DriverManager — The Most Critical Class

`DriverManager` is the class most likely to confuse newcomers, so read this carefully.

```java
// src/main/java/com/automation/drivers/DriverManager.java

public class DriverManager {

    // KEY INSIGHT: ThreadLocal<T> is a per-thread storage slot.
    // Think of it as: Map<Thread, WebDriver>
    // Thread 1 → ChromeDriver instance A
    // Thread 2 → FirefoxDriver instance B
    // They NEVER see each other's drivers.
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static void initDriver() {
        // System.getProperty reads -Dbrowser=firefox from the Maven command
        String browser  = System.getProperty("browser", "chrome");     // default: chrome
        boolean remote  = Boolean.parseBoolean(System.getProperty("remote", "false"));
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver driver = remote
                ? WebDriverFactory.createRemoteDriver(browser)   // BrowserStack/Selenium Grid
                : WebDriverFactory.createLocalDriver(browser, headless); // local machine

        driverThreadLocal.set(driver); // store driver FOR THIS THREAD ONLY
    }

    public static WebDriver getDriver() {
        // driverThreadLocal.get() returns the driver stored by THIS thread's initDriver() call
        // Safe to call from BasePage, DriverManager, etc. — always gets the right driver
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove(); // IMPORTANT: removes the entry from the map
            // Without remove(), memory leaks build up across thousands of test runs
        }
    }
}
```

### 5.2 Why `driverThreadLocal.remove()` Matters

```
Thread pool reuse scenario (WITHOUT remove):
  Test 1 runs  → Thread A stores driverA  → test ends, driverA.quit()
  Test 2 runs  → Thread A (reused!) → driverThreadLocal.get() returns driverA (QUIT browser!)
  → NullPointerException or "WebDriver connection refused"

WITH remove():
  Test 1 runs  → Thread A stores driverA  → test ends, driverA.quit(), remove()
  Test 2 runs  → Thread A (reused!) → driverThreadLocal.get() returns null
  → initDriver() is called again → fresh driverB stored → works correctly
```

---

## 6. Page Object Model

### 6.1 BasePage — The Foundation

```java
// src/main/java/com/automation/pages/BasePage.java

public abstract class BasePage {
    // 'protected' means subclasses (LoginPage, HomePage, etc.) can access these directly
    protected final WebDriver driver;   // the browser instance for this thread
    protected final WebDriverWait wait; // waits up to 'explicit.wait' seconds for elements

    protected BasePage() {
        this.driver = DriverManager.getDriver(); // fetches THIS thread's driver
        int timeout = ConfigManager.getInstance().getInt("explicit.wait"); // from properties
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // All page objects inherit these safe interaction methods:

    protected void click(By locator) {
        waitForClickable(locator).click(); // NEVER use driver.findElement().click() directly
        // Reason: direct click fails if element loads slowly; waitForClickable retries
    }

    protected void type(By locator, String text) {
        WebElement el = waitForVisible(locator);
        el.clear(); // always clear first — prevents appending to existing text on retry
        el.sendKeys(text);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false; // element not in DOM — return false, not throw
        }
    }
}
```

### 6.2 Writing a New Page Object

Follow this pattern exactly for every new page:

```java
// 1. Create file: src/main/java/com/automation/pages/web/MyNewPage.java
package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class MyNewPage extends BasePage {  // ALWAYS extend BasePage

    // 2. Define ALL locators as constants at the top
    //    Use the most specific, stable locator — prefer ID > CSS > XPath
    private static final By SEARCH_INPUT   = By.id("search");
    private static final By SEARCH_BUTTON  = By.cssSelector("button[type='submit']");
    private static final By RESULTS_LIST   = By.cssSelector(".results .item");

    // 3. Action methods — one method per user action
    //    Return 'this' for chaining, or a new Page if navigation occurs
    public MyNewPage search(String query) {
        type(SEARCH_INPUT, query);       // uses BasePage.type() — not driver.findElement()
        click(SEARCH_BUTTON);            // uses BasePage.click()
        return this;                     // returns self for chaining: page.search("foo").getResults()
    }

    public ResultsPage submitAndNavigate(String query) {
        type(SEARCH_INPUT, query);
        click(SEARCH_BUTTON);
        return new ResultsPage();        // navigates to a new page — return new page object
    }

    // 4. Query methods — return state, never interact
    public int getResultCount() {
        return driver.findElements(RESULTS_LIST).size();
    }

    public boolean isResultDisplayed(String title) {
        return isDisplayed(By.xpath("//div[@class='item'][contains(.,'" + title + "')]"));
    }
}
```

---

## 7. API Testing with RestAssured

### 7.1 How BaseApiClient Works

```java
// src/main/java/com/automation/api/BaseApiClient.java

public abstract class BaseApiClient {

    protected final RequestSpecification requestSpec;

    protected BaseApiClient() {
        ConfigManager config = ConfigManager.getInstance();
        RestAssured.baseURI = config.get("api.base.url"); // https://reqres.in/api

        // RequestSpecBuilder builds a REUSABLE spec — set once, used everywhere
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.get("api.base.url"))
                .setContentType(ContentType.JSON)   // Content-Type: application/json
                .setAccept(ContentType.JSON)         // Accept: application/json
                .log(LogDetail.ALL)                  // log ALL requests to console (great for debugging)
                .build();
    }

    // Call this for endpoints that need Authorization: Bearer <token>
    protected RequestSpecification withBearerToken(String token) {
        return RestAssured.given()
                .spec(requestSpec)                           // start with shared spec
                .header("Authorization", "Bearer " + token); // add token header
    }

    protected RequestSpecification baseRequest() {
        return RestAssured.given().spec(requestSpec); // spec without auth — for /login
    }
}
```

### 7.2 Token Flow (How Authentication Works in API Tests)

```
BaseApiTest.@BeforeMethod
    │
    ├── authEndpoint.getToken(email, password)
    │       │
    │       └── POST /api/login → { "token": "QpwL5tpe83ilfN2" }
    │
    └── TokenManager.setToken("QpwL5tpe83ilfN2")
            │
            └── ThreadLocal.set(token) ← stored for THIS test thread

LoginApiTest.@Test
    │
    └── TokenManager.getToken()
            │
            └── ThreadLocal.get() ← retrieves THIS thread's token

BaseApiTest.@AfterMethod
    │
    └── TokenManager.clearToken()
            │
            └── ThreadLocal.remove() ← prevents memory leaks
```

### 7.3 Writing a New API Test

```java
// src/test/java/com/automation/api/MyNewApiTest.java
package com.automation.api;

import com.automation.base.BaseApiTest;  // provides config, authEndpoint, token setup
import com.automation.utils.TokenManager;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;   // notNullValue, equalTo, etc.

public class MyNewApiTest extends BaseApiTest {

    private UserEndpoint userEndpoint;

    @BeforeClass   // runs once before all tests in THIS class
    public void init() {
        userEndpoint = new UserEndpoint();
    }

    // BaseApiTest.@BeforeMethod already obtains a fresh token and stores it in TokenManager.
    // Your test just retrieves it.

    @Test(description = "TC-API-XXX | Positive: my new test")
    public void myNewPositiveTest() {
        Response response = userEndpoint.getUser(2, TokenManager.getToken());

        // Hamcrest matchers — more readable than assertEquals
        response.then()
                .statusCode(200)
                .body("data.id",    equalTo(2))          // exact match
                .body("data.email", notNullValue())       // just check not null
                .body("data.email", containsString("@")); // partial match
    }

    @Test(description = "TC-API-XXX | Negative: missing field returns 400")
    public void myNegativeTest() {
        Response response = userEndpoint.getUser(9999, TokenManager.getToken());
        Assert.assertEquals(response.statusCode(), 404, "Non-existent user should return 404");
    }
}
```

---

## 8. Mobile Testing with Appium

### 8.1 Why Appium — the One-Line Explanation

> Appium is to mobile apps what Selenium is to web browsers: a protocol-compliant
> automation server that controls the UI without modifying the application under test.

### 8.2 Setup (One-Time)

```bash
# Step 1: Install Appium server
npm install -g appium@latest

# Step 2: Install platform drivers
appium driver install uiautomator2   # Android automation driver
appium driver install xcuitest       # iOS automation driver

# Step 3: Verify everything is installed
appium doctor --android  # checks Android SDK, ADB, emulator
appium doctor --ios      # checks Xcode, simulators (Mac only)

# Step 4: Start the Appium server (keep this terminal open while running mobile tests)
appium --port 4723
```

### 8.3 Update Capabilities in Config

Add to your `qa.properties`:
```properties
# Path to your app file
mobile.app.path=/Users/you/apps/trading-app.apk

# Android emulator
android.device.name=emulator-5554
android.platform.version=12.0

# iOS simulator (Mac only)
ios.device.name=iPhone 14
ios.platform.version=16.0

# Appium server
appium.server.url=http://localhost:4723
```

### 8.4 Updating Mobile Locators

Mobile locators use `accessibility-id` or `resource-id` attributes.
To find them, use Appium Inspector:

```bash
# Download Appium Inspector from: https://github.com/appium/appium-inspector
# Connect your device, start Appium, then use Inspector to tap elements and copy locators

# In the page objects, update:
private static final By EMAIL_FIELD = AppiumBy.accessibilityId("actual-id-from-inspector");
```

---

## 9. Writing Your First Test (Step-by-Step)

### Scenario: Add a test for "user can view challenge details"

**Step 1 — Create the Page Object** (if the page doesn't exist yet)

```java
// src/main/java/com/automation/pages/web/ChallengeDetailPage.java
package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class ChallengeDetailPage extends BasePage {
    private static final By CHALLENGE_TITLE       = By.cssSelector("h1.challenge-title");
    private static final By CHALLENGE_DESCRIPTION = By.cssSelector(".challenge-description");
    private static final By SUBMIT_FLAG_INPUT     = By.id("flag-input");

    public String getTitle() {
        return getText(CHALLENGE_TITLE);
    }

    public boolean isDescriptionVisible() {
        return isDisplayed(CHALLENGE_DESCRIPTION);
    }

    public ChallengeDetailPage submitFlag(String flag) {
        type(SUBMIT_FLAG_INPUT, flag);
        return this;
    }
}
```

**Step 2 — Write the Test**

```java
// src/test/java/com/automation/web/ChallengeDetailTest.java
package com.automation.web;

import com.automation.base.BaseTest;
import com.automation.pages.web.ChallengeDetailPage;
import com.automation.pages.web.HomePage;
import com.automation.pages.web.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ChallengeDetailTest extends BaseTest {  // ALWAYS extend BaseTest for web

    @Test(description = "TC-NEW-001 | Positive: challenge detail page shows title and description")
    public void challengeDetailPageIsComplete() {
        // Login — loginAs() is the reusable login method from LoginPage
        HomePage homePage = new LoginPage()
                .open(getBaseUrl())
                .loginAs(config.get("web.username"), config.get("web.password"));

        // Navigate — method chaining reads like plain English
        ChallengeDetailPage detailPage = homePage
                .navigateToChallenges()
                .clickFirstChallenge(); // you'll add this method to ChallengePage

        // Assert
        Assert.assertFalse(detailPage.getTitle().isEmpty(), "Title should not be empty");
        Assert.assertTrue(detailPage.isDescriptionVisible(), "Description should be visible");
    }
}
```

**Step 3 — Add the Test to a Suite XML**

```xml
<!-- src/test/resources/testng/testng-web.xml — add the new class -->
<test name="Challenge Detail Tests">
    <classes>
        <class name="com.automation.web.ChallengeDetailTest"/>
    </classes>
</test>
```

**Step 4 — Run It**

```bash
mvn test -Pweb -Dtest=ChallengeDetailTest -Denv=qa
```

---

## 10. Running Tests

### 10.1 All Commands Reference

```bash
# Run all web tests on QA (default environment)
mvn test -Pweb

# Run all API tests on QA
mvn test -Papi

# Run all mobile tests (Android)
mvn test -Pmobile -Dplatform=android

# Run E2E suite across all layers
mvn test -Pe2e

# Run parallel suite (4 threads)
mvn test -Pparallel

# Run a SINGLE test class
mvn test -Pweb -Dtest=LoginTest

# Run a SINGLE test method
mvn test -Pweb -Dtest=LoginTest#loginWithValidCredentials

# Change environment (dev / qa / staging)
mvn test -Pweb -Denv=staging

# Change browser
mvn test -Pweb -Dbrowser=firefox
mvn test -Pweb -Dbrowser=edge

# Run headless (no visible browser — required for CI)
mvn test -Pweb -Dheadless=true

# Run on BrowserStack (remote)
mvn test -Pweb -Dremote=true -DBROWSERSTACK_USERNAME=user -DBROWSERSTACK_ACCESS_KEY=key

# Combine options
mvn test -Pweb -Denv=staging -Dbrowser=firefox -Dheadless=true
```

### 10.2 Maven Profiles Explained

```xml
<!-- pom.xml — each profile just sets the testng.suite property -->
<profile>
    <id>web</id>   <!-- activated by -Pweb -->
    <properties>
        <testng.suite>src/test/resources/testng/testng-web.xml</testng.suite>
    </properties>
</profile>

<!-- The surefire plugin picks up ${testng.suite} and runs it -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>${testng.suite}</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

---

## 11. Understanding Reports

### 11.1 ExtentReports HTML Report

After a test run:
```bash
# Open the report (Windows)
start reports/index.html

# Or navigate to: reports/index.html in your browser
```

**What to look for:**

| Section | What it shows | Action if red |
|---------|--------------|---------------|
| Dashboard | Pass/Fail/Skip counts | Check the failed count |
| Failed Tests | Failure message + screenshot | Read the assertion error |
| Screenshot | Browser state at time of failure | Use to reproduce manually |
| Logs | Step-by-step execution log | Trace where it went wrong |
| System Info | Environment, browser, framework | Confirm you ran the right env |

### 11.2 Surefire XML Reports

```bash
# Also available for Jenkins JUnit plugin:
target/surefire-reports/*.xml
```

---

## 12. CI/CD with Jenkins

### 12.1 Jenkins Setup (First Time)

1. Install Jenkins: https://www.jenkins.io/download/
2. Install plugins:
   - **Pipeline** (declarative Jenkinsfile support)
   - **HTML Publisher** (for ExtentReports)
   - **Email Extension** (for failure notifications)
3. Create a **Pipeline** job → **Pipeline script from SCM** → paste your GitHub URL
4. Add BrowserStack credentials:
   - `Manage Jenkins → Credentials → Add → Secret Text`
   - ID: `browserstack-username` and `browserstack-access-key`

### 12.2 Triggering Builds

```
# Manually from Jenkins UI:
  Build with Parameters → ENV=qa, SUITE=api, BROWSER=chrome → Build

# On every Git push (set in Jenkins job):
  Build Triggers → GitHub hook trigger for GITScm polling

# Scheduled nightly regression:
  Build Triggers → Build periodically → 0 2 * * 1-5  (2 AM Mon-Fri)
```

---

## 13. BrowserStack Integration

BrowserStack runs your tests on **real browsers in the cloud** — Chrome on Windows 11,
Safari on macOS Ventura, Edge on Windows 10 — without you owning those machines.

```bash
# How it works:
# 1. Your test code sends WebDriver commands to BrowserStack hub URL
# 2. BrowserStack spins up a real VM with the requested browser
# 3. Test executes on that remote browser
# 4. Results, screenshots, and video appear in BrowserStack dashboard

# Run on BrowserStack:
export BROWSERSTACK_USERNAME=your_user
export BROWSERSTACK_ACCESS_KEY=your_key
mvn test -Pweb -Dremote=true -Denv=qa -Dbrowser=chrome
```

---

## 14. Troubleshooting

| Problem | Likely Cause | Fix |
|---------|-------------|-----|
| `WebDriver not initialised` | Called `getDriver()` before `initDriver()` | Ensure test extends `BaseTest` |
| `StaleElementReferenceException` | DOM re-rendered between find and use | Use `waitForClickable()` instead of caching elements |
| `TimeoutException` | Element not found within `explicit.wait` seconds | Increase `explicit.wait` in properties, or check locator |
| `Missing config key: web.username` | Properties file not loaded | Check `-Denv=qa` is set, file exists in `src/test/resources/config/` |
| `Token not stored for this thread` | `TokenManager.getToken()` before `setToken()` | Ensure test extends `BaseApiTest`, not `BaseTest` |
| Appium: `Could not start driver` | Appium server not running | Run `appium --port 4723` in a separate terminal |
| Appium: `No device found` | Emulator not started or wrong device name | Start emulator in Android Studio, update `android.device.name` |
| Build fails: `Lombok cannot find symbol` | Annotation processing disabled | Enable in `Settings → Compiler → Annotation Processors` |

---

## 15. Best Practices Checklist

Before committing any new test, verify:

- [ ] Test class extends the correct base (`BaseTest` / `BaseApiTest` / `BaseMobileTest`)
- [ ] All locators are defined as `private static final By` constants in the page object
- [ ] No `driver.findElement()` calls in test classes — only page object methods
- [ ] No `Thread.sleep()` — use `waitForVisible()` or `waitForClickable()` from `BasePage`
- [ ] Test has a descriptive `@Test(description = "TC-XXX | ...")` annotation
- [ ] Added to the appropriate `testng-*.xml` suite
- [ ] New test data uses `TestDataGenerator.uniqueChallengeName()` — never hardcoded names
- [ ] Sensitive values (passwords, tokens) are in `.properties` files, never in test code
- [ ] `config.get("key")` — not magic strings inline in tests
- [ ] Negative tests assert the error state, not just the absence of success
