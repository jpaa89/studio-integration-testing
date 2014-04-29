package org.craftercms.web.basic.dashboard.widget;

import org.apache.commons.lang.ArrayUtils;
import org.craftercms.web.helpers.DashboardWidgetHandler;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

/**
 * @author Juan Avila
 */
public class RecentlyMadeLiveTests extends DashboardWidgetTestsBase {

    protected DashboardWidgetHandler recentlyMadeLiveWidgetHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        recentlyMadeLiveWidgetHandler = new DashboardWidgetHandler("recentlyMadeLive");
    }

    /**
     * Tests the Recently Made Live Dashboard Widget
     * @throws InterruptedException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void recentlyMadeLiveTest() throws InterruptedException, IOException, SAXException, ParserConfigurationException {

        List<WebElement> iconSpanElements;
        By iconSpanBy =By.xpath("//tbody[@id='"+recentlyMadeLiveWidgetHandler.getId()+"-tbody']//span[contains(concat(' ',@class,' '),' parent-div-widget ')]");

        logger.info("Login as admin");
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Create 5 articles");
        String[] articlesUris = createArticles(5);

        logger.info("Create 5 touts");
        String[] toutsUris = createTouts(5);

        String articlesAndToutsUris[] = (String[]) ArrayUtils.addAll(articlesUris, toutsUris);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Make content go live");
        makeContentGoLive(articlesAndToutsUris);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check only pages are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingPagesOnly(driver));

        logger.info("Check the created articles are shown in RML ");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(driver, articlesUris));

        logger.info("Change view option to 'Components' in RML");
        recentlyMadeLiveWidgetHandler.filterByComponents(driver);

        logger.info("Check the created touts are shown in RML ");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(driver,toutsUris));

        logger.info("Check only components are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingComponentsOnly(driver));

        logger.info("Change view option to 'Documents' in RML");
        recentlyMadeLiveWidgetHandler.filterByDocuments(driver);

        logger.info("Check no items are shown in RML");
        assertFalse(recentlyMadeLiveWidgetHandler.hasContents(driver));

        logger.info("Change view option to 'All' in RML");
        recentlyMadeLiveWidgetHandler.filterByAll(driver);

        logger.info("Check multiple content kinds are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingMultipleKindsOfContents(driver));

        logger.info("Check the created articles and touts are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(driver, articlesUris));
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(driver, toutsUris));

        logger.info("Enter 2 in the show box in RML");
        recentlyMadeLiveWidgetHandler.changeShowNumber(driver,2);

        logger.info("Check only 2 items are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents(driver) == 2);

        logger.info("Enter 10 in the show box in RML");
        recentlyMadeLiveWidgetHandler.changeShowNumber(driver,10);

        logger.info("Check only 10 items are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents(driver) == 10);

        logger.info("Click collapse/expand All' all in RML");
        CStudioSeleniumUtil.clickOn(driver, By.id("expand-all-"+recentlyMadeLiveWidgetHandler.getId()));

        logger.info("Check all items are hidden");
        recentlyMadeLiveWidgetHandler.contentsAreHidden(driver);

        logger.info("Check icon changed to '+'");
        iconSpanElements = driver.findElements(iconSpanBy);
        for(WebElement iconSpanElement : iconSpanElements){
            assertTrue(iconSpanElement.getAttribute("class").contains("ttOpen")); //Open icon = '+'
        }

        logger.info("Click collapse/expand All' all in RML");
        CStudioSeleniumUtil.clickOn(driver, By.id("expand-all-" + recentlyMadeLiveWidgetHandler.getId()));

        logger.info("Check all (10) items are visible");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents(driver) == 10);

        logger.info("Check icon changed to '-'");
        iconSpanElements = driver.findElements(iconSpanBy);
        for(WebElement iconSpanElement : iconSpanElements){
            assertTrue(iconSpanElement.getAttribute("class").contains("ttClose")); //Close icon = '-'
        }



    }

    /**
     * Creates the given number of articles using generated fields.
     * @param number number of articles to be created
     * @return the uris of created articles
     */
    private String[] createArticles(int number){
        String titlePrefix = "Selenium RML Test ";
        String path = "/site/website";
        String urlPrefix = "selenium-rmlt-";
        String content = "This is a selenium recently made live test.";
        return CStudioSeleniumUtil.createArticlesWithinPath(driver, number, path, urlPrefix, content, titlePrefix, siteName);
    }

    /**
     * Creates the given number of touts using generated fields.
     * @param number number of touts to be created
     * @return the uris of created touts
     */
    private String[] createTouts(int number){
        String path = "/site/components/touts";
        String toutUrlPrefix = "selenium-rmlt-";
        String toutInternalNamePrefix = "Selenium RML Test ";
        String toutHeadline = "This is a selenium recently made live test";
        return CStudioSeleniumUtil.createTautsWithinPath(driver,number,path,toutUrlPrefix,toutInternalNamePrefix,toutHeadline,siteName);
    }


    /**
     * Submits the given contents to go live now.
     * @param uris the content uris
     */
    private void makeContentGoLive(final String[] uris) {

        myRecentActivityWidgetHandler.filterByAll(driver);

        myRecentActivityWidgetHandler.selectContents(driver, uris);

        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length*5).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try{
                    logger.info("Click 'Go Live Now'");
                    CStudioSeleniumUtil.clickOn(driver,By.xpath("//a[text()='Go Live Now']"),
                            TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length,
                            TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length);
                    return true;
                }
                catch (StaleElementReferenceException e){
                    return false;
                }
            }
        });

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        //In case a scheduling warning appears
        CStudioSeleniumUtil.clickOn(driver,By.id("globalSetToNow"));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        logger.info("Confirm by Clicking 'Go Live'");
        CStudioSeleniumUtil.clickOn(driver, By.id("golivesubmitButton"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Ok");
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#submitPanel input[value='OK']"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*uris.length);


        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_DEPLOY*3);

    }





}
