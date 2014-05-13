package org.craftercms.web.refactoring.handlers.editors.pages;

import org.craftercms.web.refactoring.handlers.editors.CSPageEditorHandler;
import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSArticlePageEditorHandler extends CSPageEditorHandler {

    private static final String CONTENT_TYPE = "/page/article";

    public CSArticlePageEditorHandler(WebDriver webDriver, String siteName) {
        super(webDriver, siteName, CONTENT_TYPE);
    }

    public void editBody(String text, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addImageToBody(String imageAbsolutePath, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void editAuthor(String text, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void editPublishDate(String date, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addReplaceAuthorImage(String imageAbsolutePath, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void editMetadataPageTitle(String text, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void editMetadataKeywords(String text, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void editMetadataDescription(String text, boolean clear){
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
