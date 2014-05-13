package org.craftercms.web.refactoring.handlers.editors.pages;

import org.craftercms.web.refactoring.handlers.editors.CSPageEditorHandler;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSNodeSelectorChildWidgetTestPageEditorHandler extends CSPageEditorHandler {

    private static final String CONTENT_TYPE = "";

    public CSNodeSelectorChildWidgetTestPageEditorHandler(WebDriver webDriver, String siteName) {
        super(webDriver, siteName, CONTENT_TYPE);
    }

}
