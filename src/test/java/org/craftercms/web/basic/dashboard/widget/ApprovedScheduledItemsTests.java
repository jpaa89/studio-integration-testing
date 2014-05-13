package org.craftercms.web.basic.dashboard.widget;


import org.craftercms.web.basic.dashboard.widget.helpers.CSApprovedScheduledItemsWidgetHandler;
import org.craftercms.web.basic.dashboard.widget.helpers.CSDashboardWidgetHandler;
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

    protected CSDashboardWidgetHandler approvedScheduledItemsWidgetHandler;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        approvedScheduledItemsWidgetHandler = new CSApprovedScheduledItemsWidgetHandler(driver);
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
        myRecentActivityWidgetHandler.filterByAll();

        logger.info("Check edited page appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, pageToEditUri);

        logger.info("Check uploaded image appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, imageUri);

        logger.info("Check edited component appears in My Recent Activity");
        checkContentAppearsInWidget(myRecentActivityWidgetHandler, componentToEditUri);

        logger.info("Select edited page, component and image, click 'Go Live Now' and schedule them to go live on a future date");
        selectAndSubmitContentToGoLiveOnSchedule(myRecentActivityWidgetHandler, new String[]{pageToEditUri, componentToEditUri, imageUri}, "12/12/2099", "11:55:59 p.m.");
        //scheduleContentGoLive(new String[]{pageToEditUri, componentToEditUri, imageUri}, "11:55:59 p.m.");

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check only pages appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingPagesOnly());

        logger.info("Check the edited page appears in the Approved Scheduled Items");
        checkContentAppearsInWidget(approvedScheduledItemsWidgetHandler, pageToEditUri);

        logger.info("Edit a template");
        editTemplate(templateToEditUri);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Select edited template, click 'Go Live Now' and schedule it to go live on a future date (other than the previews one");
        //scheduleContentGoLive(new String[]{templateToEditUri}, "11:59:59 p.m.");
        selectAndSubmitContentToGoLiveOnSchedule(myRecentActivityWidgetHandler, new String[]{templateToEditUri}, "12/12/2099", "11:59:59 p.m.");

        logger.info("Check only pages appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingPagesOnly());

        logger.info("Change Approved Scheduled Items View to 'Components'");
        approvedScheduledItemsWidgetHandler.filterByComponents();

        logger.info("Check only components appear in Approved Scheduled Items");
        assertTrue(approvedScheduledItemsWidgetHandler.isDisplayingComponentsOnly());

        logger.info("Check the edited component appears in the Approved Scheduled Items");
        checkContentAppearsInWidget(approvedScheduledItemsWidgetHandler, componentToEditUri);

        logger.info("Change Approved Scheduled Items View to 'Documents'");
        approvedScheduledItemsWidgetHandler.filterByDocuments();

        logger.info("Check no items are shown in Approved Scheduled Items");
        assertFalse(approvedScheduledItemsWidgetHandler.hasContents());

        logger.info("Change Approved Scheduled Items View to 'ALL'");
        approvedScheduledItemsWidgetHandler.filterByAll();

        logger.info("Check the edited page, the image, the component and template appear in the Approved Scheduled Items");
        checkContentsAppearsInWidget(approvedScheduledItemsWidgetHandler, new String[]{pageToEditUri,componentToEditUri,imageUri,templateToEditUri});

    }

    /**
     * Checks if the widget contains the specified contents.
     * Calls the widget containsContents method
     * @param csDashboardWidgetHandler the widget handler to look for the contents
     * @param contentUris contents uris to check
     */
    private void checkContentsAppearsInWidget(CSDashboardWidgetHandler csDashboardWidgetHandler, String[] contentUris){
        assertTrue(csDashboardWidgetHandler.containsContents(contentUris));
    }

    /**
     * Checks if the widget contains the specified contents.
     * @param csDashboardWidgetHandler the widget handler to look for the contents
     * @param contentUri contents uri to check
     */
    private void checkContentAppearsInWidget(CSDashboardWidgetHandler csDashboardWidgetHandler, String contentUri){
        assertTrue(csDashboardWidgetHandler.containsContent(contentUri));
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

}
