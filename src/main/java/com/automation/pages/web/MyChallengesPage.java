package com.automation.pages.web;

import com.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class MyChallengesPage extends BasePage {

    private static final By CHALLENGE_LIST  = By.cssSelector(".challenge-list .challenge-item, table.challenges tbody tr");
    private static final By CHALLENGE_TITLE = By.cssSelector(".challenge-title, td.title");
    private static final By PAGE_HEADER     = By.cssSelector("h1, .page-title");
    private static final By EMPTY_STATE     = By.cssSelector(".empty-state, .no-challenges");

    public boolean isChallengeDisplayed(String challengeTitle) {
        List<String> titles = getChallengeNames();
        boolean found = titles.stream().anyMatch(t -> t.contains(challengeTitle));
        log.info("Challenge '{}' found in My Challenges: {}", challengeTitle, found);
        return found;
    }

    public List<String> getChallengeNames() {
        return driver.findElements(CHALLENGE_LIST)
                .stream()
                .map(el -> {
                    try {
                        return el.findElement(By.cssSelector(".challenge-title, td.title")).getText();
                    } catch (Exception e) {
                        return el.getText();
                    }
                })
                .collect(Collectors.toList());
    }

    public int getChallengeCount() {
        return driver.findElements(CHALLENGE_LIST).size();
    }

    public String getPageHeader() {
        return getText(PAGE_HEADER);
    }

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(EMPTY_STATE);
    }
}
