package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author Juan Avila
 */
public class RTETests extends BaseTest {

    /**
     * Tests Rich Text Editor linking functionality
     */
    @Test
    public void testRTELink() {

        String linkTargetText = "link";
        String linkUrl = "http://craftercms.org/";
        String linkTitle= "Craftercms";

        By tinymceBy = By.id("tinymce");

        By linkTextParagraphBy = By.xpath("p[text()='"+linkTargetText+"']");
        By linkOverlayPanelBy = By.id("link");

        By linkButtonBy = By.xpath("//a[@title='Insert/Edit Link']");
        By unlinkButtonBy = By.xpath("//a[@title='Unlink']");
        By editCodeBy = By.xpath("//a[@title='Edit Code']");

        By linkUrlInputBy = By.id("href");
        By linkTitleInputBy = By.id("linktitle");
        By insertSubmitBy = By.id("insert");

        By linkElementBy = By.xpath("//a[@href='"+linkUrl+"' and @title='"+linkTitle+"']");

        By htmlTextBy = By.xpath("//div[@class='CodeMirror-lines']/div[1]/div[not(@*)]/pre[not(@*)]");

        By formCancelButtonBy = By.xpath("//input[@value='Cancel']");
        By formConfirmCancellationButtonBy = By.xpath("//button[text()='Yes']");

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        logger.info("Login as author");
        useAuthorUser();
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Open a new article form");
        CStudioSeleniumUtil.openNewArticleForm(driver,"/site/website","rtelinktest",linkTargetText,"RTE Link Test",siteName);

        logger.info("Highlight the link text");
        driver.switchTo().frame(0);
        WebElement tinymceElement = driver.findElement(tinymceBy);
        WebElement textElement = tinymceElement.findElement(linkTextParagraphBy);

        int tinymceWidth = tinymceElement.getSize().getWidth();
        int tinymceHeight = tinymceElement.getSize().getHeight()-1;

        new Actions(driver)
                .moveToElement(tinymceElement, 1, 1)
                .click().keyDown(Keys.SHIFT)
                .moveToElement(tinymceElement, tinymceWidth - 1, tinymceHeight - 1)
                .click().keyUp(Keys.SHIFT)
                .perform();

        driver.switchTo().defaultContent();

        logger.info("Click on the link tool");
        CStudioSeleniumUtil.clickOn(driver,linkButtonBy);

        logger.info("Check the link overlay appears");
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
        driver.switchTo().frame(1);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,linkOverlayPanelBy);

        logger.info("Fill out the fields and click 'Insert'");
        WebElement linkUrlInput = driver.findElement(linkUrlInputBy);
        WebElement linkTitleInput = driver.findElement(linkTitleInputBy);
        WebElement insertSubmit = driver.findElement(insertSubmitBy);
        linkUrlInput.sendKeys(linkUrl);
        linkTitleInput.sendKeys(linkTitle);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);
        insertSubmit.click();
        driver.switchTo().defaultContent();

        logger.info("Check the link was created");
        driver.switchTo().frame(0);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,linkElementBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,linkElementBy);

        logger.info("Double-click on the link");

        new Actions(driver).moveToElement(textElement.findElement(linkElementBy)).doubleClick().perform();

        logger.info("Check link overlay appears, now with the link data");
        driver.switchTo().defaultContent();
        driver.switchTo().frame(1);
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,linkOverlayPanelBy);
        linkUrlInput = driver.findElement(linkUrlInputBy);
        linkTitleInput = driver.findElement(linkTitleInputBy);
        assertTrue(linkUrl.equals(linkUrlInput.getAttribute("value")));
        assertTrue(linkTitle.equals(linkTitleInput.getAttribute("value")));

        logger.info("Switch to HTML mode");
        driver.switchTo().defaultContent();
        CStudioSeleniumUtil.clickOn(driver,By.id("formContainer")); //this is just to close the link overlay panel
        driver.switchTo().frame(0);
        CStudioSeleniumUtil.clickOn(driver,tinymceBy); //get the tools panel back
        driver.switchTo().defaultContent();
        CStudioSeleniumUtil.clickOn(driver,editCodeBy); //html mode on

        logger.info("Check html 'a' element was created, with the data entered");
        String linkHtmlText = driver.findElement(htmlTextBy).getText();
        logger.info(linkHtmlText);
        assertTrue(linkHtmlText.contains("<a title=\"" + linkTitle + "\" href=\"" + linkUrl + "\">"+linkTargetText+"</a>"));

        logger.info("Switch back to RTE mode and click the link once");
        CStudioSeleniumUtil.clickOn(driver,editCodeBy); //html mode off

        logger.info("Check 'link' and 'unlink' tools are highlighted in toolbar");

        driver.switchTo().defaultContent(); // because after the click the frame is not the same anymore
        driver.switchTo().frame(0); // because after the click the frame is not the same anymore

        CStudioSeleniumUtil.clickOn(driver,linkElementBy);

        driver.switchTo().defaultContent();

        WebElement linkButtonElement = driver.findElement(linkButtonBy);
        WebElement unlinkButtonElement = driver.findElement(unlinkButtonBy);

        assertTrue(linkButtonElement.getAttribute("class").contains("mceButtonEnabled mceButtonActive"));
        assertTrue(unlinkButtonElement.getAttribute("class").contains("mceButtonEnabled mceButtonActive"));

        logger.info("Click the unlink tool and check the link is removed");
        CStudioSeleniumUtil.clickOn(driver,unlinkButtonBy);
        driver.switchTo().frame(0);
        try {
            CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,linkElementBy);
            throw new AssertionError("The link still exists");
        }  catch (TimeoutException e){ }


        logger.info("Switch to html mode");
        driver.switchTo().defaultContent();
        CStudioSeleniumUtil.clickOn(driver,editCodeBy); //html mode on

        logger.info("Check the link was removed");
        String noLinkHtmlText = driver.findElement(htmlTextBy).getText();
        logger.info(noLinkHtmlText);
        assertTrue(noLinkHtmlText.equals("<p>"+linkTargetText+"</p>"));

        // Canceling the form creation (the test is already finished, this is just to keep everything clean):

        CStudioSeleniumUtil.clickOn(driver, formCancelButtonBy);

        CStudioSeleniumUtil.clickOn(driver,formConfirmCancellationButtonBy);

        CStudioSeleniumUtil.switchToMainWindow(driver);

    }
    /**
     * Tests Rich Text Editor html mode functionality
     */
    @Test
    public void testRTEHtmlMode() throws InterruptedException {
        By tinymceBy = By.id("tinymce");
        By editCodeBy = By.xpath("//a[@title='Edit Code']");

        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);

        logger.info("Login as author");
        useAuthorUser();
        login();

        logger.info("Navigate to Dashboard page");
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver, dashboardUrl);

        logger.info("Open a new article form and enter a long content in the body field (RTE)");
        String text = "The quick brown fox jumps over the lazy dog. ";
        StringBuilder stringBuffer = new StringBuilder();
        for(int i = 0; i < 50; i++){
            stringBuffer.append(text);
        }
        CStudioSeleniumUtil.openNewArticleForm(driver,"/site/website","rtehtmlmodetest",stringBuffer.toString(),"RTE HTML Mode Test",siteName);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Move cursor to the bottom");
        driver.switchTo().frame(0);
        WebElement tinymceElement = driver.findElement(tinymceBy);
        int tinymceWidth = tinymceElement.getSize().getWidth();
        int tinymceHeight = tinymceElement.getSize().getHeight()-1;
        new Actions(driver).moveToElement(tinymceElement,tinymceWidth-1,tinymceHeight-1).click().perform();
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Switch to html mode");
        driver.switchTo().defaultContent();
        CStudioSeleniumUtil.clickOn(driver,editCodeBy); //html mode on

        logger.info("Check html mode opens in full width");
        By rteBodyTableBy = By.xpath("//div[@id='body_html']//table[1]");
        WebElement rteBodyTable = driver.findElement(rteBodyTableBy);
        assertTrue(rteBodyTable.getAttribute("style").replace(" ","").contains("width:auto"));

        // workaround to later determine where the cursor was:
        By textEditorHtmlDivBy = By.xpath("//div[@class='CodeMirror-lines'][1]//div[not(@*)][1]");
        new Actions(driver).sendKeys("<p>Html-ON-1-Beginning</p>").perform();
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Check cursor was at the beginning");
        WebElement textEditorHtmlDiv = driver.findElement(textEditorHtmlDivBy);
        String editorText = textEditorHtmlDiv.getText();
        assertTrue(editorText.startsWith("<p>Html-ON-1-Beginning</p>"));

        logger.info("Move cursor to the bottom");
        int textEditorHtmlDivWidth = textEditorHtmlDiv.getSize().getWidth();
        int textEditorHtmlDivHeight = textEditorHtmlDiv.getSize().getHeight()-1;
        new Actions(driver).moveToElement(textEditorHtmlDiv,textEditorHtmlDivWidth-1,textEditorHtmlDivHeight-1).click().perform();

        logger.info("Switch to RTE mode");
        CStudioSeleniumUtil.clickOn(driver,editCodeBy); //html mode off

        logger.info("Check RTE mode opens with a specific width");
        rteBodyTableBy = By.xpath("//div[@id='body_html']//table[1]");
        rteBodyTable = driver.findElement(rteBodyTableBy);
        assertTrue(rteBodyTable.getAttribute("style").replace(" ","").contains("width:650px"));

        // workaround to later determine where the cursor was:
        driver.switchTo().frame(0);
        new Actions(driver).sendKeys("Html-OFF-2-Beginning\n").perform();

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        logger.info("Check cursor was at the beginning");
        tinymceElement = driver.findElement(tinymceBy);
        editorText = tinymceElement.getText();
        assertTrue(editorText.startsWith("Html-OFF-2-Beginning\n"));

        driver.switchTo().defaultContent();

        CStudioSeleniumUtil.clickOn(driver, By.xpath("//input[@value='Cancel']"));

        CStudioSeleniumUtil.clickOn(driver, By.xpath("//button[text()='Yes']"));

        CStudioSeleniumUtil.switchToMainWindow(driver);

    }


    private void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

}
