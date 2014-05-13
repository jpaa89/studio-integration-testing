/**
 *
 */
package org.craftercms.web.refactoring.util;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Praveen C Elineni
 * @author Juan Avila
 *
 * Utility Class for Selenium
 *
 * TODO because of the refactoring process most of the methods that used to be here will be moved to the handlers
 * TODO some helpful code -meaning that it could be used in the handlers- can be found in the original CStudioSeleniumUtil class
 */
public class CSSeleniumUtil {
    private static final Logger logger = Logger.getLogger(CSSeleniumUtil.class.getName());

    private static final Properties seleniumProperties = new Properties();
    static {
        try {
    	    seleniumProperties.load(CSSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Click on element
     *
     * @param driver
     * @param by
     */
    public static void clickOn(WebDriver driver, By by) {
        waitForItemToDisplay(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        waitForItemToBeEnabled(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        driver.findElement(by).click();
    }

    /**
     * Click on element
     *
     * @param driver
     * @param webElement
     */
    public static void clickOn(WebDriver driver, final WebElement webElement) {
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return webElement.isDisplayed();
            }
        });
        new WebDriverWait(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return webElement.isEnabled();
            }
        });
        webElement.click();
    }

    /**
     * Click on element
     *
     * @param driver
     * @param by
     * @param waitingSecondsElementDisplayed
     * @param waitingSecondsElementEnabled
     */
    public static void clickOn(WebDriver driver, By by, int waitingSecondsElementDisplayed, int waitingSecondsElementEnabled) {
        waitForItemToDisplay(driver, waitingSecondsElementDisplayed, by);
        waitForItemToBeEnabled(driver, waitingSecondsElementEnabled, by);
        driver.findElement(by).click();
    }

    /**
     * Wait for specified item to display
     *
     * @param driver
     * @param timeout
     * @param by
     */
    public static void waitForItemToDisplay(WebDriver driver, int timeout, final By by) {
        new WebDriverWait(driver, timeout).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                List<WebElement> elements = d.findElements(by);
                if (elements.size() == 0)
                    return false;
                return d.findElement(by).isDisplayed();
            }
        });
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

    /**
     * Read File Contents
     *
     * @param filePath
     * @param updatedString
     * @return
     */
    public static boolean readFileContents(String filePath, String updatedString) {
    	boolean result = false;
		try {
			File file = new File(filePath);
	        FileReader reader;
			reader = new FileReader(file);
	        BufferedReader in = new BufferedReader(reader);
	        String string;
	        while ((string = in.readLine()) != null) {
	            if (string.contains(updatedString)) {
	        	  result = true;
	        	  break;
	            }
	        }
	        in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }

    /**
    * Execute a click on element with a Javascript code
    *
    * @param driver
    * @param element
    */
    public static void javascriptClick(WebDriver driver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
    }

    /**
     * Execute a click on element with a Javascript code. Waits for element to be displayed and enabled
     *
     * @param driver
     * @param by
     */
    public static void javascriptClick(WebDriver driver, By by) {
        waitForItemToDisplay(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        waitForItemToBeEnabled(driver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        javascriptClick(driver, driver.findElement(by));
    }

    /**
     * Execute a click on element with a Javascript code. Waits for element to be displayed and enabled according to
     * the given time.
     *
     * @param driver
     * @param by
     * @param waitingSecondsElementDisplayed
     * @param waitingSecondsElementEnabled
     */
    public static void javascriptClick(WebDriver driver, By by, int waitingSecondsElementDisplayed, int waitingSecondsElementEnabled) throws InterruptedException {
        waitForItemToDisplay(driver, waitingSecondsElementDisplayed, by);
        waitForItemToBeEnabled(driver, waitingSecondsElementEnabled, by);
        javascriptClick(driver, driver.findElement(by));
    }

    /**
     * Close and Quit driver connection
     *
     * @param driver
     */
    public static void exit(WebDriver driver) {
        driver.close();
        driver.quit();
    }

    /**
     * Thread sleep
     * @param seconds
     */
    public static void waitFor(int seconds) {
        try{
            Thread.sleep(1000*seconds);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Moves the mouse to the specified location
     * @param webDriver
     * @param by Element locator
     */
    public static void moveMouseTo(WebDriver webDriver, By by) {
        WebElement target = webDriver.findElement(by);
        CSSeleniumUtil.waitForItemToDisplay(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        CSSeleniumUtil.waitForItemToBeEnabled(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        new Actions(webDriver).moveToElement(target).perform();
    }

    public static void sendKeys(WebDriver webDriver, final WebElement inputElement, final String keys){
        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.isDisplayed();
            }
        });
        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.isEnabled();
            }
        });
        inputElement.sendKeys(keys);
        new WebDriverWait(webDriver, (keys.length()/100)+ CSTimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.getText().contains(keys);
            }
        });
    }

    public static void clearAndSendKeys(WebDriver webDriver, final WebElement inputElement, final String keys){
        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.isDisplayed();
            }
        });
        new WebDriverWait(webDriver, CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.isEnabled();
            }
        });
        inputElement.clear();
        inputElement.sendKeys(keys);

        new WebDriverWait(webDriver, (keys.length()/100)+ CSTimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK*2).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return inputElement.getAttribute("value").contains(keys) || inputElement.getText().contains(keys);
            }
        });

    }



}