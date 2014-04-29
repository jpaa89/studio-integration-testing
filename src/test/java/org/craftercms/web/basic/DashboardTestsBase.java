package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 * @author Juan Avila
 *
 */
public abstract class DashboardTestsBase extends BaseTest {

    protected static final By GO_LIVE_QUEUE_SPAN_TOGGLE_BY = By.id("widget-toggle-GoLiveQueue");
    protected static final By APPROVED_SCHEDULED_ITEMS_SPAN_TOGGLE_BY = By.id("widget-toggle-approvedScheduledItems");
    protected static final By RECENTLY_MADE_LIVE_SPAN_TOGGLE_BY = By.id("widget-toggle-recentlyMadeLive");
    protected static final By MY_RECENT_ACTIVITY_SPAN_TOGGLE_BY = By.id("widget-toggle-MyRecentActivity");
    protected static final By ICON_GUIDE_SPAN_TOGGLE_BY = By.id("widget-toggle-icon-guide");

    protected WebElement[] widgetsSpanToggles;

    protected abstract String getUpdateString();

    /**
     * Test Dashboard Page Context Nav Functionality
     */
    @Test
    public void testContextNav() {
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        driver.navigate().to(dashboardUrl);

        logger.info("Wait for context navigation header to show");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("authoringContextNavHeader"));

        logger.info("Wait for logo link to show");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("acn-wcm-logo-link"));

        logger.info("Wait for dropdown toggler to show");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("acn-dropdown-toggler"));

        WebElement element = driver.findElement(By.id("acn-dropdown-toggler"));
        assertTrue(element.getText().equals("Site Content"));
        element.click();

        logger.info("Ensure dropdown displays when toggler is clicked");
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("acn-dropdown-menu-wrapper")).isDisplayed();
            }
        });
    }

    /**
     * Test Dashboard Page Title Functionality
     */
    @Test
    public void testSiteDashboardTitle() {
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        driver.navigate().to(dashboardUrl);

        logger.info("Check if title exists and match title with correct site value");
        WebElement element = driver.findElement(By.id("pageTitle"));
        assertTrue(element.getText().contains(seleniumProperties.getProperty("craftercms.sitetitle")));
        assertTrue(element.isDisplayed());
    }

    /**
     * Test Dashboard Page My Recent Activity Functionality
     *
     * @throws InterruptedException
     */
    @Test
    public void testMyRecentActivity() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        driver.navigate().to(dashboardUrl);

        logger.info("Edit and save page");
        CStudioSeleniumUtil.editAndSavePage(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), getUpdateString());

        logger.info("Refresh dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check my-recent-activity widget for edited page");
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.id("MyRecentActivity-body")).getText().contains(getUpdateString());
            }
        });
    }

    /**
     * Test Dashboard Page Icon Guide
     */
    @Test
    public void testIconGuide() {
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        WebElement element = driver.findElement(By.id("icon-guide"));
        logger.info("Scroll down to reach icon-guide");
        driver.manage().window().setPosition(new Point(0, driver.manage().window().getSize().height));

        logger.info("Check icon-guide is displayed");
        assertTrue(element.isDisplayed());

        logger.info("Check icon-guide text");
        assertTrue(element.getText().contains("Icon Guide"));
    }

    /**
     * Test Dashboard Page Footer
     */
    @Test
    public void testFooter() {
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        logger.info("Navigate to dashboard");
        driver.navigate().to(dashboardUrl);

        logger.info("Check footer exists");
        WebElement element = driver.findElement(By.id("footer"));
        assertTrue(element.isDisplayed());

        logger.info("Check footer texts");
        assertTrue(element.getText().contains("Alfresco Software"));
    }

}
