package org.craftercms.web.refactoring.handlers.editors;

import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSPageEditorHandler extends CSComponentEditorHandler {

    public CSPageEditorHandler(WebDriver webDriver, String siteName, String contentType) {
        super(webDriver, siteName, contentType);
    }

    public void saveAndPreview(){
        CSSeleniumUtil.clickOn(webDriver, By.id("cstudioSaveAndPreview"));
    }

    //TODO could be useful if building a url is needed in a particular test: baseUrl + getExtraPath() + getFileName()
    //public abstract String getExtraPath();

}
