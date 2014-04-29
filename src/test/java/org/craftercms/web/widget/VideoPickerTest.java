package org.craftercms.web.widget;
import static org.junit.Assert.assertTrue;
import java.util.logging.Logger;
import org.craftercms.web.util.TimeConstants;

import org.craftercms.web.WidgetBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * 
 * @author roger.diaz
 * @TODO: Fix the test to work on phatomjs
 */
public class VideoPickerTest extends WidgetBaseTest {

	private static final Logger logger = Logger.getLogger("VideoPickerTest.class");
	
    protected String assetsPath;
    protected String uploadVideoDS;
    
    private String validationString;
    

    @Before
    public void setUp() throws Exception {
    	super.setUp("craftercms.videopicker.widget.edit.page", "craftercms.videopicker.widget.content.type");
    	assetsPath = seleniumProperties.getProperty("craftercms.assets.to.tests.path");
    	uploadVideoDS = seleniumProperties.getProperty("craftercms.videopicker.widget.datasource.videoupload");
    	
    }
    
    public boolean containsClass(String[] classes, String className) {
    	boolean result = false;
    	if(classes != null) {
    		for(String c: classes){
    			if(c.equals(className)) {
    				result = true;
    				break;
    			}
    		}
    	}
    	
    	return result;
    }
    
    @Test
    public void testWidgetControlRequired() {
    	String video = "/requiredVideo.mp4";
    	//Clean the field
    	logger.info("Video Required - Content no entered");
    	driver.findElement(By.cssSelector("#required-video  input[value='Delete']")).click();
    	
    	validationString = driver.findElement(By.cssSelector("#required-video .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-invalid"));
    	
    	logger.info("Video Required - Content entered");
    	driver.findElement(By.cssSelector("#required-video input[value='Add']")).click();
    	driver.findElement(By.cssSelector("#required-video .cstudio-form-control-video-picker-add-container-item")).click();
    	
    	WebElement element = (new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + video);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
    	logger.info("Video Required - Start Uploading...");
    	new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
              return !d.findElement(By.cssSelector("#required-video .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	logger.info("Video Required - Finish Uploading...");
    	validationString = driver.findElement(By.cssSelector("#required-video .validation-hint")).getAttribute("class");
    	assertTrue(containsClass(validationString.split(" "), "cstudio-form-control-valid"));
    	validationString = driver.findElement(By.cssSelector("#required-video .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadVideoDS + video));
    }
    
    @Test
    public void testWidgetControl() {
    	String video = "/video.mov";
    	//Clean the field
    	logger.info("Video Required - Content no entered");
    	driver.findElement(By.cssSelector("#video  input[value='Delete']")).click();
    	
    	validationString = driver.findElement(By.cssSelector("#video .validation-hint")).getAttribute("class");
    	assertTrue(!containsClass(validationString.split(" "), "cstudio-form-control-invalid"));
    	
    	logger.info("Video Required - Content entered");
    	driver.findElement(By.cssSelector("#video input[value='Add']")).click();
    	driver.findElement(By.cssSelector("#video .cstudio-form-control-video-picker-add-container-item")).click();
    	
    	WebElement element = (new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT)).until(
        		ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cstudio-wcm-popup-div")));
    	//Check the modal window
    	assertTrue(element!=null);
    	
    	driver.findElement(By.cssSelector("#uploadFileNameId")).sendKeys(assetsPath + video);
    	driver.findElement(By.cssSelector("#uploadButton")).click();
    	
        logger.info("Video Required - Start Uploading...");    
        new WebDriverWait(driver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return !d.findElement(By.cssSelector("#video .datum")).getAttribute("value").isEmpty();
            }
          });
    	
    	logger.info("Video Required - Finish Uploading...");
    	validationString = driver.findElement(By.cssSelector("#video .validation-hint")).getAttribute("class");
    	assertTrue(!containsClass(validationString.split(" "), "cstudio-form-control-valid"));
    	validationString = driver.findElement(By.cssSelector("#video .datum")).getAttribute("value");
    	assertTrue(validationString.equals(uploadVideoDS + video));
    }
    
}
