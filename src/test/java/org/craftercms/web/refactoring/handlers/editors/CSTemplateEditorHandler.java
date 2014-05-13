package org.craftercms.web.refactoring.handlers.editors;

import org.openqa.selenium.WebDriver;

/**
 * @author Juan Avila
 */
public abstract class CSTemplateEditorHandler extends CSContentEditorHandler {


    public CSTemplateEditorHandler(WebDriver webDriver, String contentType) {
        super(webDriver, contentType);
    }

    public void openTemplateEditor(){
     //CStudioAuthoring.Operations.openTemplateEditor("/templates/web/article.ftl", "default");
    }

    /**
     * Wraps the given text to create an html comment.
     * Note: this method removes the "-" characters from the string.
     */
    public void editMarkupSafely(String commentText){
        //TODO finish this later
        String finalMarkup = "<!--"+commentText.replace("-","")+"-->";
    }


    public void update(){

    }

    public void cancel(){

    }

}
