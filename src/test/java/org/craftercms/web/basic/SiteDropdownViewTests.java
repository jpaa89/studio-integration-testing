package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Juan Avila
 *
 */
public class SiteDropdownViewTests extends BaseTest{

    /**
     * Tests site dropdown functionality
     */
    @Test
    public void siteDropdownViewTest() {

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        String userDashboardUrl;

        logger.info("Login as author");
        useAuthorUser();
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        logger.info("Open the site dropdown");
        CStudioSeleniumUtil.clickOn(driver, By.id("acn-dropdown-toggler"));

        logger.info("Select 'View: My Dashboard'");
        CStudioSeleniumUtil.clickOn(driver, By.xpath("//*[@id='acn-site-dropdown']/option[text()='View: My Dashboard']"));

        CStudioSeleniumUtil.waitForCurrentPageToLoad(driver);

        userDashboardUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), getUsername());
        assertEquals(userDashboardUrl,driver.getCurrentUrl());
    }

    private void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

}
