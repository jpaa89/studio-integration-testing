package org.craftercms.web.refactoring.handlers.editors;

import org.craftercms.web.refactoring.handlers.CSHandler;
import org.openqa.selenium.*;

/**
 * @author Juan Avila
 */
public abstract class CSContentEditorHandler extends CSHandler {

    protected String contentType;

    public CSContentEditorHandler(WebDriver webDriver, String contentType) {
        super(webDriver);
        this.contentType = contentType;
    }

    public abstract String getUri();

    public String getFileName(){
        String[] uriSections = getUri().split("/");
        return uriSections[uriSections.length-1];
    }

}
