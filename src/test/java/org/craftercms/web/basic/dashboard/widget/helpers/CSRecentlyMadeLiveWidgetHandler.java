package org.craftercms.web.basic.dashboard.widget.helpers;

import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 * TODO Warning this a very old implementation that needs a lot of changes. This should not be considered functional and has not been refactored at all!
 */
public class CSRecentlyMadeLiveWidgetHandler extends CSDashboardWidgetHandler {

    public CSRecentlyMadeLiveWidgetHandler(WebDriver webDriver) {
        super(webDriver,"recentlyMadeLive");
    }
}
