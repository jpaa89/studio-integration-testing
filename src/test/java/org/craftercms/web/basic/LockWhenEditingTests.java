package org.craftercms.web.basic;


import org.craftercms.web.BaseSecondayDriverTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Juan Avila
 *
 */
public class LockWhenEditingTests extends BaseSecondayDriverTest {

    private static final String UPDATE_STRING = "About Us Page Updated";
    private static final String TOOLTIP_PAGE_ITEM_KEY = "Page:";
    private static final String TOOLTIP_STATUS_ITEM_KEY = "Status:";
    private static final String TOOLTIP_LAST_EDITED_ITEM_KEY = "Last Edited:";
    private static final String TOOLTIP_EDITED_BY_ITEM_KEY = "Edited by:";
    private static final String TOOLTIP_LOCKED_BY_ITEM_KEY = "Locked by:";

    /**
     * Tests locking when editing functionality
     */
    @Test
    public void lockWhenEditingTest() {

        String editPageUri = seleniumProperties.getProperty("craftercms.page.to.edit");


        By editedPageMyRecentActivityBy = By.xpath("//a[text()='"+UPDATE_STRING+"' and ancestor::tbody[@id='MyRecentActivity-tbody']]");
        By editedPageDropdownBy = By.xpath("//span[text()='"+UPDATE_STRING+"' and ancestor::div[@id='acn-dropdown-menu']]");

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);
        secondaryDriver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        logger.info("Login as author");
        useAuthorUser();
        login();

        String mainWindowHandle = driver.getWindowHandle();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        //edit the page to set its internal name and to place it on my recent activity
        CStudioSeleniumUtil.editAndSavePage(driver,editPageUri,UPDATE_STRING);

        logger.info("Open the dropdown menu and expand the pages tree");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(driver);

        logger.info("Check tooltip appears with Page, Status, Last Edited, Edited by, Locked by");
        displayDropdownToolTip(driver, editedPageDropdownBy);

        logger.info("Open edit form, switch to it and edit content without saving the changes");
        CStudioSeleniumUtil.editContentJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), seleniumProperties.getProperty("craftercms.page.content.type"), siteName);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
        CStudioSeleniumUtil.switchToEditWindow(driver);
        WebElement internalNameInput = driver.findElement(By.cssSelector("#internal-name .datum"));
        internalNameInput.clear();
        internalNameInput.sendKeys(UPDATE_STRING);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Switch to the main window");
        driver.switchTo().window(mainWindowHandle);
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);

        logger.info("Open the dropdown menu and expand the pages tree");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(driver);

        logger.info("Check tooltip appears and 'Locked by' appears with the current user id");
        HashMap<String,String> tooltipItems = displayDropdownToolTip(driver, editedPageDropdownBy);
        String lockedByUser = tooltipItems.get(TOOLTIP_LOCKED_BY_ITEM_KEY);
        assertTrue(getUsername().equals(lockedByUser));

        logger.info("Switch to edit window");
        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Click Save&Close button to unlock");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        logger.info("Switch to main window");
        CStudioSeleniumUtil.switchToMainWindow(driver);

        logger.info("Open edit form again");
        CStudioSeleniumUtil.editContentJS(driver, seleniumProperties.getProperty("craftercms.page.to.edit"), seleniumProperties.getProperty("craftercms.page.content.type"), siteName);

        // dropdown menu had to be closed because it interfered with  moving the mouse over an element in further steps
        logger.info("Close the site dropdown");
        CStudioSeleniumUtil.clickOn(driver, By.id("acn-dropdown-toggler"));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        logger.info("Check tooltip appears in My Recent Activity dashboard widget and 'Locked by' appears with the current user id");
        tooltipItems = displayDashboardToolTip(driver, editedPageMyRecentActivityBy);
        lockedByUser = tooltipItems.get(TOOLTIP_LOCKED_BY_ITEM_KEY);
        assertTrue(getUsername().equals(lockedByUser));

        logger.info("From now on working with the secondary window");
        logger.info("Login to an independent window as admin");
        useAdminUser();
        loginUsingSecondaryDriver();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(secondaryDriver, dashboardUrl);

        logger.info("Open the dropdown menu and expand the pages tree");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(secondaryDriver);

        logger.info("Check tooltip appears and 'Locked by' appears with a different user id (from teh current one)");
        tooltipItems = displayDropdownToolTip(secondaryDriver, editedPageDropdownBy);
        lockedByUser = tooltipItems.get(TOOLTIP_LOCKED_BY_ITEM_KEY);
        assertFalse(getUsername().equals(lockedByUser));

        logger.info("Right-click on the edited content in the site dropdown and check 'Edit' is not available");
        CStudioSeleniumUtil.waitForItemToDisplay(secondaryDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,editedPageDropdownBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(secondaryDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,editedPageDropdownBy);
        boolean editAvailable = CStudioSeleniumUtil.contextMenuOptionExists(secondaryDriver,"Edit",secondaryDriver.findElement(editedPageDropdownBy));
        assertFalse(editAvailable);

        // this is just to close the context menu and avoid an error later when right clicking again
        logger.info("Clicking somewhere else to get rid of the context menu");
        CStudioSeleniumUtil.clickOn(secondaryDriver,By.id("acn-search"));

        logger.info("Open the dropdown menu and expand the pages tree");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(secondaryDriver);

        logger.info("Right-click on the edited content in the site dropdown and click 'Unlock'");
        CStudioSeleniumUtil.contextMenuOption(secondaryDriver, "Unlock", secondaryDriver.findElement(editedPageDropdownBy));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Open the dropdown menu and expand the pages tree");
        CStudioSeleniumUtil.ensurePagesTreeIsExpanded(secondaryDriver);

        logger.info("Check tooltip appears and 'Locked by' appears with no user id (meaning that it is now unlocked)");
        tooltipItems = displayDropdownToolTip(secondaryDriver, editedPageDropdownBy);
        lockedByUser = tooltipItems.get(TOOLTIP_LOCKED_BY_ITEM_KEY);
        assertTrue(lockedByUser.isEmpty());

        logger.info("Right-click on the edited content in the site dropdown and click 'Edit'");
        CStudioSeleniumUtil.waitForItemToDisplay(secondaryDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,editedPageDropdownBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(secondaryDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,editedPageDropdownBy);
        CStudioSeleniumUtil.contextMenuOption(secondaryDriver, "Edit", secondaryDriver.findElement(editedPageDropdownBy));

        logger.info("Switch to edit window");
        CStudioSeleniumUtil.switchToEditWindow(secondaryDriver);

        logger.info("Check edit form is in read only mode");
        assertFalse(secondaryDriver.findElement(By.id("cstudio-form-readonly-banner")).isDisplayed());

        logger.info("From now on working with the primary window");

        logger.info("Switch to edit window");
        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Refresh dashboard");
        driver.navigate().refresh(); // not waiting is important in order to catch the alert in the next step

        logger.info("Wait for form to load");
        new WebDriverWait(driver, 60).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        logger.info("Check form is in read only mode");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id("cstudio-form-readonly-banner"));

        logger.info("Cancel form edition");
        CStudioSeleniumUtil.javascriptClick(driver,driver.findElement(By.xpath("//input[@value='Close']")));

        logger.info("Switch to main window");
        CStudioSeleniumUtil.switchToMainWindow(driver);

        logger.info("From now on working with the secondary window");

        logger.info("Save and Close form to unlock");
        CStudioSeleniumUtil.javascriptClick(secondaryDriver,secondaryDriver.findElement(By.id("cstudioSaveAndClose")));

        logger.info("Switch to main window");
        CStudioSeleniumUtil.switchToMainWindow(secondaryDriver);

    }

    private void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

    private void useAdminUser() {
        setUsername(seleniumProperties.getProperty("craftercms.admin.username"));
        setPassword(seleniumProperties.getProperty("craftercms.admin.password"));
    }

    /**
     * Displays the dropdown tooltip for the given element locator
     * @param sourceBy By to find the element that will get the mouse over
     * @return tooltip items
     */
    private HashMap<String,String> displayDropdownToolTip(WebDriver webDriver, By sourceBy){
        return displayToolTip(webDriver,By.id("acn-context-tooltipWrapper"),sourceBy);
    }

    /**
     * Displays the dashboard tooltip for the given element locator
     * @param sourceBy By to find the element that will get the mouse over
     * @return tooltip items
     */
    private HashMap<String,String> displayDashboardToolTip(WebDriver webDriver, By sourceBy){
        return displayToolTip(webDriver,By.id("acn-context-tooltipWrapper-widgets"),sourceBy);
    }

    /**
     * Displays the tooltip for the given item (sourceBy) withing the given container(acnTooltipDivWrapper)
     * @param webDriver
     * @param acnTooltipDivWrapper
     * @param sourceBy
     * @return
     */
    private HashMap<String,String> displayToolTip(WebDriver webDriver,By acnTooltipDivWrapper, By sourceBy){
        HashMap<String,String> toolTipItems;

        By tooltipElementItemsBy =  By.xpath("div[1]/table/tbody/tr");

        logger.info("Moving the mouse over the element to get the tooltip");
        CStudioSeleniumUtil.moveMouseTo(webDriver, sourceBy);

        WebElement tooltipElement = webDriver.findElement(acnTooltipDivWrapper);

        new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(
                ExpectedConditions.visibilityOf(tooltipElement));

        List<WebElement> toolTipElementItems = tooltipElement.findElements(tooltipElementItemsBy);

        assertFalse(toolTipElementItems.isEmpty());

        toolTipItems = new HashMap<String, String>();

        for(WebElement webElement : toolTipElementItems){
            List<WebElement> items = webElement.findElements(By.xpath("td"));
            toolTipItems.put(items.get(0).getText(),items.get(1).getText());
        }

        boolean allItemsExist = toolTipItems.containsKey(TOOLTIP_PAGE_ITEM_KEY) &&
                toolTipItems.containsKey(TOOLTIP_STATUS_ITEM_KEY) &&
                toolTipItems.containsKey(TOOLTIP_LAST_EDITED_ITEM_KEY) &&
                toolTipItems.containsKey(TOOLTIP_EDITED_BY_ITEM_KEY) &&
                toolTipItems.containsKey(TOOLTIP_LOCKED_BY_ITEM_KEY);

        logger.info("Checking the basic tooltip items exist");
        assertTrue(allItemsExist);

        return toolTipItems;

    }


}
