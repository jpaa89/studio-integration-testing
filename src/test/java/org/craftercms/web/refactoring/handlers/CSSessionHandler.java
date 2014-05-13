package org.craftercms.web.refactoring.handlers;

import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


/**
 * @author Juan Avila
 */
public class CSSessionHandler extends CSHandler {


    //TODO Removal explanation: it is better to keep every handler functionality unaware of any external configuration.
    /*private String adminUserName;
    private String adminPassword;
    private String authorUserName;
    private String authorPassword;*/


    public CSSessionHandler(WebDriver webDriver){
        super(webDriver);
        /*adminUserName = seleniumProperties.getProperty("craftercms.admin.username");
        adminPassword = seleniumProperties.getProperty("craftercms.admin.password");
        authorUserName = seleniumProperties.getProperty("craftercms.author.username");
        authorPassword = seleniumProperties.getProperty("craftercms.author.password");*/
    }

    /*public void loginAsAdmin(){
        login(adminUserName,adminPassword);
    }

    public void loginAsAuthor(){
        login(authorUserName,authorPassword);
    } */

    public void login(String userName, String password){
        By userNameBy;
        By passwordBy;
        By loginBy = By.xpath("//*[text()='Login' or @value='Login']"); // 2.4 or 2.3
        WebElement userNameInput;
        WebElement passwordInput;

        userNameBy = By.name("username");
        passwordBy = By.name("password");

        userNameInput = webDriver.findElement(userNameBy);
        passwordInput = webDriver.findElement(passwordBy);

        CSSeleniumUtil.clickOn(webDriver, userNameInput);
        CSSeleniumUtil.clearAndSendKeys(webDriver, userNameInput, userName);

        CSSeleniumUtil.clickOn(webDriver, passwordInput);
        CSSeleniumUtil.clearAndSendKeys(webDriver, passwordInput, password);

        CSSeleniumUtil.clickOn(webDriver, loginBy);

        /**
         * TODO this should be moved to the tests
        String expectedUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), userName);
        // Assert was rewritten this way so we can see both strings in tests log
        if (expected) {
            assertEquals(expectedUrl, driver.getCurrentUrl());
        } else {
            assertNotEquals(expectedUrl, driver.getCurrentUrl());
        }
         */
    }

    public void logout(){
        By logoutBy;
        By userItemsWrapperBy;// =user-items-wrapper
        By optionalLogoutBy;

        webDriver.manage().window().maximize();
        logoutBy = By.id("acn-logout-link");
        userItemsWrapperBy = By.xpath("//body[@id='Share']//div[@class='user-items-wrapper']");
        List<WebElement> userItemsWrappers = webDriver.findElements(userItemsWrapperBy);

        if(userItemsWrappers.size() > 0) { //user dashboard page
            CSSeleniumUtil.clickOn(webDriver,userItemsWrappers.get(0).findElement(By.tagName("button")));
            optionalLogoutBy = By.xpath("//a[text()='Logout']");
            CSSeleniumUtil.clickOn(webDriver,optionalLogoutBy);
        }
        else {
            CSSeleniumUtil.clickOn(webDriver, logoutBy);
        }


    }


}
