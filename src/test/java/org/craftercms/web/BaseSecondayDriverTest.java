package org.craftercms.web;

import org.apache.commons.io.FileUtils;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

/**
 * @author Juan Avila
 * Holds a secondary driver.
 * This could be useful when a test requieres to open a new independent window to test scenarios
 * that have to do with handling two different users.
 *
 * Warning: This works using chromedriver (other drivers not tested yet) because chromedriver creates
 * a new profile directory each time it launches chrome by default.
 */
public class BaseSecondayDriverTest extends BaseTest {

    protected WebDriver secondaryDriver;
    protected static DesiredCapabilities secondaryDriverDesiredCapabilities;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        initializeSecondaryDriver();
        secondaryDriver.manage().window().maximize();
    }

    protected void initializeSecondaryDriver() {
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("takesScreenshot", true);
        secondaryDriver = new ChromeDriver(desiredCapabilities);
    }

    @Override
    public void failed(Throwable e, Description description) {
        System.out.println("TEST CASE FAILED");
        try {
            File shoot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(shoot, new File(getScreenshotOutputFolder() + File.separator + description.getMethodName() + ".png"));

        } catch (IOException ex) {
            logger.info("Unable to save driver screenshot");
        }

        try {
            File shoot = ((TakesScreenshot) secondaryDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(shoot, new File(getScreenshotOutputFolder() + File.separator + description.getMethodName() + ".png"));

        } catch (IOException ex) {
            logger.info("Unable to save secondary driver screenshot");
        }
    }

    @Override
    protected void finished(Description description) {
        driver.quit();
        secondaryDriver.quit();
    }

    @After
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
        CStudioSeleniumUtil.exit(secondaryDriver);
    }

    protected void loginUsingSecondaryDriver() {
        logger.info("Login as '" + getUsername() + "'");
        CStudioSeleniumUtil.tryLogin(secondaryDriver,
                getUsername(),
                getPassword(),
                true);
    }

    protected void logoutUsingSecondaryDriver() {
        logger.info("Logout");
        CStudioSeleniumUtil.tryLogout(secondaryDriver);
    }
}
