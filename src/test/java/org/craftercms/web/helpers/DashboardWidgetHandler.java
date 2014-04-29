package org.craftercms.web.helpers;

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
import java.util.Properties;


/**
 * @author Juan Avila
 * Handles most of the necessary dashboard widget ui and content interactions
 */
public class DashboardWidgetHandler {

    private static final Properties seleniumProperties = new Properties();
    static {
        try {
            seleniumProperties.load(CStudioSeleniumUtil.class.getClassLoader().getResourceAsStream("selenium.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String id;

    /**
     * Creates a dashboard widget handler that will use the given id to perform ui and content interactions
     * @param id the id of the dashboard widget div
     */
    public DashboardWidgetHandler(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    /**
     * Selects (clicks the item checkbox) the specified contents
     * @param webDriver driver
     * @param contentUris contents uris
     */
    public void selectContents(WebDriver webDriver, String[] contentUris) {
        checkCurrentPageIsDashboardPage(webDriver);

        if(contentUris.length > 0){

            By inputElementsBy;
            List<WebElement> inputElements;
            StringBuilder inputsContainsIdXpathOr = new StringBuilder("contains(@id,'" + contentUris[0] + "')");

            for(int i = 1; i < contentUris.length; i++){
                inputsContainsIdXpathOr.append(" or contains(@id,'").append(contentUris[i]).append("')");
            }

            inputElementsBy = By.xpath("//tbody[@id='"+id+"-tbody']//input["+inputsContainsIdXpathOr+"]");

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
     * @param webDriver driver
     * @param numberOfContentItemsToShow number of items to show
     */
    public void changeShowNumber(WebDriver webDriver, int numberOfContentItemsToShow) {

        checkCurrentPageIsDashboardPage(webDriver);

        // Element whose future staleness will help detect the widget has effectively changed
        final WebElement widgetTbody = webDriver.findElement(By.id(id + "-tbody"));

        WebElement showBoxInput = webDriver.findElement(By.id("widget-showitems-"+id));
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

        CStudioSeleniumUtil.waitForItemToDisplay(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id(id+"-tbody"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id(id+"-tbody"));

    }

    /**
     * Filter contents by pages
     * @param webDriver driver
     */
    public void filterByPages(WebDriver webDriver) {
        changeFilter(webDriver,"Pages");
    }

    /**
     * Filter contents by components
     * @param webDriver driver
     */
    public void filterByComponents(WebDriver webDriver) {
        changeFilter(webDriver,"Components");
    }

    /**
     * Filter contents by documents
     * @param webDriver driver
     */
    public void filterByDocuments(WebDriver webDriver) {
        changeFilter(webDriver,"Documents");
    }

    /**
     * Filter contents by all
     * @param webDriver driver
     */
    public void filterByAll(WebDriver webDriver) {
        changeFilter(webDriver,"All");
    }

    /**
     * Checks if only pages are being displayed
     * @param webDriver driver
     * @return true if displays Pages only
     */
    public boolean isDisplayingPagesOnly(WebDriver webDriver) {
        return displaysOnlyContentsOfKind(webDriver, DashboardWidgetContentInfo.KIND_PAGE);
    }

    /**
     * Checks if only components are being displayed
     * @param webDriver driver
     * @return true if displays Components only
     */
    public boolean isDisplayingComponentsOnly(WebDriver webDriver) {
        return displaysOnlyContentsOfKind(webDriver, DashboardWidgetContentInfo.KIND_COMPONENT);
    }

    /**
     * Checks if only documents are being displayed
     * @param webDriver driver
     * @return true if displays Documents only
     */
    public boolean isDisplayingDocumentsOnly(WebDriver webDriver) {
        return displaysOnlyContentsOfKind(webDriver, DashboardWidgetContentInfo.KIND_DOCUMENT);
    }

    /**
     * Checks if many kinds of contents are being displayed.
     * Important: Ignores level descriptors by checking using contains("Section Defaults").
     * @param webDriver driver
     * @return true if displays more than one kind of item
     */
    public boolean isDisplayingMultipleKindsOfContents(WebDriver webDriver) {

        DashboardWidgetContentInfo[] dashboardWidgetContentInfoArray;
        String lastKind = "";
        String currentKind;
        boolean multipleKinds = false;

        dashboardWidgetContentInfoArray = dashboardWidgetContentsInfo(webDriver);

        if(dashboardWidgetContentInfoArray != null && dashboardWidgetContentInfoArray.length > 1) {

            int j = 0;

            while(j < dashboardWidgetContentInfoArray.length){
                if(dashboardWidgetContentInfoArray[j].getTitle().contains("Section Defaults")){
                    j++;
                }
                else{
                    lastKind = dashboardWidgetContentInfoArray[j].getKind();
                    j++;
                    break;
                }
            }

            //lastKind = dashboardWidgetContentInfoArray[0].getKind();
            for(int i = j; i < dashboardWidgetContentInfoArray.length; i++){
                if(dashboardWidgetContentInfoArray[i].getTitle().contains("Section Defaults")){
                    continue;
                }
                currentKind = dashboardWidgetContentInfoArray[i].getKind();
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
     * @param webDriver the driver
     * @param contentsUris contents uris to check
     */
    public boolean containsContents(WebDriver webDriver, String[] contentsUris) {

        checkCurrentPageIsDashboardPage(webDriver);

        By inputElementsBy;
        List<WebElement> inputElements;
        StringBuilder inputsContainsIdXpathOr;
        boolean containsContents = false;

        if(contentsUris != null && contentsUris.length > 0) {

            inputsContainsIdXpathOr = new StringBuilder("contains(@id,'" + contentsUris[0] + "')");

            for(int i = 1; i < contentsUris.length; i++){
                inputsContainsIdXpathOr.append(" or contains(@id,'").append(contentsUris[i]).append("')");
            }

            inputElementsBy = By.xpath("//tbody[@id='"+id+"-tbody']//input["+inputsContainsIdXpathOr+"]");
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
     * @param webDriver driver
     * @param contentUri content uri to check
     * @return true if the widget contains the content/item
     */
    public boolean containsContent(WebDriver webDriver, String contentUri) {
        return  containsContents(webDriver,new String[]{contentUri});
    }

    /**
     * Checks if all the contents are hidden (however they should exist within the html for them to be hidden)
     * @param webDriver driver
     * @return true if all the contents are hidden
     */
    public boolean contentsAreHidden(WebDriver webDriver) {
        boolean contentsHidden = true;
        By inputElementsBy = By.xpath("//tbody[@id='"+id+"']//tr[contains(@class,'wcm-table-parent')]");
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
     * @param webDriver driver
     * @return true if the widget has any elements
     */
    public boolean hasContents(WebDriver webDriver){
        return countContents(webDriver) > 0;
    }

    /**
     * Counts the contents of this widget. This method looks for the content withing the widget html
     * in its current mode -meaning that the number of shown items and the filter dictate the total amount
     * of possible existing of items-).
     * @param webDriver driver
     * @return contents count
     */
    public int countContents(WebDriver webDriver) {
        checkCurrentPageIsDashboardPage(webDriver);
        By inputElementsBy = By.xpath("//tbody[@id='" + id + "-tbody']//input");
        return  webDriver.findElements(inputElementsBy).size();
    }

    /**
     * Returns an array containing the labels information of all the existing contents.
     * @param webDriver driver
     * @return all the labels/contents information of all the existing contents
     */
    public DashboardWidgetContentInfo[] dashboardWidgetContentsInfo(WebDriver webDriver) {

        By spanElementsBy = By.xpath("//tbody[@id='"+id+"-tbody']/tr/td[1]/div[1]/div[1]/span[preceding-sibling::input[1]]");
        final List<WebElement> spanElements = webDriver.findElements(spanElementsBy) ;
        int spanElementsSize = spanElements.size();
        StringBuilder stringTables = new StringBuilder();
        DashboardWidgetContentInfo[] dashboardWidgetContentInfoArray = new DashboardWidgetContentInfo[spanElementsSize];
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

                        if(kind.contains(DashboardWidgetContentInfo.KIND_PAGE)){
                            kind = DashboardWidgetContentInfo.KIND_PAGE;
                        } else if(kind.contains(DashboardWidgetContentInfo.KIND_COMPONENT)){
                            kind = DashboardWidgetContentInfo.KIND_COMPONENT;
                        }
                        else if(kind.contains(DashboardWidgetContentInfo.KIND_DOCUMENT)){
                            kind = DashboardWidgetContentInfo.KIND_DOCUMENT;
                        }

                        dashboardWidgetContentInfoArray[i] = new DashboardWidgetContentInfo(kind,title.trim(),status.trim(),lastEdited.trim(),editedBy.trim(),lockedBy.trim(),scheduled.trim());

                    }

                }

            }


        }


        return dashboardWidgetContentInfoArray;
    }

    /**
     * Returns the label/content information for the given content
     * @param webDriver driver
     * @param contentUri the content uri
     * @return the label/content information for the given content uri
     */
    public DashboardWidgetContentInfo dashboardWidgetContentInfo(WebDriver webDriver, String contentUri) {

        By spanElementsBy = By.xpath("//tbody[@id='"+id+"-tbody']/tr/td[1]/div[1]/div[1]/span[preceding-sibling::input[contains(@id,'"+contentUri+"') and position() = 1]]");
        final List<WebElement> spanElements = webDriver.findElements(spanElementsBy) ;
        StringBuilder stringTables = new StringBuilder();
        DashboardWidgetContentInfo dashboardWidgetContentInfo = null;
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

                        if(kind.contains(DashboardWidgetContentInfo.KIND_PAGE)){
                            kind = DashboardWidgetContentInfo.KIND_PAGE;
                        } else if(kind.contains(DashboardWidgetContentInfo.KIND_COMPONENT)){
                            kind = DashboardWidgetContentInfo.KIND_COMPONENT;
                        }
                        else if(kind.contains(DashboardWidgetContentInfo.KIND_DOCUMENT)){
                            kind = DashboardWidgetContentInfo.KIND_DOCUMENT;
                        }

                        if(status.contains(DashboardWidgetContentInfo.STATUS_DELETED)){
                            status = DashboardWidgetContentInfo.STATUS_DELETED;
                        } else if(status.contains(DashboardWidgetContentInfo.STATUS_IN_PROGRESS)){
                            status = DashboardWidgetContentInfo.STATUS_IN_PROGRESS;
                        }
                        else if(status.contains(DashboardWidgetContentInfo.STATUS_PROCESSING)){
                            status = DashboardWidgetContentInfo.STATUS_PROCESSING;
                        }
                        else if(status.contains(DashboardWidgetContentInfo.STATUS_SUBMITTED)){
                            status = DashboardWidgetContentInfo.STATUS_SUBMITTED;
                        }

                        dashboardWidgetContentInfo = new DashboardWidgetContentInfo(kind,title.trim(),status,lastEdited.trim(),editedBy.trim(),lockedBy.trim(),scheduled.trim());

                    }


                }

            }


        }


        return dashboardWidgetContentInfo;
    }

    /**
     * Checks the current driver url equals the dashboard url
     * @param webDriver driver
     */
    protected void checkCurrentPageIsDashboardPage(WebDriver webDriver) {
        String siteName = seleniumProperties.getProperty("craftercms.sitename");
        String dashboardUrl = String.format(seleniumProperties.getProperty("craftercms.site.dashboard.url"), siteName);

        if(!webDriver.getCurrentUrl().equals(dashboardUrl)){
            throw new UnsupportedOperationException("Current driver URL is not dashboard URL");
        }
    }

    /**
     * Change the widget filter
     * @param webDriver driver
     * @param filter kind of content to filter by
     */
    protected void changeFilter(WebDriver webDriver, String filter) {
        // Element whose future staleness will help detect the widget has effectively changed
        checkCurrentPageIsDashboardPage(webDriver);

        final WebElement widgetTbody = webDriver.findElement(By.id(id + "-tbody"));

        CStudioSeleniumUtil.clickOn(webDriver, By.xpath("//select[@id='widget-filterBy-"+id+"']/option[text()='"+filter+"']"));

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

        CStudioSeleniumUtil.waitForItemToDisplay(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT,By.id(id+"-tbody"));
        CStudioSeleniumUtil.waitForItemToBeEnabled(webDriver, TimeConstants.WAITING_SECONDS_WEB_ELEMENT, By.id(id + "-tbody"));

    }

    /**
     * Check if only contents of the matching kind are being displayed.
     * Important: Ignores level descriptors by checking using contains("Section Defaults").
     * @param webDriver driver
     * @param kind kind of content
     * @return true if only contents of the matching kind are being displayed
     */
    protected boolean displaysOnlyContentsOfKind(WebDriver webDriver, String kind) {
        boolean sameKind = true;

        DashboardWidgetContentInfo[] dashboardWidgetContentInfoArray = dashboardWidgetContentsInfo(webDriver);

        if(dashboardWidgetContentInfoArray != null) {
            for (DashboardWidgetContentInfo aDashboardWidgetContentInfo : dashboardWidgetContentInfoArray) {
                if (!kind.equals(aDashboardWidgetContentInfo.getKind())) {
                    if(aDashboardWidgetContentInfo.getTitle().contains("Section Defaults")){
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
