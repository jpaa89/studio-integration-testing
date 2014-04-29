package org.craftercms.web.basic;


import org.craftercms.web.BaseTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * @author Juan Avila
 */
public class ICEToolsTests extends BaseTest{

    /**
     * Tests In Context Editing ui behavior
     */
    @Test
    public void iceToolsTest()  {

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        String editPageUrl = seleniumProperties.getProperty("craftercms.base.url") + seleniumProperties.getProperty("craftercms.page.to.edit.url");

        logger.info("Login as author");
        useAuthorUser();
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,dashboardUrl);

        logger.info("Navigate to About page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,editPageUrl);

        logger.info("Click the Preview Tools icon on contextual nav");
        CStudioSeleniumUtil.clickOn(driver, By.id("acn-preview-tools"));

        //CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Click the preview tools panel In-Conext Editing '+' icon");
        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[@id='preview-tools-panel-container']/div[2]/div/div[1]/a"));

        // Preparing the list that holds all the ICEElements (these are actually wrappers so they are always "visible", their content is not always visible)
        final List<WebElement> ICEElements = driver.findElements(By.className("cstudio-ice"));

        logger.info("Click the preview tools panel pencil icon (1)");
        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[@id='preview-tools-panel-container']/div[2]/div/div[2]/div[1]"));

        logger.info("Ensure the ICE pencil icons display on top of the contents after clicking the pencil icon");
        assertTrue(iceContentIconsVisible(ICEElements));

        logger.info("Click the preview tools panel pencil icon (2)");
        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[@id='preview-tools-panel-container']/div[2]/div/div[2]/div[1]"));

        logger.info("Ensure the ICE pencil icons do not display on top of the contents after clicking the pencil icon");
        assertFalse(iceContentIconsVisible(ICEElements));

        logger.info("Click the preview tools panel pencil icon (3)");
        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[@id='preview-tools-panel-container']/div[2]/div/div[2]/div[1]"));

        logger.info("Ensure the ICE pencil icons display on top of the contents after clicking the pencil icon");
        assertTrue(iceContentIconsVisible(ICEElements));

        logger.info("Click the Preview Tools icon on contextual nav (2)");
        CStudioSeleniumUtil.clickOn(driver, By.id("acn-preview-tools"));

        logger.info("Ensure the ICE pencil icons do not display on top of the contents after clicking the pencil icon");
        assertFalse(iceContentIconsVisible(ICEElements));

        logger.info("Ensure the Preview Tools Panel is gone");
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);
        assertFalse(driver.findElement(By.id("preview-tools-panel-container_c")).isDisplayed());

    }

    private void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

    /**
     *
     * @param iceElements
     * @return true if the ice content icons are visible
     */
    private boolean iceContentIconsVisible(final List<WebElement> iceElements) {
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
        boolean displayFlag = false;
        for(WebElement ICEElement : iceElements){
            if(ICEElement.findElement(By.xpath("div[1]")).isDisplayed()){
                displayFlag = true;
            }
            else{
                displayFlag = false;
                break;
            }
        }
        return displayFlag;

    }



}
