package org.craftercms.web.refactoring.handlers.editors.pages;

import org.craftercms.web.refactoring.handlers.editors.CSPageEditorHandler;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSInteriorPageEditorHandler extends CSPageEditorHandler {

    private static final String CONTENT_TYPE = "";

    public CSInteriorPageEditorHandler(WebDriver webDriver, String siteName) {
        super(webDriver, siteName, CONTENT_TYPE);
    }

}
