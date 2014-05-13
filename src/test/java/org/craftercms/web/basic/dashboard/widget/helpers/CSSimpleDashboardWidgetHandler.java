package org.craftercms.web.basic.dashboard.widget.helpers;

import org.craftercms.web.refactoring.handlers.CSHandler;
import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Juan Avila
 * TODO Warning this a very old implementation that needs a lot of changes. This should not be considered functional and has not been refactored at all!
 */
public abstract class CSSimpleDashboardWidgetHandler extends CSHandler {

    protected String divId;

    public CSSimpleDashboardWidgetHandler(WebDriver webDriver, String divId) {
        super(webDriver);
        this.divId = divId;
    }

    public String getDivId() {
        return divId;
    }

    public void setDivId(String divId) {
        this.divId = divId;
    }

    public boolean isVisible(){
        By divIdBy = By.id(divId);
        return isDisplayed(CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT,divIdBy);
    }

    public String headerTitle(){
        By widgetHeaderBy = By.cssSelector("div#" + divId + " .ttWidgetHdr");
        return webDriver.findElement(widgetHeaderBy).getText();
    }

    public void toggleCloseOpen(){
        By togglerBy = By.id("widget-toggle-" + divId);
        CSSeleniumUtil.clickOn(webDriver, togglerBy);
    }

    public boolean isClosed(){
        By togglerBy = By.id("widget-toggle-" + divId);
        WebElement tooggler = webDriver.findElement(togglerBy);
        return "ttOpen".equals(tooggler.getAttribute("class"));
    }

    public boolean isOpen(){
        By togglerBy = By.id("widget-toggle-" + divId);
        WebElement tooggler = webDriver.findElement(togglerBy);
        return "ttClose".equals(tooggler.getAttribute("class"));
    }


}
