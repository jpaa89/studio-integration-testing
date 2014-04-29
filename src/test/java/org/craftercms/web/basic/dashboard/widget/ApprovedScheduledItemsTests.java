package org.craftercms.web.basic.dashboard.widget;

import org.craftercms.web.helpers.DashboardWidgetHandler;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Juan Avila
 */
public class ApprovedScheduledItemsTests extends DashboardWidgetTestsBase {

    protected DashboardWidgetHandler approvedScheduledItemsWidgetHandler;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        approvedScheduledItemsWidgetHandler = new DashboardWidgetHandler("approvedScheduledItems");
    }

    /**
     * Tests the Approved Scheduled Items Dashboard Widget
     * @throws URISyntaxException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void approvedScheduledItemsTest() throws URISyntaxException, IOException, SAXException, ParserConfigurationException {

        URL resource = ApprovedScheduledItemsTests.class.getClassLoader().getResource("uploadFiles/author-test-image.png");

        if(resource == null) {
            throw new AssertionError("Image not found: uploadFiles/author-test-image.png");
        }

        String imageToUploadPath = resource.toURI().getPath();
        String pageToEditUri = seleniumProperties.getProperty("craftercms.page.to.edit");
        String componentToEditUri = seleniumProperties.getProperty("craftercms.component.to.edit");
        String templateToEditUri = "/templates/web/article.ftl";
        String imageUri = "/static-assets/images/author-test-image.png";

        logger.info("Login as admin");
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Edit a chosen page");
        editPageReplaceImage(pageToEditUri,imageToUploadPath);

        logger.info("Edit a chosen component (a tout)");
        editComponent(componentToEditUri);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Change My Recent Activity View to 'All'");
        myRecentActivityWidgetHandler.filterByAll(driver);

        logger.info("Check edited page appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, pageToEditUri);

        logger.info("Check uploaded image appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, imageUri);

        logger.info("Check edited component appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, componentToEditUri);

        logger.info("Select edited page, component and image, click 'Go Live Now' and schedule them to go live on a future date");
        scheduleContentGoLive(new String[]{pageToEditUri, componentToEditUri, imageUri}, "11:55:59 p.m.");

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check only pages appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingPagesOnly(driver));

        logger.info("Check the edited page appears in the Approved Scheduled Items");
        checkContentAppearsInWidget(approvedScheduledItemsWidgetHandler, pageToEditUri);

        logger.info("Edit a template");
        editTemplate(templateToEditUri);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Select edited template, click 'Go Live Now' and schedule it to go live on a future date (other than the previews one");
        scheduleContentGoLive(new String[]{templateToEditUri}, "11:59:59 p.m.");

        logger.info("Check only pages appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingPagesOnly(driver));

        logger.info("Change Approved Scheduled Items View to 'Components'");
        approvedScheduledItemsWidgetHandler.filterByComponents(driver);

        logger.info("Check only components appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingComponentsOnly(driver));

        logger.info("Check the edited component appears in the Approved Scheduled Items");
        checkContentAppearsInWidget(approvedScheduledItemsWidgetHandler, componentToEditUri);

        logger.info("Change Approved Scheduled Items View to 'Documents'");
        approvedScheduledItemsWidgetHandler.filterByDocuments(driver);

        logger.info("Check no items are shown in Approved Scheduled Items");
        assertFalse(approvedScheduledItemsWidgetHandler.hasContents(driver));

        logger.info("Change Approved Scheduled Items View to 'ALL'");
        approvedScheduledItemsWidgetHandler.filterByAll(driver);

        logger.info("Check the edited page, the image, the component and template appear in the Approved Scheduled Items");
        checkContentsAppearsInWidget(approvedScheduledItemsWidgetHandler, new String[]{pageToEditUri,componentToEditUri,imageUri,templateToEditUri});

    }

    /**
     * Checks if the widget contains the specified contents.
     * Calls the widget containsContents method
     * @param dashboardWidgetHandler the widget handler to look for the contents
     * @param contentUris contents uris to check
     */
    private void checkContentsAppearsInWidget(DashboardWidgetHandler dashboardWidgetHandler, String[] contentUris){
        assertTrue(dashboardWidgetHandler.containsContents(driver, contentUris));
    }

    /**
     * Checks if the widget contains the specified contents.
     * @param dashboardWidgetHandler the widget handler to look for the contents
     * @param contentUri contents uri to check
     */
    private void checkContentAppearsInWidget(DashboardWidgetHandler dashboardWidgetHandler, String contentUri){
        assertTrue(dashboardWidgetHandler.containsContent(driver, contentUri));
    }

    /**
     * Edits the page by replacing the author's image
     * @param uri the page uri
     * @param imageToUploadPath path of the author image to upload
     * @throws URISyntaxException
     */
    private void editPageReplaceImage(String uri, String imageToUploadPath) throws URISyntaxException {
        By authorImageButton;
        WebElement fileInputElement;
        String pageContentType = seleniumProperties.getProperty("craftercms.page.content.type");

        authorImageButton = By.xpath("//div[@id='authorImage']//input[@value='Replace']");

        CStudioSeleniumUtil.openAndSwitchToEditForm(driver, uri, pageContentType, siteName);
        CStudioSeleniumUtil.javascriptClick(driver,authorImageButton);

        fileInputElement = driver.findElement(By.id("uploadFileNameId"));
        fileInputElement.sendKeys(imageToUploadPath);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK); //Wait for keys

        CStudioSeleniumUtil.clickOn(driver,By.id("uploadButton"));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS); // Wait for image upload

        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndClose"));

        CStudioSeleniumUtil.switchToMainWindow(driver);

    }

    /**
     * Edits the component internal file name.
     * @param uri the component uri
     */
    private void editComponent(String uri) {
        CStudioSeleniumUtil.editAndSaveComponent(driver, uri, "Component-Updated: " + System.currentTimeMillis());
    }

    /**
     * Edits the template by adding a Tab space
     * @param uri the template uri
     */
    private void editTemplate(String uri) {

        String templateName;
        By templateBy;

        templateName = uri.substring(uri.lastIndexOf("/")+1,uri.length());
        templateBy = By.xpath("//div[contains(@class,'templates-tree')]//div[@class='ygtvitem']//span[text()='"+templateName+"' or text()='"+templateName+"*'][1]");


        CStudioSeleniumUtil.ensureTemplatesTreeIsExpanded(driver);
        CStudioSeleniumUtil.contextMenuOption(driver,"Edit",driver.findElement(templateBy));

        final By emptyPreBy = By.xpath("//*[@id='editor-container']//pre[not(@*) and not(child::*)]");

        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try{
                    WebElement emptyPre = driver.findElement(emptyPreBy);
                    new Actions(driver).moveToElement(emptyPre).click().sendKeys(Keys.TAB).perform();
                    return true;
                }
                catch (StaleElementReferenceException e){
                    return false;
                }
            }
        });

        CStudioSeleniumUtil.clickOn(driver, By.id("template-editor-update-button"));
        CStudioSeleniumUtil.waitForCurrentPageToLoad(driver);

    }

    //TODO scheduling and making content go live should be independent methods at some moment, maybe static methods in CStudioSeleniumUtil or in a base test
    /**
     * Schedules the given uris to go live today at the given time.
     * @param uris content uris to be scheduled
     * @param timePickerTime Time format example: "11:59:59 p.m."
     */
    private void scheduleContentGoLive(String[] uris, String timePickerTime) {

        driver.manage().window().maximize();
        myRecentActivityWidgetHandler.selectContents(driver,uris);

        driver.manage().window().maximize();
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT*3).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try{
                    logger.info("Click 'Go Live Now'");
                    CStudioSeleniumUtil.clickOn(driver,By.xpath("//a[text()='Go Live Now']"));
                    return true;
                }
                catch (StaleElementReferenceException e){
                    return false;
                }
            }
        });

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        logger.info("Setting date and time fields");

        //enabling scheduling controls
        CStudioSeleniumUtil.clickOn(driver,By.id("globalSetToDateTime"));


        //TODO expand this approach to other methods
        //remove readonly from the datepicker input
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("document.getElementById('datepicker').readOnly = false");

        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id("datepicker"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("datepicker"));
        WebElement datepicker = driver.findElement(By.id("datepicker"));

        try{
            datepicker.clear();
            datepicker.sendKeys("12/12/2099\n"); // Ensure time is after now.
        }
        catch(UnhandledAlertException e){
            Alert alert = driver.switchTo().alert(); // (  ) is not a valid date format, please provide a valid time
            alert.accept();
            datepicker.clear();
            datepicker.sendKeys("12/12/2121\n"); // Ensure time is after now.
        }

        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id("timepicker"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("timepicker"));
        WebElement timepicker = driver.findElement(By.id("timepicker"));

        try{
            timepicker.clear();
            timepicker.sendKeys(timePickerTime+"\n"); // Ensure time is after now.
        }
        catch(UnhandledAlertException e){
            Alert alert = driver.switchTo().alert(); // (  ) is not a valid time format, please provide a valid time
            alert.accept();
            timepicker.clear();
            timepicker.sendKeys(timePickerTime+"\n"); // Ensure time is after now.
        }

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Confirm Schedule");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));

        logger.info("Waiting for item to get scheduled...");
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_DEPLOY);

    }

}
