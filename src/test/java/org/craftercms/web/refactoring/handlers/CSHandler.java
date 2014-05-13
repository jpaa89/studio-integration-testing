package org.craftercms.web.refactoring.handlers;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Juan Avila
 */
public abstract class CSHandler {

    protected static final Properties seleniumProperties = new Properties();
    static {
        try {
            seleniumProperties.load(CStudioSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected WebDriver webDriver;

    public CSHandler(WebDriver webDriver){
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    protected boolean isDisplayed(int secondsToWait, final By by){

        boolean isDisplayed = false;

        WebDriverWait webDriverWait = new WebDriverWait(webDriver, secondsToWait);

        try {
            isDisplayed = new WebDriverWait(webDriver, secondsToWait).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    List<WebElement> elements = d.findElements(by);
                    if (elements.size() == 0){
                        return false;
                    }
                    return d.findElement(by).isDisplayed();
                }
            });
        }
        catch (ElementNotFoundException ignore){}
        catch (TimeoutException ignore){}

        return isDisplayed;

    }

    protected boolean isEnabled(int secondsToWait, final By by){

        boolean isDisplayed = false;

        WebDriverWait webDriverWait = new WebDriverWait(webDriver, secondsToWait);

        try {
            isDisplayed = new WebDriverWait(webDriver, secondsToWait).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    List<WebElement> elements = d.findElements(by);
                    if (elements.size() == 0){
                        return false;
                    }
                    return d.findElement(by).isEnabled();
                }
            });
        }
        catch (ElementNotFoundException ignore){}
        catch (TimeoutException ignore){}

        return isDisplayed;

    }

    /**
     * Wait for specified item to display
     *
     * @param driver
     * @param timeout
     * @param by
     */
    public static void waitForItemToDisplay(WebDriver driver, int timeout, final By by) {

    }

    /**
     * Wait for specified item to be enabled
     *
     * @param driver
     * @param timeout
     * @param by
     */
    public static void waitForItemToBeEnabled(WebDriver driver, int timeout, final By by) {
        if (!driver.findElement(by).isEnabled())
            new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return d.findElement(by).isEnabled();
                }
            });
    }

}
