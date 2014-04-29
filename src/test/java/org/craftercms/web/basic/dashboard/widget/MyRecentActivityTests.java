package org.craftercms.web.basic.dashboard.widget;

import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Juan Avila
 *
 */
public class MyRecentActivityTests extends DashboardWidgetTestsBase {

    /**
     * Tests My Recent Activity Dashboard Widget
     * @throws URISyntaxException
     */
    @Test
    public void myRecentActivityTest() throws URISyntaxException {

        URL resource;
        String articleUri;
        String toutUri;
        String templateUri;
        String toutUpdateString;

        String imageToUploadPath;
        String articleImageUri;

        String articleCreatedTimestamp;
        String articleEditedTimestamp;

        resource = ApprovedScheduledItemsTests.class.getClassLoader().getResource("uploadFiles/test-image.png");

        if(resource == null) {
            throw new AssertionError("Image not found: uploadFiles/test-image.png");
        }

        toutUri = seleniumProperties.getProperty("craftercms.component.to.edit");
        templateUri = "/templates/web/article.ftl";
        toutUpdateString = "Tout Updated "+System.currentTimeMillis();
        imageToUploadPath = resource.toURI().getPath();
        articleImageUri = "static-assets/images/test-image.png";


        logger.info("Login as admin");
        login();

        logger.info("Navigate to the dashboard");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Create an article");
        articleUri = createArticle();

        // Forcing refreshing since the article was created by opening the edit form with a javascript execution
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);

        logger.info("Check the article appears 'My Recent Activity'");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, articleUri));
        articleCreatedTimestamp = myRecentActivityMyLastEdit(articleUri);

        logger.info("Waiting 1 minute before editing the article (so the timestamp changes) ");
        CStudioSeleniumUtil.waitFor(60);

        logger.info("Edit the article content");
        editArticleUploadImageToContent(articleUri, imageToUploadPath);

        logger.info("Check the article appears 'My Recent Activity' dashboard widget with a different timestamp");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, articleUri));
        articleEditedTimestamp = myRecentActivityMyLastEdit(articleUri);
        assertFalse(articleCreatedTimestamp.equals(articleEditedTimestamp));

        logger.info("Edit a component");
        CStudioSeleniumUtil.editAndSaveComponent(driver, toutUri, toutUpdateString);

        logger.info("Check the component doesn't appear in 'My Recent Activity'");
        assertFalse(myRecentActivityWidgetHandler.containsContent(driver, toutUri));

        logger.info("Edit template");
        editTemplate(templateUri);

        logger.info("Check edited template doesn't appear in 'My Recent Activity'");
        assertFalse(myRecentActivityWidgetHandler.containsContent(driver, templateUri));

        logger.info("Change the view option to 'Components' in 'My Recent Activity'");
        myRecentActivityWidgetHandler.filterByComponents(driver);

        logger.info("Check the component appears in 'My Recent Activity'");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, toutUri));

        logger.info("Change the view option to 'Documents' in 'My Recent Activity'");
        myRecentActivityWidgetHandler.filterByDocuments(driver);

        logger.info("Check no items appear in 'My Recent Activity'");
        assertFalse(myRecentActivityWidgetHandler.hasContents(driver));

        logger.info("Change the view option to 'ALl' in 'My Recent Activity'");
        myRecentActivityWidgetHandler.filterByAll(driver);

        logger.info("Check the article image appears in 'My Recent Activity'");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, articleImageUri));

        logger.info("Check the component appears in 'My Recent Activity'");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, toutUri));

        logger.info("Select the article page, click 'Submit to Go Live' and then 'Submit'");
        selectAndSubmitToGoLive(articleUri);

        logger.info("Click 'Hide Live Items'");
        CStudioSeleniumUtil.clickOn(driver, By.id("widget-expand-state-MyRecentActivity"));

        logger.info("Check link changed to 'Show Live Items'");
        assertTrue("Show Live Items".equals(driver.findElement(By.id("widget-expand-state-MyRecentActivity")).getText()));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check Live Items items are not shown'");
        checkNoLiveItemsAppearInMyRecentActivity();

        logger.info("Enter 2 in the Show box and press enter");
        myRecentActivityWidgetHandler.changeShowNumber(driver,2);

        logger.info("Check only 2 items appear in MRA");
        assertTrue(myRecentActivityWidgetHandler.countContents(driver) == 2);

        logger.info("Click 'Show Live Items'");
        CStudioSeleniumUtil.clickOn(driver, By.id("widget-expand-state-MyRecentActivity"));

        logger.info("logout");
        logout();

        logger.info("Login as author");
        useAuthorUser();
        login();

        logger.info("Navigate to the dashboard");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Edit the article content");
        editArticleUploadImageToContent(articleUri, imageToUploadPath);

        logger.info("Check the article appears in MRA");
        assertTrue(myRecentActivityWidgetHandler.containsContent(driver, articleUri));

        logger.info("logout");
        logout();

        logger.info("Login as admin");
        useAdminUser();
        login();

        logger.info("Navigate to the dashboard");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Enter 10 in the Show box and press enter");
        myRecentActivityWidgetHandler.changeShowNumber(driver,10);

        logger.info("Click 'Last Edited By'");
        CStudioSeleniumUtil.clickOn(driver, By.id("sortuserLastName-MyRecentActivity"));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check Activities are listed by user names");
        checkActivitiesListedByUserName();

    }

    /**
     * Looks for the my last edit timestamp for the specified content in My Recent Activity
     * @param contentUri the content uri
     * @return the content MyLastEdit text
     */
    private String myRecentActivityMyLastEdit(String contentUri){
        By mraEditedPageTimestampTdBy = By.xpath("//tbody[@id='MyRecentActivity-tbody']/tr[descendant::input[contains(@id,'"+contentUri+"')]]/td[last()]");
        WebElement myRecentActivityEditedPageTimestampTd = driver.findElement(mraEditedPageTimestampTdBy);
        return myRecentActivityEditedPageTimestampTd.getText();
    }

    /**
     * Checks no live items are being shown in My Recent Activity
     */
    private void checkNoLiveItemsAppearInMyRecentActivity(){
        WebElement myRecentActivityElement = driver.findElement(By.id("MyRecentActivity-tbody"));
        By liveSpanElementsBy = By.xpath("tr[not(@class='avoid')]/td[1]/div[1]/div[1]/span[normalize-space(@class)='status-icon floating']");
        List<WebElement> liveElements = myRecentActivityElement.findElements(liveSpanElementsBy);
        assertTrue(liveElements.isEmpty());
    }

    /**
     * Selects the article uri within My recent Activity and submits it to go live now
     * @param articleUri the articleUri
     */
    private void selectAndSubmitToGoLive(String articleUri) {

        myRecentActivityWidgetHandler.selectContents(driver,new String[]{articleUri});

        driver.manage().window().maximize();
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
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

        //In case a scheduling warning appears
        CStudioSeleniumUtil.clickOn(driver,By.id("globalSetToNow"));

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"));

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        CStudioSeleniumUtil.clickOn(driver, By.id("acnOKButton"));

        CStudioSeleniumUtil.waitForCurrentPageToLoad(driver);

    }

    /**
     * Checks the activities are listed by user name.
     * Calls isSortedDescendingOrder
     */
    private void checkActivitiesListedByUserName(){
        List<WebElement> users = driver.findElements(By.xpath("//*[@id='MyRecentActivity-tbody']/tr[not(@class='avoid')]/td[6]"));
        String[] userNames = new String[users.size()];

        for (int i=0; i < userNames.length; i++){
            userNames[i] = users.get(i).getText();
        }

        assertTrue(isSortedDescendingOrder(userNames));

    }

    /**
     * Returns true if the user names are sorted in descending order
     * @param userNames user names
     * @return true if the user names are sorted
     */
    private boolean isSortedDescendingOrder(String[] userNames) {

        if(userNames == null){
            return false;
        }

        if (userNames.length <= 1) {
            return true;
        }

        String s1 = userNames[0];

        for(int i = 1; i < userNames.length; i++){
            String s2 = userNames[i];
            if(s1.compareTo(s2) < 0){
                return false;
            }
            s1 = s2;
        }

        return true;
    }

    /**
     * Edits an article by uploading an image to its content body
     * @param articleUri the uri of article to edit
     * @param imageToUploadPath the path of the image to upload
     * @throws URISyntaxException
     */
    private void editArticleUploadImageToContent(String articleUri, String imageToUploadPath) throws URISyntaxException {
        //TODO update By locators

        String contentType = seleniumProperties.getProperty("craftercms.page.content.type");

        CStudioSeleniumUtil.editContentJS(driver, articleUri, contentType, siteName);

        CStudioSeleniumUtil.switchToEditWindow(driver);

        CStudioSeleniumUtil.waitForCurrentPageToLoad(driver);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        driver.switchTo().frame(0);
        WebElement tinymceElement = driver.findElement(By.id("tinymce"));
        tinymceElement.clear();
        tinymceElement.click();
        driver.switchTo().defaultContent();

        CStudioSeleniumUtil.clickOn(driver,By.id("mce_0_managedImage"));

        CStudioSeleniumUtil.clickOn(driver,By.xpath("//table[@id='menu_mce_0_mce_0_managedImage_menu_tbl']/tbody/tr/td/a[span[normalize-space(text())='Desktop Images']]") );

        WebElement fileInputElement = driver.findElement(By.id("uploadFileNameId"));
        fileInputElement.sendKeys(imageToUploadPath);
        CStudioSeleniumUtil.waitFor(1); //Wait for keys

        CStudioSeleniumUtil.clickOn(driver, By.id("uploadButton"));

        CStudioSeleniumUtil.waitFor(5); // Wait for image upload

        CStudioSeleniumUtil.clickOn(driver,By.id("cstudioSaveAndClose"));

        CStudioSeleniumUtil.switchToMainWindow(driver);

        // Forcing refreshing since the article was edited by opening the edit form with a javascript execution
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);

    }

    /**
     * Edits a template by adding a Tab space
     * @param uri the uri of the template
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

    /**
     * Creates an article
     * @return the uri of the created article
     */
    public String createArticle(){
        long time = System.currentTimeMillis();
        String path = "/site/website";
        String fileName = "selenium-mra-"+time;
        String title = "Selenium MRA Test "+time;
        String content = "This is a selenium my recent activity test "+time;

        CStudioSeleniumUtil.createArticle(driver,path,fileName,content,title,siteName);

        return path+"/"+fileName+"/index.xml";

    }


}