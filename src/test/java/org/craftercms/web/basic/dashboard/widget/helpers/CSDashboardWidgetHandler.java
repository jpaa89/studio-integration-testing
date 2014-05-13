package org.craftercms.web.basic.dashboard.widget.helpers;

import org.craftercms.web.refactoring.handlers.CSHandler;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Juan Avila
 * TODO Warning this a very old implementation that needs a lot of changes. This should not be considered functional and has not been refactored at all!
 * TODO refactor this so it uses the new apprach (no asserts in the handlers, no properties -get them by parameter-, no methods named check, etc
 */
public abstract class CSDashboardWidgetHandler extends CSSimpleDashboardWidgetHandler {


    public CSDashboardWidgetHandler(WebDriver webDriver, String id) {
        super(webDriver, id);
    }

    public void selectContents(String[] contentUris) {
        checkCurrentPageIsDashboardPage();

        if(contentUris.length > 0){

            By inputElementsBy;
            List<WebElement> inputElements;
            StringBuilder inputsContainsIdXpathOr = new StringBuilder("contains(@id,'" + contentUris[0] + "')");

            for(int i = 1; i < contentUris.length; i++){
                inputsContainsIdXpathOr.append(" or contains(@id,'").append(contentUris[i]).append("')");
            }

            inputElementsBy = By.xpath("//tbody[@id='"+ divId +"-tbody']//input["+inputsContainsIdXpathOr+"]");

            inputElements = webDriver.findElements(inputElementsBy);

            if(!inputElements.isEmpty()){
                for(WebElement inputElement : inputElements){
                    if(!inputElement.isSelected()){
                        CStudioSeleniumUtil.javascriptClick(webDriver, inputElement);
                    }
                }
            }

        }
    }

    /**
     * Changes the number of shown items (show input)
     * @param numberOfContentItemsToShow number of items to show
     */
    public void changeShowNumber(int numberOfContentItemsToShow) {

        checkCurrentPageIsDashboardPage();

        // Element whose future staleness will help detect the widget has effectively changed
        final WebElement widgetTbody = webDriver.findElement(By.id(divId + "-tbody"));

        WebElement showBoxInput = webDriver.findElement(By.id("widget-showitems-"+ divId));
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_WEB_ELEMENT);
        showBoxInput.clear();
        showBoxInput.sendKeys(""+numberOfContentItemsToShow);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);
        showBoxInput.sendKeys(Keys.RETURN);
        CStudioSeleniumUtil.waitFor(TimeConstants.WAITING_SECONDS_LIGHT_KEY_SENDING_TASK);

        new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                try {
                    widgetTbody.isEnabled(); // looking forward to get the StaleElementReferenceException
                    return false;
                }
                catch(StaleElementReferenceException ignore) {
                    return true;
                }
            }
        });

        CStudioSeleniumUtil.waitForItemToDisplay(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id(divId +"-tbody"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id(divId +"-tbody"));

    }

    /**
     * Filter contents by pages
     */
    public void filterByPages() {
        changeFilter("Pages");
    }

    /**
     * Filter contents by components
     */
    public void filterByComponents() {
        changeFilter("Components");
    }

    /**
     * Filter contents by documents
     */
    public void filterByDocuments() {
        changeFilter("Documents");
    }

    /**
     * Filter contents by all
     */
    public void filterByAll() {
        changeFilter("All");
    }

    /**
     * Checks if only pages are being displayed
     * @return true if displays Pages only
     */
    public boolean isDisplayingPagesOnly() {
        return displaysOnlyContentsOfKind(DashboardWidgetLabel.KIND_PAGE);
    }

    /**
     * Checks if only components are being displayed
     * @return true if displays Components only
     */
    public boolean isDisplayingComponentsOnly() {
        return displaysOnlyContentsOfKind(DashboardWidgetLabel.KIND_COMPONENT);
    }

    /**
     * Checks if only documents are being displayed
     * @return true if displays Documents only
     */
    public boolean isDisplayingDocumentsOnly() {
        return displaysOnlyContentsOfKind(DashboardWidgetLabel.KIND_DOCUMENT);
    }

    /**
     * Checks if many kinds of contents are being displayed.
     * Important: Ignores level descriptors by checking using contains("Section Defaults").
     * @return true if displays more than one kind of item
     */
    public boolean isDisplayingMultipleKindsOfContents() {

        DashboardWidgetLabel[] dashboardWidgetLabelArray;
        String lastKind = "";
        String currentKind;
        boolean multipleKinds = false;

        dashboardWidgetLabelArray = dashboardWidgetLabels();

        if(dashboardWidgetLabelArray != null && dashboardWidgetLabelArray.length > 1) {

            int j = 0;

            while(j < dashboardWidgetLabelArray.length){
                if(dashboardWidgetLabelArray[j].getTitle().contains("Section Defaults")){
                    j++;
                }
                else{
                    lastKind = dashboardWidgetLabelArray[j].getKind();
                    j++;
                    break;
                }
            }

            //lastKind = dashboardWidgetLabelArray[0].getKind();
            for(int i = j; i < dashboardWidgetLabelArray.length; i++){
                if(dashboardWidgetLabelArray[i].getTitle().contains("Section Defaults")){
                    continue;
                }
                currentKind = dashboardWidgetLabelArray[i].getKind();
                if(!lastKind.equals(currentKind)){
                    multipleKinds = true;
                    break;
                }
                lastKind = currentKind;
            }
        }

        return multipleKinds;
    }

    /**
     * Checks if the widget contains the specified contents. This method looks for the contents withing the widget html
     * in its current mode -meaning that the number of shown items and the filter dictate the total amount
     * of possible existing of items-)
     * @param contentsUris contents uris to check
     */
    public boolean containsContents(String[] contentsUris) {

        checkCurrentPageIsDashboardPage();

        By inputElementsBy;
        List<WebElement> inputElements;
        StringBuilder inputsContainsIdXpathOr;
        boolean containsContents = false;

        if(contentsUris != null && contentsUris.length > 0) {

            inputsContainsIdXpathOr = new StringBuilder("contains(@id,'" + contentsUris[0] + "')");

            for(int i = 1; i < contentsUris.length; i++){
                inputsContainsIdXpathOr.append(" or contains(@id,'").append(contentsUris[i]).append("')");
            }

            inputElementsBy = By.xpath("//tbody[@id='"+ divId +"-tbody']//input["+inputsContainsIdXpathOr+"]");
            inputElements = webDriver.findElements(inputElementsBy);

            if(inputElements.size() >= contentsUris.length){
                containsContents = true;
            }

        }

        return containsContents;
    }

    /**
     * Checks if the widget contains the specified content. This method looks for the content withing the widget html
     * in its current mode -meaning that the number of shown items and the filter dictate the total amount
     * of possible existing of items-)
     * @param contentUri content uri to check
     * @return true if the widget contains the content/item
     */
    public boolean containsContent(String contentUri) {
        return  containsContents(new String[]{contentUri});
    }

    /**
     * Checks if all the contents are hidden (however they should exist within the html for them to be hidden)
     * @return true if all the contents are hidden
     */
    public boolean contentsAreHidden() {
        boolean contentsHidden = true;
        By inputElementsBy = By.xpath("//tbody[@id='"+ divId +"']//tr[contains(@class,'wcm-table-parent')]");
        List<WebElement> trElements = webDriver.findElements(inputElementsBy);

        if(!trElements.isEmpty()){
            for(final WebElement input : trElements){
                try {
                    new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {

                        public Boolean apply(WebDriver webDriver) {
                            return input.getAttribute("style").replace(" ","").contains("display:none");
                        }
                    });
                }
                catch (TimeoutException e){
                    contentsHidden = false;
                    break;
                }
            }
        }
        else{
            contentsHidden = false; // there are no contents therefore they can not be hidden
        }


        return contentsHidden;
    }

    /**
     * Checks if there are any elements within the widget. This method looks for the content withing the widget html
     * in its current mode -meaning that the number of shown items and the filter dictate the total amount
     * of possible existing of items-)
     * @return true if the widget has any elements
     */
    public boolean hasContents(){
        return countContents() > 0;
    }

    public boolean allMenusHidden(){
        boolean hiddenMenus = true;

        By menusBy = By.cssSelector("div#"+divId+" a.widget-expand-state");
        List<WebElement> menus = webDriver.findElements(menusBy);

        // This is coded this way because isDisplay may return false if the page hasn't loaded yet, so it could return
        // that a menu is not visible when it actually is.
        if(menus.size() > 0){

            for(WebElement menu : menus){
                if(!menu.getAttribute("style").replace(" ","").contains("display:none")){
                    hiddenMenus = false;
                    break;
                }
            }
        }
        else{
            hiddenMenus = false; // if there are no menus they cannot be considered as hidden
        }

        return hiddenMenus;

    }

    public boolean allMenusVisible(){
        By menusBy = By.cssSelector("div#"+divId+" a.widget-expand-state");
        final List<WebElement> menus = webDriver.findElements(menusBy);

        try {
            new WebDriverWait(webDriver,TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(ExpectedConditions.visibilityOfAllElements(menus));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Counts the contents of this widget. This method looks for the content withing the widget html
     * in its current mode -meaning that the number of shown items and the filter dictate the total amount
     * of possible existing of items-).
     * @return contents count
     */
    public int countContents() {
        checkCurrentPageIsDashboardPage();
        By inputElementsBy = By.xpath("//tbody[@id='" + divId + "-tbody']//input");
        return  webDriver.findElements(inputElementsBy).size();
    }

    /**
     * Returns an array containing the labels information of all the existing contents.
     * @return all the labels/contents information of all the existing contents
     */
    public DashboardWidgetLabel[] dashboardWidgetLabels() {

        By spanElementsBy = By.xpath("//tbody[@id='"+ divId +"-tbody']/tr/td[1]/div[1]/div[1]/span[preceding-sibling::input[1]]");
        final List<WebElement> spanElements = webDriver.findElements(spanElementsBy) ;
        int spanElementsSize = spanElements.size();
        StringBuilder stringTables = new StringBuilder();
        DashboardWidgetLabel[] dashboardWidgetLabelArray = new DashboardWidgetLabel[spanElementsSize];
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();
        }

        if(!spanElements.isEmpty()){

            new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(ExpectedConditions.visibilityOfAllElements(spanElements));

            stringTables.append("<root>");
            for (WebElement spanElement : spanElements) {
                stringTables.append(spanElement.getAttribute("title"));
            }
            stringTables.append("</root>");

            try {
                document = documentBuilder.parse(new ByteArrayInputStream(stringTables.toString().getBytes()));
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Element root = document.getDocumentElement();

            NodeList tables = root.getElementsByTagName("table");

            if(tables != null) {
                int tablesLength = tables.getLength();

                for(int i = 0; i < tablesLength; i++) {
                    NodeList trs = ((Element)tables.item(i)).getElementsByTagName("tr");

                    int trsCount = trs.getLength();

                    if(trsCount >= 5) {

                        String kind = trs.item(0).getFirstChild().getTextContent();
                        String title = trs.item(0).getLastChild().getTextContent();
                        String status = trs.item(1).getLastChild().getTextContent();
                        String lastEdited = trs.item(2).getLastChild().getTextContent();
                        String editedBy = trs.item(3).getLastChild().getTextContent();
                        String lockedBy = trs.item(4).getLastChild().getTextContent();
                        String scheduled = (trsCount == 6) ? trs.item(5).getLastChild().getTextContent() : "";

                        if(kind.contains(DashboardWidgetLabel.KIND_PAGE)){
                            kind = DashboardWidgetLabel.KIND_PAGE;
                        } else if(kind.contains(DashboardWidgetLabel.KIND_COMPONENT)){
                            kind = DashboardWidgetLabel.KIND_COMPONENT;
                        }
                        else if(kind.contains(DashboardWidgetLabel.KIND_DOCUMENT)){
                            kind = DashboardWidgetLabel.KIND_DOCUMENT;
                        }

                        dashboardWidgetLabelArray[i] = new DashboardWidgetLabel(kind,title.trim(),status.trim(),lastEdited.trim(),editedBy.trim(),lockedBy.trim(),scheduled.trim());

                    }

                }

            }


        }


        return dashboardWidgetLabelArray;
    }

    /**
     * Returns the label/content information for the given content
     * @param contentUri the content uri
     * @return the label/content information for the given content uri
     */
    public DashboardWidgetLabel dashboardWidgetLabel(String contentUri) {

        By spanElementsBy = By.xpath("//tbody[@id='"+ divId +"-tbody']/tr/td[1]/div[1]/div[1]/span[preceding-sibling::input[contains(@id,'"+contentUri+"') and position() = 1]]");
        final List<WebElement> spanElements = webDriver.findElements(spanElementsBy) ;
        StringBuilder stringTables = new StringBuilder();
        DashboardWidgetLabel dashboardWidgetLabel = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();
        }

        if(!spanElements.isEmpty() && spanElements.size() == 1){

            new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(ExpectedConditions.visibilityOfAllElements(spanElements));

            stringTables.append("<root>");
            stringTables.append(spanElements.get(0).getAttribute("title"));
            stringTables.append("</root>");

            try {
                if (documentBuilder != null) {
                    document = documentBuilder.parse(new ByteArrayInputStream(stringTables.toString().getBytes()));
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Element root;

            if (document != null) {
                root = document.getDocumentElement();

                NodeList tables = root.getElementsByTagName("table");

                if(tables != null && tables.getLength() == 1) {

                    NodeList trs = ((Element)tables.item(0)).getElementsByTagName("tr");

                    int trsCount = trs.getLength();

                    if(trsCount >= 5) {

                        String kind = trs.item(0).getFirstChild().getTextContent();
                        String title = trs.item(0).getLastChild().getTextContent();
                        String status = trs.item(1).getLastChild().getTextContent();
                        String lastEdited = trs.item(2).getLastChild().getTextContent();
                        String editedBy = trs.item(3).getLastChild().getTextContent();
                        String lockedBy = trs.item(4).getLastChild().getTextContent();
                        String scheduled = (trsCount == 6) ? trs.item(5).getLastChild().getTextContent() : "";

                        if(kind.contains(DashboardWidgetLabel.KIND_PAGE)){
                            kind = DashboardWidgetLabel.KIND_PAGE;
                        } else if(kind.contains(DashboardWidgetLabel.KIND_COMPONENT)){
                            kind = DashboardWidgetLabel.KIND_COMPONENT;
                        }
                        else if(kind.contains(DashboardWidgetLabel.KIND_DOCUMENT)){
                            kind = DashboardWidgetLabel.KIND_DOCUMENT;
                        }

                        if(status.contains(DashboardWidgetLabel.STATUS_DELETED)){
                            status = DashboardWidgetLabel.STATUS_DELETED;
                        } else if(status.contains(DashboardWidgetLabel.STATUS_IN_PROGRESS)){
                            status = DashboardWidgetLabel.STATUS_IN_PROGRESS;
                        }
                        else if(status.contains(DashboardWidgetLabel.STATUS_PROCESSING)){
                            status = DashboardWidgetLabel.STATUS_PROCESSING;
                        }
                        else if(status.contains(DashboardWidgetLabel.STATUS_SUBMITTED)){
                            status = DashboardWidgetLabel.STATUS_SUBMITTED;
                        }

                        dashboardWidgetLabel = new DashboardWidgetLabel(kind,title.trim(),status,lastEdited.trim(),editedBy.trim(),lockedBy.trim(),scheduled.trim());

                    }


                }

            }


        }


        return dashboardWidgetLabel;
    }

    /**
     * Checks the current driver url equals the dashboard url
     */
    protected void checkCurrentPageIsDashboardPage() {
        String siteName = CSHandler.seleniumProperties.getProperty("craftercms.sitename");
        String dashboardUrl = String.format(CSHandler.seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName);

        if(!webDriver.getCurrentUrl().equals(dashboardUrl)){
            throw new UnsupportedOperationException("Current driver URL is not dashboard URL");
        }
    }

    /**
     * Change the widget filter
     * @param filter kind of content to filter by
     */
    protected void changeFilter(String filter) {
        // Element whose future staleness will help detect the widget has effectively changed
        checkCurrentPageIsDashboardPage();

        final WebElement widgetTbody = webDriver.findElement(By.id(divId + "-tbody"));

        CStudioSeleniumUtil.clickOn(webDriver, By.xpath("//select[@id='widget-filterBy-"+ divId +"']/option[text()='"+filter+"']"));

        new WebDriverWait(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT).until(new ExpectedCondition<Boolean>() {

            public Boolean apply(WebDriver webDriver) {
                try {
                    widgetTbody.isEnabled(); // looking forward to get the StaleElementReferenceException
                    return false;
                }
                catch(StaleElementReferenceException ignore) {
                    return true;
                }
            }
        });

        CStudioSeleniumUtil.waitForItemToDisplay(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id(divId +"-tbody"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id(divId + "-tbody"));

    }

    /**
     * Check if only contents of the matching kind are being displayed.
     * Important: Ignores level descriptors by using contains("Section Defaults").
     * @param kind kind of content
     * @return true if only contents of the matching kind are being displayed
     */
    protected boolean displaysOnlyContentsOfKind(String kind) {
        boolean sameKind = true;

        DashboardWidgetLabel[] dashboardWidgetLabelArray = dashboardWidgetLabels();

        if(dashboardWidgetLabelArray != null) {
            for (DashboardWidgetLabel aDashboardWidgetLabel : dashboardWidgetLabelArray) {
                if (!kind.equals(aDashboardWidgetLabel.getKind())) {
                    if(aDashboardWidgetLabel.getTitle().contains("Section Defaults")){
                        continue;
                    }
                    sameKind = false;
                    break;
                }
            }
        }
        else {
            sameKind = false;
        }

        return sameKind;
    }





}
