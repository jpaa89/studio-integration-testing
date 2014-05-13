/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.craftercms.web.basic;

import org.craftercms.web.BaseTest;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author daniel.granados
 */
public class ComponentTest extends BaseTest {
    
    private static final Logger logger = Logger.getLogger("ComponentTest.class");
    private final String aboutPage = "/about";
    private String assetsPath;
    private String url;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        url = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), seleniumProperties.getProperty("craftercms.sitename"));
        assetsPath = seleniumProperties.getProperty("craftercms.assets.to.tests.path");
        
        CStudioSeleniumUtil.tryLogin(driver,
                seleniumProperties.getProperty("craftercms.admin.username"), 
                seleniumProperties.getProperty("craftercms.admin.password"),
                true);
        
        CStudioSeleniumUtil.navigateToUrl(driver, seleniumProperties.getProperty("craftercms.sitename"), url);
    }
    
    public void setShowInNav(boolean show) {

        // Click on Edit link
        driver.findElement(By.xpath("//*[@id=\"acn-active-content\"]/div[2]/a")).click();
        
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return d.getWindowHandles().size() > 1;
            }
          });
        
        String pageWindow = driver.getWindowHandle();
    	String editWindow = "";
    	Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
        	driver.switchTo().window(windowHandle);
        	if (driver.getCurrentUrl().contains("cstudio-form")){
        		editWindow = windowHandle;
        		break;
        	}
        }
        //The window can be switch
        assertTrue(!editWindow.isEmpty());
        
        Select dropdown = new Select(driver.findElement(By.cssSelector("#placeInNav .cstudio-form-control-dropdown")));
        dropdown.selectByValue(""+show);
        CStudioSeleniumUtil.clickOn(driver, By.id("cstudioSaveAndClose"));
        
        driver.switchTo().window(pageWindow);
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return d.getWindowHandles().size() == 1;
            }
          });
    }
    
    public boolean isPresent(List<WebElement> list, String validationString) {
        boolean isPresent = false;
        int i=0;
        while(!isPresent && i < list.size() ) {
            if(list.get(i).getText().contains(validationString))
                isPresent = true;
            i++;
        }
        
        return isPresent;
    }
    
    @Test
    public void testDragAndDrop() throws InterruptedException {
        String image = "/requiredImage.png";
        
        // Navigate to About page
        //driver.navigate().to(seleniumProperties.getProperty("craftercms.base.url") + aboutPage);
        CStudioSeleniumUtil.navigateToAndWaitForPageToLoad(driver,seleniumProperties.getProperty("craftercms.base.url") + aboutPage);

        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#acn-preview-tools-container"));
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return d.findElement(By.cssSelector("#preview-tools-panel-container")).isDisplayed();
            }
          });
        
        List<WebElement> options = (new WebDriverWait(driver, 10)).until(
        		ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#preview-tools-panel-container .contracted")));
    	
        options.get(1).click();
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return d.findElement(By.id("placeholder-zone-bottomPromos1")).isDisplayed();
            }
          });
        
        WebElement source = driver.findElement(By.id("yui-gen4"));
        WebElement target = driver.findElement(By.id("bottomPromos1"));
        
        Actions builder = new Actions(driver);
        Action dragAndDrop = builder.dragAndDrop(source, target).build();
        dragAndDrop.perform();
        
        WebElement element = (new WebDriverWait(driver, 10)).until(
        		ExpectedConditions.presenceOfElementLocated(By.id("in-context-edit-editor")));
    	//Check the modal window
    	assertTrue(element!=null);
        
        driver.switchTo().frame(element);
        
        //Upload an Image
        CStudioSeleniumUtil.clickOn(driver, By.cssSelector("#image input[value='Add']"));
        
        element = (new WebDriverWait(driver, 10)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + image);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
        
        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#image .datum")).getAttribute("value").isEmpty();
            }
          });
        
        driver.findElement(By.cssSelector("#cstudioSaveAndClose")).click();
        
        driver.switchTo().defaultContent();
    }
    
    
    @Test
    public void testNavigationBar() {
        // Navigate to About page
        driver.navigate().to(seleniumProperties.getProperty("craftercms.base.url") + aboutPage);
        
        List<WebElement> list =  driver.findElements(By.cssSelector(".main-nav > li"));
        
        assertTrue(this.isPresent(list, "ABOUT"));
        
        this.setShowInNav(false);
        
        list =  driver.findElements(By.cssSelector(".main-nav > li"));
       
        assertTrue(!this.isPresent(list, "ABOUT"));
        
        this.setShowInNav(true);
    }
    
    
}
