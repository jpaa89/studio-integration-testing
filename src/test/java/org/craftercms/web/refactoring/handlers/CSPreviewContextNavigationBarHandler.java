package org.craftercms.web.refactoring.handlers;

import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public class CSPreviewContextNavigationBarHandler extends CSContextNavigationBarHandler {

    protected CSInContextEditHandler csInContextEditHandler;
    protected CSPreviewToolsHandler csPreviewToolsHandler;

    public CSPreviewContextNavigationBarHandler(WebDriver webDriver) {
        super(webDriver);

    }

}
