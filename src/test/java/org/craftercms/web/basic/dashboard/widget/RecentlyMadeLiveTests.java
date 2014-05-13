package org.craftercms.web.basic.dashboard.widget;

import org.apache.commons.lang.ArrayUtils;
import org.craftercms.web.basic.dashboard.widget.helpers.CSDashboardWidgetHandler;
import org.craftercms.web.basic.dashboard.widget.helpers.CSRecentlyMadeLiveWidgetHandler;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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

    protected CSDashboardWidgetHandler recentlyMadeLiveWidgetHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        recentlyMadeLiveWidgetHandler = new CSRecentlyMadeLiveWidgetHandler(driver);
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
        By iconSpanBy =By.xpath("//tbody[@id='"+recentlyMadeLiveWidgetHandler.getDivId()+"-tbody']//span[contains(concat(' ',@class,' '),' parent-div-widget ')]");

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
        myRecentActivityWidgetHandler.filterByAll();
        selectAndSubmitContentToGoLiveNow(myRecentActivityWidgetHandler, articlesAndToutsUris);
        //makeContentGoLive(articlesAndToutsUris);

        logger.info("Refresh dashboard");
        CStudioSeleniumUtil.refreshAndWaitForPageToLoad(driver);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Check only pages are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingPagesOnly());

        logger.info("Check the created articles are shown in RML ");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(articlesUris));

        logger.info("Change view option to 'Components' in RML");
        recentlyMadeLiveWidgetHandler.filterByComponents();

        logger.info("Check the created touts are shown in RML ");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(toutsUris));

        logger.info("Check only components are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingComponentsOnly());

        logger.info("Change view option to 'Documents' in RML");
        recentlyMadeLiveWidgetHandler.filterByDocuments();

        logger.info("Check no items are shown in RML");
        assertFalse(recentlyMadeLiveWidgetHandler.hasContents());

        logger.info("Change view option to 'All' in RML");
        recentlyMadeLiveWidgetHandler.filterByAll();

        logger.info("Check multiple content kinds are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.isDisplayingMultipleKindsOfContents());

        logger.info("Check the created articles and touts are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(articlesUris));
        assertTrue(recentlyMadeLiveWidgetHandler.containsContents(toutsUris));

        logger.info("Enter 2 in the show box in RML");
        recentlyMadeLiveWidgetHandler.changeShowNumber(2);

        logger.info("Check only 2 items are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents() == 2);

        logger.info("Enter 10 in the show box in RML");
        recentlyMadeLiveWidgetHandler.changeShowNumber(10);

        logger.info("Check only 10 items are shown in RML");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents() == 10);

        logger.info("Click collapse/expand All' all in RML");
        CStudioSeleniumUtil.clickOn(driver, By.id("expand-all-"+recentlyMadeLiveWidgetHandler.getDivId()));

        logger.info("Check all items are hidden");
        recentlyMadeLiveWidgetHandler.contentsAreHidden();

        logger.info("Check icon changed to '+'");
        iconSpanElements = driver.findElements(iconSpanBy);
        for(WebElement iconSpanElement : iconSpanElements){
            assertTrue(iconSpanElement.getAttribute("class").contains("ttOpen")); //Open icon = '+'
        }

        logger.info("Click collapse/expand All' all in RML");
        CStudioSeleniumUtil.clickOn(driver, By.id("expand-all-" + recentlyMadeLiveWidgetHandler.getDivId()));

        logger.info("Check all (10) items are visible");
        assertTrue(recentlyMadeLiveWidgetHandler.countContents() == 10);

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


}
