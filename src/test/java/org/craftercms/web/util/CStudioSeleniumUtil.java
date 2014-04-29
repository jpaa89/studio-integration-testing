/**
 *
 */
package org.craftercms.web.util;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Praveen C Elineni
 *
 * Utility Class for Selenium
 *
 */
public class CStudioSeleniumUtil {
    private static final Logger logger = Logger.getLogger("CStudioSeleniumUtil.class");

    private static final Properties seleniumProperties = new Properties();
    static {
        try {
    	    seleniumProperties.load(CStudioSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Login to Crafter Studio
     *
     * @param driver
     * @param userName
     * @param password
     * @param expected
     */
	public static void tryLogin(WebDriver driver, String userName, String password, boolean expected) {
        logger.info("Logging in");
        driver.get(seleniumProperties.getProperty("craftercms.login.page.url"));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        CStudioSeleniumUtil.clickOn(driver, By.name("username"));
        WebElement userNameElement = driver.findElement(By.name("username"));
        userNameElement.clear();
        userNameElement.sendKeys(userName);
        CStudioSeleniumUtil.clickOn(driver, By.name("password"));
        WebElement passwordElement = driver.findElement(By.name("password"));
        passwordElement.clear();
        passwordElement.sendKeys(password);
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        logger.log(Level.INFO, "Username: ''{0}''", userNameElement.getAttribute("value"));
        logger.log(Level.INFO, "Password: ''{0}''", passwordElement.getAttribute("value"));

        CStudioSeleniumUtil.clickOn(driver,By.xpath("//*[text()='Login' or @value='Login']"));// 2.4 or 2.3

        String expectedUrl = String.format(seleniumProperties.getProperty("craftercms.user.dashboard.url"), userName);
        // Assert was rewritten this way so we can see both strings in tests log
        if (expected) {
            assertEquals(expectedUrl, driver.getCurrentUrl());
        } else {
            assertNotEquals(expectedUrl, driver.getCurrentUrl());
        }
    }

	/**
	 * Logout of crafter Studio
	 *
	 * @param driver
	 */
    public static void tryLogout(WebDriver driver) {
        driver.manage().window().maximize();
        By by = By.id("acn-logout-link");
        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        WebElement element = driver.findElement(by);
        element.click();
    }

	/**
	 * Try to access a page in Crafter Studio Site
	 *
	 * @param driver
	 * @param siteName
	 */
    public static void navigateToUrl(WebDriver driver, String siteName, String url) {
        driver.navigate().to(url);
        assertTrue(driver.getCurrentUrl().equals(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName)));
    }

    /**
     * Execute JS before page edit
     *
     * @param driver
     * @param editContent
     * @param contentType
     * @param siteName 
     */
    public static void editContentJS(WebDriver driver, String editContent, String contentType, String siteName) {
        
        try {
            // Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
        		"CStudioAuthoring.Operations.editContent(" +
        	            "'" + contentType + "', " +
        	            "'" + siteName + "', " +
        	            "'" + editContent + "', " +
        	            "'', " +
        	            "'" + editContent + "', " +
        	            "false, " +
        	            "{" +
        	            "  success: function() { " +
        	            "}," +
        	            "  failure: function() {" +
        	            "}" +
        	            "}" +
        	            ");");

        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = driver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if(continueButton.size() > 0) {
            continueButton.get(0).click();
        }
    }

    /**
     * Execute JS to open create content window
     *
     * @param driver
     * @param path
     * @param siteName
     */
    public static void createContentJS(WebDriver driver, String path, String siteName) {
        try {// Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "CStudioAuthoring.Operations.createNewContent(" +
                        "'" + siteName + "', " +
                        "'" + path + "', " +
                        "false, " +
                        "{" +
                        "  success: function() { " +
                        "}," +
                        "  failure: function() {" +
                        "}" +
                        "}" +
                        ");");
    }

    /**
     * Execute JS to open create folder dialog
     *
     * @param driver
     * @param path
     * @param siteName
     */
    public static void createFolderJS(WebDriver driver, String path, String siteName) {
        try {// Wait for page to load
            Thread.sleep(5000);
        } catch (Exception ex) {
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "CStudioAuthoring.Operations.createFolder(" +
                        "'" + siteName + "'," +
                        "'" + path + "'," +
                        "window," +
                        "{" +
                        "  success: function() { " +
                        "}," +
                        "  failure: function() {" +
                        "}" +
                        "}" +
                        ");");
    }

    /**
     * Click on element
     *
     * @param driver
     * @param by
     */
    public static void clickOn(WebDriver driver, By by) {
        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        driver.findElement(by).click();
    }

    /**
     * Click on element
     *
     * @param driver
     * @param webElement
     */
    public static void clickOn(WebDriver driver, final WebElement webElement) {
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return webElement.isDisplayed();
            }
        });
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
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

    public static void loginAndEditPage(WebDriver driver, String userName, String password, String editPage, String contentType, String siteName) {
    	CStudioSeleniumUtil.tryLogin(driver, userName, password, true);

    	driver.navigate().to(String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName));

        // Execute JS before Edit Page
        CStudioSeleniumUtil.editContentJS(driver, editPage, contentType, siteName);

        switchToEditWindow(driver);
    }


    /**
     * Waits for a new window to show, then switches to it
     * @param driver
     */
    public static void switchToEditWindow(WebDriver driver) {
        // Wait for the window to load
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getWindowHandles().size() > 1;
            }
        });

        // Switch to edit window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            if (driver.getCurrentUrl().contains("cstudio-form"))
                break;
        }
        driver.manage().window().maximize();
    }

    /**
     * Waits for secondary window to close, then switch back to main window
     * @param driver
     */
    public static void switchToMainWindow(WebDriver driver) {
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (d.getWindowHandles().size() == 1);
            }
        });

        // Navigate back to dashboard page and switch window
        Set<String> handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
        }
    }

    public static void editAndSavePage(WebDriver driver, String editPage, String editString) {
        // Execute JS before Edit Page
        CStudioSeleniumUtil.editContentJS(driver, editPage,
                seleniumProperties.getProperty("craftercms.page.content.type"),
                seleniumProperties.getProperty("craftercms.sitename"));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = driver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if (continueButton.size() > 0) {
            continueButton.get(0).click();
        }
        switchToEditWindow(driver);

        // Find internal-name field and edit
        By internalNameBy = By.cssSelector("#internal-name .datum");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, internalNameBy);
        WebElement internalNameElement = driver.findElement(internalNameBy);
        internalNameElement.clear();
        internalNameElement.sendKeys(editString);

        // Click Save&Close button and wait for change to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        switchToMainWindow(driver);

        assertTrue(driver.getTitle().equals("Crafter Studio"));
    }

    public static void editAndSaveComponent(WebDriver driver, String editComponent, String editString) {
        // Execute JS before Edit Page
        CStudioSeleniumUtil.editContentJS(driver, editComponent,
                seleniumProperties.getProperty("craftercms.component.content.type"),
                seleniumProperties.getProperty("craftercms.sitename"));
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = driver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if (continueButton.size() > 0) {
            continueButton.get(0).click();
        }
        switchToEditWindow(driver);

        // Find internal-name field and edit
        By internalNameBy = By.cssSelector("#internal-name .datum");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, internalNameBy);
        WebElement internalNameElement = driver.findElement(internalNameBy);
        internalNameElement.clear();
        internalNameElement.sendKeys(editString);

        // Click Save&Close button and wait for change to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("cstudioSaveAndClose")).click();

        switchToMainWindow(driver);

        assertTrue(driver.getTitle().equals("Crafter Studio"));
    }

    public static void createArticle(WebDriver driver, String path, String articleUrl, String articleContent, String title, String siteName) {
        createContentJS(driver, path, siteName);

        clickOn(driver, By.cssSelector("option[value=\"/page/article\"]"));
        clickOn(driver, By.id("submitWCMPopup"));

        switchToEditWindow(driver);

        setField(driver, "div#file-name .datum", articleUrl);
        setField(driver, "div#internal-name .datum", title);
        setField(driver, "div#title .datum", title);
        setField(driver, "div#author .datum", "Crafter CMS");

        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.tagName("iframe"));

        // Put text in article main content
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.frames['mce_0_ifr'].document.getElementsByTagName('body')[0].innerHTML = '<p>" + articleContent + "</p>'");

        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));

        switchToMainWindow(driver);
    }

    public static void openNewArticleForm(WebDriver driver, String path, String articleUrl, String articleContent, String title, String siteName) {
        createContentJS(driver, path, siteName);

        clickOn(driver, By.cssSelector("option[value=\"/page/article\"]"));
        clickOn(driver, By.id("submitWCMPopup"));

        switchToEditWindow(driver);

        setField(driver, "div#file-name .datum", articleUrl);
        setField(driver, "div#internal-name .datum", title);
        setField(driver, "div#title .datum", title);
        setField(driver, "div#author .datum", "Crafter CMS");

        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.tagName("iframe"));

        // Put text in article main content
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.frames['mce_0_ifr'].document.getElementsByTagName('body')[0].innerHTML = '<p>" + articleContent + "</p>'");

    }

    private static void setField(WebDriver driver, String selector, String value) {
        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.cssSelector(selector));
        waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.cssSelector(selector));
        WebElement element = driver.findElement(By.cssSelector(selector));
        element.sendKeys(value);
    }

    public static void createFolder(WebDriver driver, String folderName, String path, String siteName) {
        createFolderJS(driver, path, siteName);

        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id("folderNameId"));

        WebElement folderNameField = driver.findElement(By.id("folderNameId"));
        folderNameField.sendKeys(folderName);

        driver.findElement(By.id("createButton")).click();

        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElements(By.id("createButton")).size() == 0;
            }
        });
    }

    public static void createTout(WebDriver driver, String path, String toutName, String headline, String internalName, String siteName) {
        createContentJS(driver, path, siteName);

        clickOn(driver, By.cssSelector("option[value=\"/component/tout\"]"));
        clickOn(driver, By.id("submitWCMPopup"));

        switchToEditWindow(driver);

        setField(driver, "div#file-name .datum", toutName);
        setField(driver, "div#internal-name .datum", internalName);
        setField(driver, "div#headline .datum", headline);

        clickOn(driver, By.id("segments-All"));

        clickOn(driver, By.id("cstudioSaveAndClose"));

        switchToMainWindow(driver);
    }

    public static void contextMenuOption(WebDriver driver, String option, WebElement item) {
        final By menuItemsBy = By.cssSelector("#acn-context-menu li.yuimenuitem a");
        new Actions(driver).contextClick(item).perform();
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElements(menuItemsBy).size() > 0;
            }
        });
        new Actions(driver).moveToElement(driver.findElement(menuItemsBy)).build().perform();
        boolean optionFound = false;
        List<WebElement> menuItems = driver.findElements(menuItemsBy);
        assertTrue(menuItems.size() > 0);
        for (WebElement menuItem : menuItems) {
            if (menuItem.getAttribute("innerHTML").equals(option)) {
                menuItem.click();
                optionFound = true;
                break;
            }
        }
        assertTrue(optionFound);
    }

    public static boolean contextMenuOptionExists(WebDriver driver, String option, WebElement item) {
        final By menuItemsBy = By.cssSelector("#acn-context-menu li.yuimenuitem a");
        new Actions(driver).contextClick(item).perform();
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElements(menuItemsBy).size() > 0;
            }
        });
        new Actions(driver).moveToElement(driver.findElement(menuItemsBy)).build().perform();
        boolean optionFound = false;
        List<WebElement> menuItems = driver.findElements(menuItemsBy);
        assertTrue(menuItems.size() > 0);
        for (WebElement menuItem : menuItems) {
            if (menuItem.getAttribute("innerHTML").equals(option)) {
                optionFound = true;
                break;
            }
        }
        return optionFound;
    }

    public static void ensureDropDownIsVisible(WebDriver driver) {
        boolean dropDownVisible = false;
        final By dropDownMenuElementBy = By.id("acn-dropdown-menu-wrapper");
        List<WebElement> dropDownElement = driver.findElements(dropDownMenuElementBy);
        if (dropDownElement.size() > 0) {
            dropDownVisible = driver.findElement(dropDownMenuElementBy).isDisplayed();
        }

        if (!dropDownVisible) {
            By togglerBy = By.id("acn-dropdown-toggler");
            CStudioSeleniumUtil.clickOn(driver, togglerBy);

            new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return d.findElement(dropDownMenuElementBy).isDisplayed();
                }
            });
            waitFor(5);
        }
    }

    public static void ensurePagesTreeIsExpanded(WebDriver driver) {
        ensureDropDownIsVisible(driver);

        By homePageBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'Home')]");
        List<WebElement> pagesTreeRoot = driver.findElements(homePageBy);
        boolean needToExpand = true;
        if (pagesTreeRoot.size() > 0) {
            if (pagesTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, By.id("pages-tree"));
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, homePageBy);
        }

        final WebElement homeTableElement = driver.findElement(By.xpath("//span[contains(.,'Home')]/ancestor::table"));
        String homeTableClass = homeTableElement.getAttribute("class");
        needToExpand = homeTableClass.contains("collapsed");

        if (needToExpand) {
            homeTableElement.findElement(By.cssSelector("a.ygtvspacer")).click();
            new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return !homeTableElement.getAttribute("class").contains("collapsed");
                }
            });
        }
        waitFor(2);
    }

    public static void ensureComponentsTreeIsExpanded(WebDriver driver) {
        ensureDropDownIsVisible(driver);

        By componentsTreeRootBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'components')]");
        List<WebElement> componentsTreeRoot = driver.findElements(componentsTreeRootBy);
        boolean needToExpand = true;
        if (componentsTreeRoot.size() > 0) {
            if (componentsTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            waitFor(1);
            clickOn(driver, By.id("components-tree"));
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, componentsTreeRootBy);
        }

        By toutsFolderBy = By.xpath("//span[contains(.,'touts')]");
        List<WebElement> toutsFolder = driver.findElements(toutsFolderBy);
        needToExpand = true;
        if (toutsFolder.size() > 0) {
            if (toutsFolder.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, componentsTreeRootBy);
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, toutsFolderBy);
        }
        final WebElement toutsTableElement = driver.findElement(By.xpath("//span[contains(.,'touts')]/ancestor::table"));
        String toutsClass = toutsTableElement.getAttribute("class");
        needToExpand = toutsClass.contains("collapsed");
        if (needToExpand) {
            clickOn(driver, toutsFolderBy);
            new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver d) {
                    return !toutsTableElement.getAttribute("class").contains("collapsed");
                }
            });
        }
        waitFor(2);
    }

    public static void ensureTemplatesTreeIsExpanded(WebDriver driver) {
        ensureDropDownIsVisible(driver);

        By templatesTreeRootBy = By.xpath("//div[@id='acn-dropdown-menu-wrapper']//span[contains(.,'web')]");
        List<WebElement> componentsTreeRoot = driver.findElements(templatesTreeRootBy);
        boolean needToExpand = true;
        if (componentsTreeRoot.size() > 0) {
            if (componentsTreeRoot.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            waitFor(1);
            clickOn(driver, By.id("templates-tree"));
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, templatesTreeRootBy);
        }



        By commonFolderBy = By.xpath("//span[contains(.,'common')]");
        List<WebElement> toutsFolder = driver.findElements(commonFolderBy);
        needToExpand = true;
        if (toutsFolder.size() > 0) {
            if (toutsFolder.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            clickOn(driver, templatesTreeRootBy);
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, commonFolderBy);
        }
    }

    public static void ensureStaticAssetsTreeIsExpanded(WebDriver driver) throws InterruptedException {
        ensureDropDownIsVisible(driver);

        By assetsTreeFoldersBy = By.xpath("//a[@id='static assets-tree']/ancestor::div[contains(@class, 'assets-tree')]//table[contains(@class,'ygtvtable')]");
        List<WebElement> assetsTreeFolderTables = driver.findElements(assetsTreeFoldersBy);
        boolean needToExpand = true;
        if (assetsTreeFolderTables.size() > 0) {
            if (assetsTreeFolderTables.get(0).isDisplayed()) {
                needToExpand = false;
            }
        }
        if (needToExpand) {
            Thread.sleep(1000);
            clickOn(driver, By.id("static assets-tree"));
            waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, assetsTreeFoldersBy);
        }
        assetsTreeFolderTables = driver.findElements(assetsTreeFoldersBy);
        for (WebElement folderTable : assetsTreeFolderTables) {
            if (folderTable.getAttribute("class").contains("collapsed")) {
                List<WebElement> folder = folderTable.findElements(By.cssSelector("span.acn-parent-folder"));
                if (folder.size() > 0) {
                    folder.get(0).click();
                    Thread.sleep(1000);
                }
            }
        }
    }

    /**
     * Find an element in content tree with name itemName
     * REQUIRES relevant part of content tree to be expanded.
     * @param driver web driver
     * @param itemName name of the item to find
     * @return WebElement with text containing itemName, or null if none was found
     */
    public static WebElement findItemWithName(WebDriver driver, String itemName) {
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='ygtvitem']//tr[@class='ygtvrow' and contains(.,'" + itemName + "')]"));
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    /**
     * Upload a file to the path represented by parentElement (element under Site Content drop down)
     * @param driver
     * @param file
     * @param parentElement
     */
    public static void uploadFile(WebDriver driver, File file, WebElement parentElement) {
        logger.info("Choose 'Upload' option in context menu");
        CStudioSeleniumUtil.contextMenuOption(driver, "Upload", parentElement);

        By fileInputBy = By.id("uploadFileNameId");
        CStudioSeleniumUtil.waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, fileInputBy);
        CStudioSeleniumUtil.waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_DEPLOY, fileInputBy);

        logger.info("Indicate file path");
        WebElement fileInput = driver.findElement(fileInputBy);
        fileInput.sendKeys(file.getAbsolutePath());

        logger.info("Confirm upload");
        final By uploadButtonBy = By.id("uploadButton");
        driver.findElement(uploadButtonBy).click();

        logger.info("Wait for file to upload");
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_DEPLOY).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return d.findElements(uploadButtonBy).size() == 0;
            }
        });
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
        waitForItemToDisplay(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        waitForItemToBeEnabled(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
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
     * Waits for current page lo load. Uses js document.readyState to check if the page is loaded and then
     * waits a few more arbitrary seconds to give light js tasks -if any- a chance.
     * @param webDriver
     */
    public static void waitForCurrentPageToLoad(WebDriver webDriver) {

        new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_PAGE_LOADING).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriverParam) {
                return ((JavascriptExecutor) webDriverParam).executeScript("return document.readyState").equals("complete");
            }
        });

        // Arbitrary waiting time (to give light javascript tasks -if any- a chance)
        waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);
    }

    /**
     * Navigates to the given url and calls c
     * @param webDriver
     * @param url
     */
    public static void navigateToAndWaitForPageToLoad(WebDriver webDriver, String url) {
        webDriver.navigate().to(url);
        waitForCurrentPageToLoad(webDriver);
    }

    /**
     * Refreshes the current page and calls waitForCurrentPageToLoad
     * @param webDriver
     */
    public static void refreshAndWaitForPageToLoad(WebDriver webDriver) {
        webDriver.navigate().refresh();
        waitForCurrentPageToLoad(webDriver);
    }

    /**
     * Opens the edit form for the specified content and switches to its edit window
     *
     * @param webDriver
     * @throws InterruptedException
     */
    public static void openAndSwitchToEditForm(WebDriver webDriver, String editContent, String contentType, String siteName) {

        editContentJS(webDriver,editContent,contentType,siteName);

        waitFor(TimeConstants.WAITING_SECONDS_LIGHT_JAVASCRIPT_TASKS);

        // Cancel schedule to edit, when needed
        List<WebElement> continueButton = webDriver.findElements(By.cssSelector(".cancel-workflow-view button.continue"));
        if (continueButton.size() > 0) {
            continueButton.get(0).click();
        }

        switchToEditWindow(webDriver);

    }

    /**
     * Moves the mouse to the specified location
     * @param webDriver
     * @param by Element locator
     */
    public static void moveMouseTo(WebDriver webDriver, By by) {
        WebElement target = webDriver.findElement(by);
        CStudioSeleniumUtil.waitForItemToDisplay(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,by);
        CStudioSeleniumUtil.waitForItemToBeEnabled(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, by);
        new Actions(webDriver).moveToElement(target).perform();
    }

    /**
     * Creates specified number of generated articles adding System.currentTimeMillis() to the given prefixes prefixes.
     * Calls methods that end up using js calls to accomplish this.
     * @param webDriver
     * @param numberOfArticlesToBeCreated
     * @param path
     * @param fileNamePrefix
     * @param articleContent
     * @param articleTitlePrefix
     * @param siteName
     * @return articles Uris
     */
    public static String[] createArticlesWithinPath(WebDriver webDriver, int numberOfArticlesToBeCreated, String path, String fileNamePrefix, String articleContent, String articleTitlePrefix, String siteName) {
        String[] articlesUris = new String[numberOfArticlesToBeCreated];
        String currentTimeMillis;
        String articleFileName;
        String articleTitle;
        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }
        for(int i = 0; i < numberOfArticlesToBeCreated; i++){
            currentTimeMillis = ""+System.currentTimeMillis();
            articleFileName = fileNamePrefix+currentTimeMillis;
            articleTitle = articleTitlePrefix+currentTimeMillis;
            createArticle(webDriver,path,articleFileName,articleContent,articleTitle,siteName);
            articlesUris[i] = path+"/"+articleFileName+"/index.xml";
        }
        return  articlesUris;
    }

    /**
     * Deletes the specified articles.
     * Uses js calls to accomplish this.
     * Warning: not tested after changing the method to use uris
     * @param webDriver
     * @param articlesUris
     * @throws InterruptedException
     */
    public static void deleteArticles(WebDriver webDriver, String[] articlesUris) throws InterruptedException {

        int articleCount = articlesUris.length;
        String jsItemObject;
        StringBuffer jsItemObjectsJsArray = new StringBuffer();
        String[] jsItemObjectsStrings = new String[articleCount];
        String jsBrowserUriProperty;
        String jsUriProperty;
        String jsScriptToDeleteItems;
        String articleUri;
        String browserUri;

        for(int i = 0; i < articleCount; i++){
            articleUri = articlesUris[i];
            String[] splitString = articleUri.split("/");
            browserUri = splitString[splitString.length-2];
            jsBrowserUriProperty = "browserUri: '/"+browserUri+"'";
            jsUriProperty = "uri: '"+articleUri+"'";
            jsItemObject = "{"+jsBrowserUriProperty+","+jsUriProperty+"}";
            jsItemObjectsStrings[i] = jsItemObject;
        }

        if(articleCount > 0){
            jsItemObjectsJsArray.append("["+jsItemObjectsStrings[0]);
            for(int i = 1; i < articleCount; i++){
                jsItemObjectsJsArray.append(","+jsItemObjectsStrings[i]);
            }
            jsItemObjectsJsArray.append("]");
        }

        jsScriptToDeleteItems = "CStudioAuthoring.Operations.deleteContent("+jsItemObjectsJsArray+")";

        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript(jsScriptToDeleteItems);

        //TODO change this by checking if the elements are listed within the window popup, watch out with
        // elements staleness fix staleness problems. They had to do with the popup that was opened by
        // the javascript call (it seems like it reloads itself after some javascript calls and tasks)
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Confirm deletion");
        CStudioSeleniumUtil.clickOn(webDriver, By.cssSelector("input.do-delete"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*articleCount,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*articleCount);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Ok");
        CStudioSeleniumUtil.clickOn(webDriver, By.cssSelector("#acnVersionWrapper input[value='OK']"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*articleCount,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*articleCount);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

    }

    /**
     * Creates specified number of touts adding System.currentTimeMillis() to their urls and internal name prefixes
     * @param webDriver
     * @param numberOfToutsToBeCreated
     * @param path
     * @param toutUrlPrefix
     * @param toutInternalNamePrefix
     * @param toutHeadline
     * @param siteName
     * @return touts uris
     */
    public static String[] createTautsWithinPath(WebDriver webDriver, int numberOfToutsToBeCreated, String path, String toutUrlPrefix, String toutInternalNamePrefix, String toutHeadline, String siteName) {
        String[] toutsUris = new String[numberOfToutsToBeCreated];
        String currentTimeMillis;
        String toutFileName;
        String toutInternalName;
        if(path.endsWith("/")){
            path = path.substring(0,path.length()-1);
        }
        for(int i = 0; i < numberOfToutsToBeCreated; i++){
            currentTimeMillis = ""+System.currentTimeMillis();
            toutFileName = toutUrlPrefix+currentTimeMillis;
            toutInternalName = toutInternalNamePrefix+currentTimeMillis;
            createTout(webDriver,path,toutFileName,toutHeadline,toutInternalName,siteName);
            toutsUris[i] = path+"/"+toutFileName+".xml";
        }
        return  toutsUris;
    }

    /**
     * Deletes Touts
     * Warning: not tested after changing the method to use uris
     * @param webDriver
     * @param toutsUris
     * @throws InterruptedException
     */
    public static void deleteTauts(WebDriver webDriver, String[] toutsUris) throws InterruptedException {

        int toutsCount = toutsUris.length;
        String jsItemObject;
        StringBuffer jsItemObjectsJsArray = new StringBuffer();
        String[] jsItemObjectsStrings = new String[toutsCount];
        String jsBrowserUriProperty;
        String jsUriProperty;
        String jsScriptToDeleteItems;
        String toutUri;
        String browserUri;

        for(int i = 0; i < toutsCount; i++){
            toutUri = toutsUris[i];
            String[] splitString = toutUri.split("/");
            browserUri = splitString[splitString.length-1];
            jsBrowserUriProperty = "browserUri: '"+browserUri+"'";
            jsUriProperty = "uri: '"+toutUri+"'";
            jsItemObject = "{"+jsBrowserUriProperty+","+jsUriProperty+"}";
            jsItemObjectsStrings[i] = jsItemObject;
        }

        if(toutsCount > 0){
            jsItemObjectsJsArray.append("["+jsItemObjectsStrings[0]);
            for(int i = 1; i < toutsCount; i++){
                jsItemObjectsJsArray.append(","+jsItemObjectsStrings[i]);
            }
            jsItemObjectsJsArray.append("]");
        }

        jsScriptToDeleteItems = "CStudioAuthoring.Operations.deleteContent("+jsItemObjectsJsArray+")";

        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript(jsScriptToDeleteItems);

        //TODO change this by checking if the elements are listed within the window popup, watch out with elements
        // staleness
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Confirm deletion");
        CStudioSeleniumUtil.clickOn(webDriver, By.cssSelector("input.do-delete"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*toutsCount,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*toutsCount);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

        logger.info("Ok");
        CStudioSeleniumUtil.clickOn(webDriver, By.cssSelector("#acnVersionWrapper input[value='OK']"),
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*toutsCount,
                TimeConstants.WAITING_SECONDS_WEB_ELEMENT*toutsCount);

        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_HEAVY_JAVASCRIPT_TASKS);

    }




}