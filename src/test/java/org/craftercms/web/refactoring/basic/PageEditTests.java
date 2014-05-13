/**
 *
 */
package org.craftercms.web.refactoring.basic;

import org.craftercms.web.refactoring.CSBaseTest;
import org.craftercms.web.refactoring.handlers.CSDashboardHandler;
import org.craftercms.web.refactoring.handlers.editors.pages.impl.CSAboutPageEditorHandler;
import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;


/**
 * @author Praveen C Elineni
 * @author Juan Avila
 */
public class PageEditTests extends CSBaseTest {

    protected CSAboutPageEditorHandler aboutPageEditorHandler;
    protected CSDashboardHandler csDashboardHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        aboutPageEditorHandler = new CSAboutPageEditorHandler(driver,siteName);
        csDashboardHandler = new CSDashboardHandler(driver);
    }

    /**
     * Test Page Save and Close Functionality
     */
    @Test
    public void testPageEditSaveAndClose() {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        //TODO create a standard auto generated edit method ?
        logger.info("edit and save page");
        aboutPageEditorHandler.openEditFormJS();
        csNavigationHandler.switchToRecentlyOpenWindow();
        aboutPageEditorHandler.editInternalNameField(generateUpdateString("Edited-"),true);
        aboutPageEditorHandler.saveAndClose();
        csNavigationHandler.switchBackToPreviousWindow();

        logger.info("refresh dashboard");
        csNavigationHandler.refreshCurrentPage();

        logger.info("check edited page is in my recent activity widget");
        csDashboardHandler.getCsMyRecentActivityWidgetHandler().containsContent(aboutPageEditorHandler.getUri());

    }

    /**
     * Test Page Save and Preview Functionality
     */
    @Test
    public void testPageEditSaveAndPreview() {

        String updateString = generateUpdateString("Edited-");

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("edit about page");
        aboutPageEditorHandler.openEditFormJS();
        csNavigationHandler.switchToRecentlyOpenWindow();
        aboutPageEditorHandler.editInternalNameField(updateString, true);
        aboutPageEditorHandler.saveAndPreview();
        csNavigationHandler.switchBackToPreviousWindow();

        //TODO could this be handled in a different way? (maybe by returning a boolean when saving and previewing
        logger.info("wait for preview to sync");
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_PREVIEW_SYNC).until(new ExpectedCondition<Object>() {
            public Boolean apply(WebDriver webDriver) {
                try {
                    csNavigationHandler.switchToAlertAndAccept();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });

        logger.info("check the url matches edited page url");
        csNavigationHandler.isCurrentPage(baseUrl + "/" + aboutPageEditorHandler.getFileName());

        logger.info("check item content has changed");

        //TODO think about the readFileContents approach, although this looks ok too
        assertTrue(CSSeleniumUtil.readFileContents(previewDeployerPath + aboutPageEditorHandler.getUri(), updateString));

        logger.info("go back and close edit window so item is not locked");
        csNavigationHandler.switchToRecentlyOpenWindow();

        logger.info("cancel, no more editing is needed");
        aboutPageEditorHandler.cancel();

        csNavigationHandler.switchBackToPreviousWindow();

        //logout();

    }



    /**
     * Test Save And Preview My RecentActivity Functionality
     * TODO refactor
     */
    /*@Test
    public void testPageSaveAndPreviewMyRecentActivity() throws ParseException {
        driver.manage().timeouts().implicitlyWait(CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        CSDashboardWidgetHandler myRecentActivityHandler = new CSMyRecentActivityWidgetHandler(driver);
        String updateStringBody1 = updateString+ " (1)";
        String updateStringBody2 = updateString+ " (2)";
        String updateStringBody3 = updateString+ " (3)";
        String updateStringOriginalUrlName = seleniumProperties.getProperty("craftercms.page.to.edit.url").replace("/","");
        String updateStringNewUrlName = updateStringOriginalUrlName+"-updated-url";
        String editPageUri = seleniumProperties.getProperty("craftercms.page.to.edit");

        logger.info("Login as author");
        useAuthorUser();
        login();

        String mainWindowHandle = driver.getWindowHandle();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        logger.info("Navigate to About page");
        String fullPageToEditUrl = seleniumProperties.getProperty("craftercms.base.url") + seleniumProperties.getProperty("craftercms.page.to.edit.url");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, fullPageToEditUrl);

        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_PAGE_LOADING);

        try {
            logger.info("Click 'Edit' on contextual nav");
            CStudioSeleniumUtil.clickOn(driver, By.xpath("//div[@id='acn-active-content']//a[text()='Edit']"));
            CStudioSeleniumUtil.switchToEditWindow(driver); // Test if edit form window is present
            driver.switchTo().window(mainWindowHandle);
        }
        catch (TimeoutException e){ // workflow cancellation popup might have appeared

            logger.info("Click 'Continue' on the workflow cancellation popup");
            CStudioSeleniumUtil.clickOn(driver, By.xpath("//*[text()='Continue']"));
        }

        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_PAGE_LOADING);

        logger.info("Go to the dashboard");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        logger.info("Edit page");
        CStudioSeleniumUtil.switchToEditWindow(driver);
        String editWindowHandle = driver.getWindowHandle();

        logger.info("Edit body field (1)");
        editBodyField(updateStringBody1);

        logger.info("Click Save&Preview button and wait for change to complete");
        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndPreview"));

        driver.switchTo().window(mainWindowHandle);

        logger.info("Wait for preview to load");
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_PREVIEW_SYNC).until(new ExpectedCondition<Boolean>() {
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

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        logger.info(" Check edited page appears as 'in progress' in My Recent Activity with latest timestamp." );
        DashboardWidgetLabel mraWidgetEditedPageContentInfo = myRecentActivityHandler.dashboardWidgetLabel(editPageUri);
        String timestampFirstEdit = myRecentActivityMyLastEdit(editPageUri);
        assertFalse(timestampFirstEdit.isEmpty());
        assertEquals(DashboardWidgetLabel.STATUS_IN_PROGRESS, mraWidgetEditedPageContentInfo.getStatus());
        assertTrue(myRecentActivityHandler.containsContent(editPageUri));

        logger.info("Edit body field (2)");
        driver.switchTo().window(editWindowHandle);
        CStudioSeleniumUtil.waitFor(60); // For the timestamp minutes to change

        editBodyField(updateStringBody2);
        logger.info("Click Save&Close button");
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        logger.info("Switch to Dashboard page");
        driver.switchTo().window(mainWindowHandle);
        CStudioSeleniumUtil.waitFor(1);

        //TODO Info: Dashboard should reload automatically. Minor crafter bug!
        // To keep the test going uncomment these 2 lines:
        //CSSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        //CSSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_PAGE_LOADING);

        logger.info("Check if timespan of edited page is updated");
        String timestampSecondEdit = myRecentActivityMyLastEdit(editPageUri);
        assertFalse(timestampSecondEdit.isEmpty());
        assertNotEquals(timestampFirstEdit,timestampSecondEdit);

        logger.info("Click 'Edit' next to the edited page url");
        By mraEditPageTdBy = By.xpath("//tbody[@id='MyRecentActivity-tbody']/tr[descendant::input[contains(@id,'"+editPageUri+"')]]/td[2]/a");
        CStudioSeleniumUtil.clickOn(driver,mraEditPageTdBy);

        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_PAGE_LOADING);

        logger.info("Edit page");
        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Edit body field (3)");
        editBodyField(updateStringBody3);

        logger.info("Click Save&Preview button and wait for change to complete");
        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndPreview"));

        driver.switchTo().window(mainWindowHandle);

        logger.info("Wait for preview to load");
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_PREVIEW_SYNC).until(new ExpectedCondition<Boolean>() {
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

        logger.info("Check the change took effect");
        assertTrue(updateStringBody3.equals(driver.findElement(By.xpath("/html/body/div[2]/div/div/div[1]/p")).getText()));

        logger.info("Edit Page");
        CStudioSeleniumUtil.switchToEditWindow(driver);

        logger.info("Click Edit Button next to the Page URL field");
        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[@id='file-name']/div/div[3]/input"));

        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_PAGE_LOADING);

        logger.info("Check alert and accept");
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS).until(new ExpectedCondition<Boolean>() {
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

        logger.info("Edit Page URL field");
        WebElement urlInput = driver.findElement(By.xpath("//*[@id='file-name']/div/input"));
        urlInput.clear();
        urlInput.sendKeys(updateStringNewUrlName);
        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Click Save&Preview button and wait for change to complete");
        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndPreview"));

        driver.switchTo().window(mainWindowHandle);

        logger.info("Wait for preview to load");
        new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {
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

        logger.info("Check new URL");
        String url = driver.getCurrentUrl();
        assertTrue(url.endsWith(updateStringNewUrlName));

        logger.info("Edit Page");
        CStudioSeleniumUtil.switchToEditWindow(driver);
        logger.info("Edit Page URL field");
        urlInput = driver.findElement(By.xpath("//*[@id='file-name']/div/input"));
        urlInput.clear();
        urlInput.sendKeys(updateStringOriginalUrlName);
        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Click Save&Close button and wait for change to complete");
        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndClose"));

        CStudioSeleniumUtil.switchToMainWindow(driver);
        CStudioSeleniumUtil.waitFor(1);

        //TODO Info: Edited Page should reload automatically with new URL. Minor Crafter bug!
        logger.info("Check new (original) URL");
        url = driver.getCurrentUrl();
        assertTrue(url.endsWith(updateStringOriginalUrlName));

    }*/

    /*private void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }*/
    /**
     * Looks for the my last edit timestamp for the specified content in My Recent Activity
     * @param contentUri the content uri
     * @return the content MyLastEdit text
     */
    /*private String myRecentActivityMyLastEdit(String contentUri){
        By mraEditedPageTimestampTdBy = By.xpath("//tbody[@id='MyRecentActivity-tbody']/tr[descendant::input[contains(@id,'"+contentUri+"')]]/td[last()]");
        WebElement myRecentActivityEditedPageTimestampTd = driver.findElement(mraEditedPageTimestampTdBy);
        return myRecentActivityEditedPageTimestampTd.getText();
    }*/

    /**
     * Edit the open edit form body field
     * Requires edit form to be open
     * @param update
     */
    /*private void editBodyField(String update) {
        driver.switchTo().frame(0);
        WebElement bodyElement = driver.findElement(By.id("tinymce"));
        bodyElement.clear();
        bodyElement.sendKeys(update);
        CStudioSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);
        driver.switchTo().defaultContent();
    }*/



}