package org.craftercms.web.basic.dashboard.widget;

import org.craftercms.web.BaseTest;
import org.craftercms.web.helpers.DashboardWidgetHandler;
import org.craftercms.web.util.TimeConstants;

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
        myRecentActivityWidgetHandler = new DashboardWidgetHandler("MyRecentActivity");
    }

    public void useAuthorUser() {
        setUsername(seleniumProperties.getProperty("craftercms.author.username"));
        setPassword(seleniumProperties.getProperty("craftercms.author.password"));
    }

    public void useAdminUser() {
        setUsername(seleniumProperties.getProperty("craftercms.admin.username"));
        setPassword(seleniumProperties.getProperty("craftercms.admin.password"));
    }
}
