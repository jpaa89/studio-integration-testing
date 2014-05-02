package org.craftercms.web.basic.dashboard.widget;

import org.craftercms.web.BaseTest;
import org.craftercms.web.helpers.DashboardWidgetHandler;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;


/**
 * @author Juan Avila
 */
public class DashboardWidgetTestsBase extends BaseTest {

    protected DashboardWidgetHandler myRecentActivityWidgetHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        driver.manage().timeouts().implicitlyWait(TimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);
        myRecentActivityWidgetHandler = new DashboardWidgetHandler(driver,"MyRecentActivity");
    }

    public void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

    public void useAdminUser() {
        setUsername(seleniumProperties.getProperty("craftercms.admin.username"));
        setPassword(seleniumProperties.getProperty("craftercms.admin.password"));
    }

    /**
     * Selects and Schedules the given contents to go live at the given date and time, (EST time zone).
     * Warning: Be careful, other selected contents within the dashboard will affect this method (they will also be
     * submitted to go live on the given schedule or will hide the Go live option from the context nav if they are not
     * suitable to go live. Ensure the given date and time are after now.
     * @param dashboardWidgetHandler the dashboard widget to select the contents from
     * @param contentUris content uris to be scheduled
     * @param date Date format example: "12/28/2099."
     * @param time Time format example: "11:59:59 p.m."
     *
     */
    protected void selectAndSubmitContentToGoLiveOnSchedule(DashboardWidgetHandler dashboardWidgetHandler, final String[] contentUris, String date, String time) {
        dashboardWidgetHandler.selectContents(contentUris);
        CStudioSeleniumUtil.selectAndSubmitContentToGoLiveOnSchedule(driver, date, time);
    }

    /**
     * Selects and submits the given contents to go live now.
     * Warning: Be careful, other selected contents within the dashboard will affect this method (they will also be
     * submitted to go live or will hide the Go live option from the context nav if they are not suitable to go live.
     * @param dashboardWidgetHandler the dashboard widget to select the contents from
     * @param contentUris the content uris
     */
    protected void selectAndSubmitContentToGoLiveNow(DashboardWidgetHandler dashboardWidgetHandler, final String[] contentUris) {
        dashboardWidgetHandler.selectContents(contentUris);
        CStudioSeleniumUtil.selectAndSubmitContentToGoLiveNow(driver);
    }



}
