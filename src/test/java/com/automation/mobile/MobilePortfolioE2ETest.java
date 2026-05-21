package com.automation.mobile;

import com.automation.base.BaseMobileTest;
import com.automation.pages.mobile.MobileLoginPage;
import com.automation.pages.mobile.MobilePortfolioPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MobilePortfolioE2ETest extends BaseMobileTest {

    @Test(description = "E2E-MOB-001 | Full flow: Login → View Portfolio → Logout")
    public void loginViewPortfolioAndLogout() {
        // Step 1 — Login with valid credentials
        MobilePortfolioPage portfolioPage = new MobileLoginPage()
                .loginWith(config.get("mobile.email"), config.get("mobile.password"));

        // Step 2 — Navigate to Portfolio tab
        portfolioPage.navigateToPortfolio();
        Assert.assertTrue(portfolioPage.isPortfolioHeaderVisible(),
                "Step 2 failed — portfolio header not visible");

        // Step 3 — Validate portfolio data is populated
        String totalValue = portfolioPage.getTotalPortfolioValue();
        Assert.assertNotNull(totalValue, "Step 3 failed — total portfolio value is null");
        Assert.assertFalse(totalValue.isEmpty(), "Step 3 failed — total portfolio value is empty");
        log.info("Portfolio total value: {}", totalValue);

        int holdingCount = portfolioPage.getHoldingsCount();
        Assert.assertTrue(holdingCount > 0,
                "Step 3 failed — no holdings displayed in portfolio");
        log.info("Holdings displayed: {}", holdingCount);

        // Step 4 — Logout
        portfolioPage.logout();
        log.info("E2E mobile flow completed successfully");
    }

    @Test(description = "E2E-MOB-002 | Verify holdings list is not empty after login")
    public void portfolioShowsHoldingsAfterLogin() {
        MobilePortfolioPage portfolioPage = new MobileLoginPage()
                .loginWith(config.get("mobile.email"), config.get("mobile.password"))
                .navigateToPortfolio();

        Assert.assertTrue(portfolioPage.getHoldingsCount() > 0,
                "Holdings list should not be empty for a logged-in user");
        Assert.assertFalse(portfolioPage.getHoldingNames().isEmpty(),
                "Holding names list should not be empty");
    }
}
