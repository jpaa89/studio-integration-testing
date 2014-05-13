package org.craftercms.web.refactoring.handlers.editors;

import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.craftercms.web.util.TimeConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author Juan Avila
 */
public abstract class CSComponentEditorHandler extends CSContentEditorHandler {

    private String siteName;

    public CSComponentEditorHandler(WebDriver webDriver, String siteName, String contentType) {
        super(webDriver, contentType);
        this.siteName = siteName;
    }

    public void openNewFormJS(String path){

        String script;
        JavascriptExecutor js;
        final int windowHandlerCountBeforeNewWindow;

        windowHandlerCountBeforeNewWindow = webDriver.getWindowHandles().size();

        script = "CStudioAuthoring.Operations.createNewContent('"+siteName+"','"+path+"',false,{success: function(){}, failure: function(){}});";

        js = (JavascriptExecutor) webDriver;
        js.executeScript(script);

        CSSeleniumUtil.clickOn(webDriver, By.cssSelector("option[value='" + contentType + "']"));
        CSSeleniumUtil.clickOn(webDriver, By.id("submitWCMPopup"));

        new WebDriverWait(webDriver,CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.getWindowHandles().size() > windowHandlerCountBeforeNewWindow;
            }
        });


    }

    public void openEditFormJS(){

        String script;
        JavascriptExecutor js;
        final int windowHandlerCountBeforeNewWindow;

        windowHandlerCountBeforeNewWindow = webDriver.getWindowHandles().size();

        script = "CStudioAuthoring.Operations.editContent(" +
                "'"+contentType+"','"+siteName+"','"+getUri()+"','','"+getUri()+"',false,{success: function(){}, failure: function(){}});";

        js = (JavascriptExecutor) webDriver;
        js.executeScript(script);

        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = webDriver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if(continueButton.size() > 0) {
            continueButton.get(0).click();
        }

        new WebDriverWait(webDriver,CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return webDriver.getWindowHandles().size() > windowHandlerCountBeforeNewWindow;
            }
        });

    }

    public void editFileNameField(String fileName, boolean clear){
        By fileNameBy = By.cssSelector("div#file-name .datum");
        WebElement fileNameInput = webDriver.findElement(fileNameBy);
        setTextInputField(fileNameInput, fileName, clear);
    }

    public void editInternalNameField(String internalName, boolean clear){
        By internalNameBy = By.cssSelector("div#internal-name .datum");
        WebElement internalNameInput = webDriver.findElement(internalNameBy);
        setTextInputField(internalNameInput, internalName, clear);
    }

    public void saveAndClose(){
        CSSeleniumUtil.clickOn(webDriver, By.id("cstudioSaveAndClose"));
    }

    public void cancel(){
        List<WebElement> confirmationButton;

        CSSeleniumUtil.clickOn(webDriver, By.xpath("//input[@value='Cancel']"));

        confirmationButton = webDriver.findElements(By.xpath("//button[text()='Yes']"));

        if (confirmationButton.size() > 0) {
            confirmationButton.get(0).click();
        }

    }

    protected void setTextInputField(WebElement elementInput, String text, boolean clear){
        if(clear){
            CSSeleniumUtil.clearAndSendKeys(webDriver, elementInput, text);
        }
        else{
            CSSeleniumUtil.sendKeys(webDriver, elementInput, text);
        }
    }

}
