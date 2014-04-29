/**
 *
 */
package org.craftercms.web.basic;

import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Praveen C Elineni
 * @author Juan Ávila
 */
public class AdminDashboardTests extends DashboardTestsBase {

    @Override
    protected String getUpdateString() {
        return "About Page Updated1";
    }

    @Override
    protected String getUsername() {
        return seleniumProperties.getProperty("craftercms.admin.username");
    }

    @Override
    protected String getPassword() {
        return seleniumProperties.getProperty("craftercms.admin.password");
    }

    /**
     * Test Dashboard Widgets collapsible states
     */
    @Test
    public void testDashboardWidgets() throws InterruptedException {

        WebElement goLiveQueueSpanToggle;
        WebElement approvedScheduledItemsSpanToggle;
        WebElement recentlyMadeLiveSpanToggle;
        WebElement myRecentActivitySpanToggle;
        WebElement iconGuideSpanToggle;

        List<WebElement> menuLinks;

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        login();
        logger.info("Navigate to dashboard");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        goLiveQueueSpanToggle = driver.findElement(GO_LIVE_QUEUE_SPAN_TOGGLE_BY);
        approvedScheduledItemsSpanToggle = driver.findElement(APPROVED_SCHEDULED_ITEMS_SPAN_TOGGLE_BY);
        recentlyMadeLiveSpanToggle = driver.findElement(RECENTLY_MADE_LIVE_SPAN_TOGGLE_BY);
        myRecentActivitySpanToggle = driver.findElement(MY_RECENT_ACTIVITY_SPAN_TOGGLE_BY);
        iconGuideSpanToggle = driver.findElement(ICON_GUIDE_SPAN_TOGGLE_BY);

        widgetsSpanToggles = new WebElement[]{goLiveQueueSpanToggle,approvedScheduledItemsSpanToggle,recentlyMadeLiveSpanToggle,myRecentActivitySpanToggle,iconGuideSpanToggle};

        logger.info("Click the '-' widget icons to collapse the widgets");
        for(WebElement element: widgetsSpanToggles) {
            CStudioSeleniumUtil.clickOn(driver,element);
        }

        logger.info("Check the widgets are collapsed");
        for(WebElement element: widgetsSpanToggles) {
            assertTrue("ttOpen".equals(element.getAttribute("class")));
        }

        logger.info("Check widgets menus are no longer displayed");
        String widgetIdsXpathOR = "@id='GoLiveQueue' or @id='approvedScheduledItems' or @id='recentlyMadeLive' or @id='MyRecentActivity'";
        menuLinks = driver.findElements(By.xpath("//a[@class='widget-expand-state' and ancestor::div["+widgetIdsXpathOR+"]]"));
        assertFalse(menuLinks.isEmpty());
        for(WebElement element: menuLinks) {
            assertFalse(element.isDisplayed());
        }

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        goLiveQueueSpanToggle = driver.findElement(GO_LIVE_QUEUE_SPAN_TOGGLE_BY);
        approvedScheduledItemsSpanToggle = driver.findElement(APPROVED_SCHEDULED_ITEMS_SPAN_TOGGLE_BY);
        recentlyMadeLiveSpanToggle = driver.findElement(RECENTLY_MADE_LIVE_SPAN_TOGGLE_BY);
        myRecentActivitySpanToggle = driver.findElement(MY_RECENT_ACTIVITY_SPAN_TOGGLE_BY);
        iconGuideSpanToggle = driver.findElement(ICON_GUIDE_SPAN_TOGGLE_BY);

        widgetsSpanToggles = new WebElement[]{goLiveQueueSpanToggle,approvedScheduledItemsSpanToggle,recentlyMadeLiveSpanToggle,myRecentActivitySpanToggle,iconGuideSpanToggle};

        logger.info("Check the widgets remain collapsed");
        for(WebElement element: widgetsSpanToggles) {
            assertTrue("ttOpen".equals(element.getAttribute("class")));
        }

        logger.info("Click the '+' widget icons to expand the widgets");
        for(WebElement element: widgetsSpanToggles) {
            CStudioSeleniumUtil.clickOn(driver,element);
        }

        logger.info("Check the widgets are expanded");
        for(WebElement element: widgetsSpanToggles) {
            assertTrue("ttClose".equals(element.getAttribute("class")));
        }

        logger.info("Check widgets menus are now displayed");
        menuLinks = driver.findElements(By.xpath("//a[@class='widget-expand-state' and ancestor::div["+widgetIdsXpathOR+"]]"));

        assertFalse(menuLinks.isEmpty());

        for(WebElement element: menuLinks) {
            assertTrue(element.isDisplayed());
        }

    }

}