package org.craftercms.web.refactoring.handlers.editors.pages.impl;

import org.craftercms.web.refactoring.handlers.editors.pages.CSArticlePageEditorHandler;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public class CSAboutPageEditorHandler extends CSArticlePageEditorHandler{

    private final static String URI = "/site/website/about/index.xml";

    public CSAboutPageEditorHandler(WebDriver webDriver, String siteName) {
        super(webDriver, siteName);
    }

    @Override
    public String getUri() {
        return URI;
    }

}
