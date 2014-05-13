package org.craftercms.web.refactoring.handlers.editors.components;

import org.craftercms.web.refactoring.handlers.editors.CSComponentEditorHandler;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSImageComponentEditorHandler extends CSComponentEditorHandler {

    private static final String CONTENT_TYPE = "";

    public CSImageComponentEditorHandler(WebDriver webDriver, String siteName) {
        super(webDriver, siteName, CONTENT_TYPE);
    }

}
