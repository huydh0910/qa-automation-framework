# QA Automation Framework

A multi-layer test automation framework built with **Java 11**, **Selenium 4**, **TestNG 7**, **RestAssured 5**, and **Appium 8** — covering Web UI, REST API, and Mobile (native) test automation.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Project Structure](#project-structure)
3. [Prerequisites](#prerequisites)
4. [Setup](#setup)
5. [Configuration](#configuration)
6. [Running Tests](#running-tests)
7. [Test Suites](#test-suites)
8. [Reports](#reports)
9. [CI/CD — Jenkins](#cicd--jenkins)
10. [BrowserStack Integration](#browserstack-integration)
11. [Mobile (Appium) Setup](#mobile-appium-setup)
12. [Coverage](#coverage)

---

## Tech Stack

| Concern | Technology |
|---------|-----------|
| Language | Java 11 |
| Build Tool | Maven 3.8+ |
| Web Automation | Selenium 4.18 |
| Driver Management | WebDriverManager 5.7 |
| Test Framework | TestNG 7.9 |
| API Testing | RestAssured 5.4 |
| Mobile Automation | Appium 8.6 (optional) |
| Reporting | ExtentReports 5.1 |
| Serialisation | Jackson 2.16 + Lombok |
| Logging | Log4j2 |
| CI/CD | Jenkins (declarative pipeline) |
| Cloud Execution | BrowserStack |

---

## Project Structure

```
qa-automation-framework/
├── Jenkinsfile                         # CI/CD pipeline definition
├── browserstack.yml                    # BrowserStack cloud config
├── pom.xml                             # Maven dependencies + profiles
│
└── src/
    ├── main/java/com/automation/
    │   ├── config/
    │   │   └── ConfigManager.java      # Singleton config reader (env-aware)
    │   ├── drivers/
    │   │   ├── DriverManager.java      # ThreadLocal WebDriver lifecycle
    │   │   ├── WebDriverFactory.java   # Local / remote driver creation
    │   │   └── MobileDriverFactory.java# Appium Android/iOS driver
    │   ├── pages/
    │   │   ├── BasePage.java           # Shared Selenium actions + waits
    │   │   ├── web/
    │   │   │   ├── LoginPage.java
    │   │   │   ├── HomePage.java
    │   │   │   ├── ChallengePage.java
    │   │   │   └── MyChallengesPage.java
    │   │   └── mobile/
    │   │       ├── MobileLoginPage.java
    │   │       └── MobilePortfolioPage.java
    │   ├── api/
    │   │   ├── BaseApiClient.java      # RestAssured RequestSpecification
    │   │   ├── endpoints/
    │   │   │   ├── AuthEndpoint.java   # POST /login
    │   │   │   └── UserEndpoint.java   # GET/PUT/PATCH /users/{id}
    │   │   └── models/                 # Jackson + Lombok request/response POJOs
    │   └── utils/
    │       ├── TokenManager.java       # ThreadLocal API token store
    │       ├── ReportManager.java      # ExtentReports singleton
    │       ├── ScreenshotUtil.java     # Auto-capture on failure
    │       └── TestDataGenerator.java  # Unique test data helpers
    │
    └── test/
        ├── java/com/automation/
        │   ├── base/
        │   │   ├── BaseTest.java       # Web test lifecycle (@Before/@After)
        │   │   ├── BaseApiTest.java    # API test lifecycle + token setup
        │   │   └── BaseMobileTest.java # Mobile test lifecycle
        │   ├── web/
        │   │   ├── LoginTest.java
        │   │   └── ChallengeE2ETest.java
        │   ├── api/
        │   │   ├── LoginApiTest.java
        │   │   ├── GetUserApiTest.java
        │   │   ├── UpdateUserApiTest.java
        │   │   └── ApiE2ETest.java
        │   └── mobile/
        │       ├── MobileLoginTest.java
        │       └── MobilePortfolioE2ETest.java
        └── resources/
            ├── config/
            │   ├── common.properties
            │   ├── dev.properties
            │   ├── qa.properties
            │   └── staging.properties
            ├── log4j2.xml
            └── testng/
                ├── testng-web.xml
                ├── testng-api.xml
                ├── testng-mobile.xml
                ├── testng-e2e.xml
                └── testng-parallel.xml
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 11+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Chrome | Latest | Auto-managed by WebDriverManager |
| Firefox / Edge | Latest | Optional — selectable via `-Dbrowser` |
| Appium Server | 2.x | Only required for mobile tests |
| Android Studio / Xcode | Latest | Only required for mobile tests |

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/qa-automation-framework.git
cd qa-automation-framework
```

### 2. Install dependencies

```bash
mvn clean install -DskipTests
```

### 3. Update credentials

Edit the relevant environment file under `src/test/resources/config/`:

```properties
# qa.properties — update with real test credentials
web.username=your_ctflearn_email@example.com
web.password=YourCTFPassword

api.email=eve.holt@reqres.in
api.password=cityslicka

mobile.email=your_mobile_user@example.com
mobile.password=YourMobilePassword
```

> **Security note:** Never commit real credentials. Use environment variables or a secrets manager for CI pipelines.

---

## Configuration

The `ConfigManager` loads `common.properties` first, then overlays the environment-specific file. Any property can be overridden at runtime with a `-D` system property.

| System Property | Default | Description |
|-----------------|---------|-------------|
| `env` | `qa` | Target environment (`dev` / `qa` / `staging`) |
| `browser` | `chrome` | Browser for web tests (`chrome` / `firefox` / `edge`) |
| `headless` | `false` | Run browser headlessly |
| `remote` | `false` | Use remote WebDriver (BrowserStack) |
| `platform` | `android` | Mobile platform (`android` / `ios`) |

---

## Running Tests

### Run a specific suite

```bash
# Web UI tests
mvn test -Pweb -Denv=qa

# API tests
mvn test -Papi -Denv=qa

# Mobile tests
mvn test -Pmobile -Denv=qa -Dplatform=android

# Full E2E suite
mvn test -Pe2e -Denv=staging

# Parallel execution (4 threads)
mvn test -Pparallel -Denv=qa
```

### Override browser or environment inline

```bash
# Headless Firefox on staging
mvn test -Pweb -Denv=staging -Dbrowser=firefox -Dheadless=true

# Chrome against dev environment
mvn test -Pweb -Denv=dev -Dbrowser=chrome
```

### Run a single test class

```bash
mvn test -Papi -Dtest=LoginApiTest -Denv=qa
```

### Run on BrowserStack

```bash
mvn test -Pweb -Dremote=true -Denv=qa \
  -DBROWSERSTACK_USERNAME=your_user \
  -DBROWSERSTACK_ACCESS_KEY=your_key
```

---

## Test Suites

| Maven Profile | TestNG XML | Contents |
|--------------|-----------|----------|
| `web` | `testng-web.xml` | `LoginTest`, `ChallengeE2ETest` |
| `api` | `testng-api.xml` | `LoginApiTest`, `GetUserApiTest`, `UpdateUserApiTest` |
| `mobile` | `testng-mobile.xml` | `MobileLoginTest`, `MobilePortfolioE2ETest` |
| `e2e` | `testng-e2e.xml` | All three E2E flows combined |
| `parallel` | `testng-parallel.xml` | Web + API tests, 4 threads |

---

## Reports

ExtentReports generates an interactive HTML report after every run:

```
reports/index.html     ← open in any browser
reports/automation.log ← full execution log
screenshots/           ← auto-captured on test failure
```

Open the report after a test run:

```bash
# Windows
start reports/index.html

# macOS
open reports/index.html
```

---

## CI/CD — Jenkins

The `Jenkinsfile` at the repository root defines a declarative pipeline with three parameters:

| Parameter | Options | Default |
|-----------|---------|---------|
| `ENV` | `qa` / `dev` / `staging` | `qa` |
| `SUITE` | `web` / `api` / `mobile` / `e2e` / `parallel` | `web` |
| `BROWSER` | `chrome` / `firefox` / `edge` | `chrome` |

### Steps the pipeline runs

1. **Checkout** — clones the repository
2. **Build** — compiles sources (`mvn clean compile -DskipTests`)
3. **Run Tests** — executes the selected suite headlessly
4. **Publish Reports** — attaches the ExtentReports HTML as a Jenkins build artifact
5. **Email notification** — sends failure alerts to `$DEFAULT_RECIPIENTS`

### Setting up in Jenkins

1. Create a new **Pipeline** job
2. Set **Pipeline script from SCM** → Git → your repository URL
3. Set **Script Path** to `Jenkinsfile`
4. Add credentials for `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY` as Jenkins secret text if using BrowserStack

---

## BrowserStack Integration

`browserstack.yml` configures cloud execution across:

- **Browsers:** Chrome (latest), Firefox (latest), Safari (latest on macOS Ventura)
- **Devices:** Samsung Galaxy S23 (Android 13), iPhone 14 (iOS 16)
- **Parallelism:** 2 sessions per platform

Set credentials as environment variables:

```bash
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_access_key
```

Then run with the remote flag:

```bash
mvn test -Pweb -Dremote=true -Denv=qa
```

---

## Mobile (Appium) Setup

Mobile tests are **optional** and require Appium Server + an Android emulator or iOS simulator.

### 1. Install Appium

```bash
npm install -g appium
appium driver install uiautomator2   # Android
appium driver install xcuitest       # iOS
```

### 2. Start Appium Server

```bash
appium --port 4723
```

### 3. Configure device capabilities

Update the relevant `*.properties` file:

```properties
mobile.app.path=/absolute/path/to/your-app.apk
android.device.name=emulator-5554
android.platform.version=12.0
appium.server.url=http://localhost:4723

# iOS
ios.device.name=iPhone 14
ios.platform.version=16.0
```

### 4. Run mobile tests

```bash
mvn test -Pmobile -Dplatform=android -Denv=qa
# or
mvn test -Pmobile -Dplatform=ios -Denv=qa
```

> **Note:** Mobile page locators in `MobileLoginPage` and `MobilePortfolioPage` use `AppiumBy.accessibilityId()`. Update the locator values to match the actual `accessibility-id` or `resource-id` attributes of your app's UI elements before running.

---

## Coverage

| Test ID | Description | Layer | Type |
|---------|-------------|-------|------|
| TC-US01-001 | Login with valid credentials | Web | Positive |
| TC-US01-002 | Login with wrong password | Web | Negative |
| TC-US01-003 | Login with empty fields | Web | Negative |
| E2E-WEB-001 | Create challenge → verify in My Challenges → logout | Web | E2E |
| E2E-WEB-002 | Submit challenge form without title | Web | Negative |
| API-LOGIN-001 | Valid login returns token | API | Positive |
| API-LOGIN-002 | Login response deserialises correctly | API | Positive |
| API-LOGIN-003 | Missing password returns 400 | API | Negative |
| API-LOGIN-004 | Missing email returns 400 | API | Negative |
| API-LOGIN-005 | Unregistered email returns 400 | API | Negative |
| API-GET-001 | GET existing user returns 200 + full data | API | Positive |
| API-GET-002 | GET response deserialises to model | API | Positive |
| API-GET-003 | GET a different valid user | API | Positive |
| API-GET-004 | GET non-existent user returns 404 | API | Negative |
| API-GET-005 | GET user id=0 returns 4xx | API | Negative |
| API-PUT-001 | PUT update user returns 200 + updated fields | API | Positive |
| API-PUT-002 | PUT response deserialises to model | API | Positive |
| API-PATCH-003 | PATCH partial update returns 200 | API | Positive |
| API-PUT-004 | PUT empty body returns no 500 | API | Negative |
| API-PUT-005 | PUT non-existent user behaviour documented | API | Negative |
| E2E-API-001 | Login → Get User → Update User full flow | API | E2E |
| MOB-LOGIN-001 | Valid credentials show portfolio screen | Mobile | Positive |
| MOB-LOGIN-002 | Invalid password shows error | Mobile | Negative |
| MOB-LOGIN-003 | Empty credentials show error | Mobile | Negative |
| E2E-MOB-001 | Login → Portfolio → validate data → Logout | Mobile | E2E |
| E2E-MOB-002 | Holdings list is populated after login | Mobile | Positive |

**Total: 26 test cases** across Web UI, REST API, and Mobile layers.
