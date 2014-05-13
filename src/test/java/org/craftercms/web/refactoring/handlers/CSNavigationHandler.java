package org.craftercms.web.refactoring.handlers;

import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Juan Avila
 */
public class CSNavigationHandler extends CSHandler {

    protected String previousWindowHandle;

    public CSNavigationHandler(WebDriver webDriver) {
        super(webDriver);
    }

    public String currentPageUrl(){
        return webDriver.getCurrentUrl();
    }

    public void navigateTo(String url){
        webDriver.navigate().to(url);
        waitForCurrentPageToLoad();
    }

    public void waitForCurrentPageToLoad(){
        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_PAGE_LOADING).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriverParam) {
                return ((JavascriptExecutor) webDriverParam).executeScript("return document.readyState").equals("complete");
            }
        });

        // Arbitrary waiting time (to give light javascript tasks -if any- a chance)
        CSSeleniumUtil.waitFor(CSTimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
    }

    public boolean isCurrentPageALoginPage(){
        return (webDriver.findElements(By.id("alflogin")).size() > 0);
    }

    public boolean isCurrentPage(String url){
        return currentPageUrl().equals(url);
    }

    public void refreshCurrentPage(){
        webDriver.navigate().refresh();
        waitForCurrentPageToLoad();
    }

    public void switchToRecentlyOpenWindow(){

        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                return webDriver.getWindowHandles().size() > 1;
            }
        });

        previousWindowHandle = webDriver.getWindowHandle();

        for (String windowHandle : webDriver.getWindowHandles()) {
            webDriver.switchTo().window(windowHandle);
        }

        waitForCurrentPageToLoad();

    }

    public void switchBackToPreviousWindow(){
        webDriver.switchTo().window(previousWindowHandle);
    }

    public void switchToAlertAndAccept(){
        webDriver.switchTo().alert().accept();
    }

    public void switchToAlertAndDismiss(){
        webDriver.switchTo().alert().dismiss();
    }

    public String getCurrentWindowHandler(){
        return webDriver.getWindowHandle();
    }

    public String switchTo(String windowHandler){
        previousWindowHandle = webDriver.getWindowHandle();
        return webDriver.getWindowHandle();
    }



}
