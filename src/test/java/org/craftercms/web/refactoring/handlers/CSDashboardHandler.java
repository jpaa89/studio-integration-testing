package org.craftercms.web.refactoring.handlers;

import org.craftercms.web.basic.dashboard.widget.helpers.*;
import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Juan Avila
 */
public class CSDashboardHandler extends CSHandler {

    //TODO this could be split into an admin dashboard and an author dashboard

    protected CSContextNavigationBarHandler csContextNavigationBarHandler;
    protected CSGoLiveQueueWidgetHandler csGoLiveQueueWidgetHandler;
    protected CSApprovedScheduledItemsWidgetHandler csApprovedScheduledItemsWidgetHandler;
    protected CSRecentlyMadeLiveWidgetHandler csRecentlyMadeLiveWidgetHandler;
    protected CSMyRecentActivityWidgetHandler csMyRecentActivityWidgetHandler;
    protected CSIconGuideSimpleDashboardWidgetHandler csIconGuideSimpleDashboardWidgetHandler;


    public CSDashboardHandler(WebDriver webDriver) {
        super(webDriver);
        csContextNavigationBarHandler = new CSContextNavigationBarHandler(webDriver);
        csGoLiveQueueWidgetHandler = new CSGoLiveQueueWidgetHandler(webDriver);
        csApprovedScheduledItemsWidgetHandler = new CSApprovedScheduledItemsWidgetHandler(webDriver);
        csRecentlyMadeLiveWidgetHandler = new CSRecentlyMadeLiveWidgetHandler(webDriver);
        csMyRecentActivityWidgetHandler = new CSMyRecentActivityWidgetHandler(webDriver);
        csIconGuideSimpleDashboardWidgetHandler = new CSIconGuideSimpleDashboardWidgetHandler(webDriver);
    }

    public String pageTitle(){
        By pageTitleBy = By.id("pageTitle");
        WebElement pageTitle = webDriver.findElement(pageTitleBy);
        return pageTitle.getText();
    }

    public String pageFooter(){
        By pageFooterBy = By.id("footer");
        WebElement pageFooter = webDriver.findElement(pageFooterBy);
        return pageFooter.getText();
    }

    public CSContextNavigationBarHandler getCsContextNavigationBarHandler() {
        return csContextNavigationBarHandler;
    }

    public void setCsContextNavigationBarHandler(CSContextNavigationBarHandler csContextNavigationBarHandler) {
        this.csContextNavigationBarHandler = csContextNavigationBarHandler;
    }

    public CSGoLiveQueueWidgetHandler getCsGoLiveQueueWidgetHandler() {
        return csGoLiveQueueWidgetHandler;
    }

    public void setCsGoLiveQueueWidgetHandler(CSGoLiveQueueWidgetHandler csGoLiveQueueWidgetHandler) {
        this.csGoLiveQueueWidgetHandler = csGoLiveQueueWidgetHandler;
    }

    public CSApprovedScheduledItemsWidgetHandler getCsApprovedScheduledItemsWidgetHandler() {
        return csApprovedScheduledItemsWidgetHandler;
    }

    public void setCsApprovedScheduledItemsWidgetHandler(CSApprovedScheduledItemsWidgetHandler csApprovedScheduledItemsWidgetHandler) {
        this.csApprovedScheduledItemsWidgetHandler = csApprovedScheduledItemsWidgetHandler;
    }

    public CSRecentlyMadeLiveWidgetHandler getCsRecentlyMadeLiveWidgetHandler() {
        return csRecentlyMadeLiveWidgetHandler;
    }

    public void setCsRecentlyMadeLiveWidgetHandler(CSRecentlyMadeLiveWidgetHandler csRecentlyMadeLiveWidgetHandler) {
        this.csRecentlyMadeLiveWidgetHandler = csRecentlyMadeLiveWidgetHandler;
    }

    public CSMyRecentActivityWidgetHandler getCsMyRecentActivityWidgetHandler() {
        return csMyRecentActivityWidgetHandler;
    }

    public void setCsMyRecentActivityWidgetHandler(CSMyRecentActivityWidgetHandler csMyRecentActivityWidgetHandler) {
        this.csMyRecentActivityWidgetHandler = csMyRecentActivityWidgetHandler;
    }

    public CSIconGuideSimpleDashboardWidgetHandler getCsIconGuideSimpleDashboardWidgetHandler() {
        return csIconGuideSimpleDashboardWidgetHandler;
    }

    public void setCsIconGuideSimpleDashboardWidgetHandler(CSIconGuideSimpleDashboardWidgetHandler csIconGuideSimpleDashboardWidgetHandler) {
        this.csIconGuideSimpleDashboardWidgetHandler = csIconGuideSimpleDashboardWidgetHandler;
    }

    public boolean isPageTitleVisible(){
        By pageTitleBy = By.id("pageTitle");
        return isDisplayed(CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT,pageTitleBy);
    }

    public boolean isPageFooterVisible(){
        By pageFooterBy = By.id("footer");
        return isDisplayed(CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT,pageFooterBy);
    }


}
