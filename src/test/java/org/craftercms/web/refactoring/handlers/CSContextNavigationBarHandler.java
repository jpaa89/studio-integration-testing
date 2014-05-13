package org.craftercms.web.refactoring.handlers;

import org.craftercms.web.refactoring.util.CSSeleniumUtil;
import org.craftercms.web.util.TimeConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 */
public class CSContextNavigationBarHandler extends CSHandler {

    //TODO reconsider deleting the dropdown menu handler, active content handler and search handler
    protected CSActiveContentHandler csActiveContentHandler;
    protected CSContextSearchHandler csContextSearchHandler;

    public CSContextNavigationBarHandler(WebDriver webDriver) {
        super(webDriver);
    }

    public String dropDownTogglerText() {
        By dropDownTogglerBy = By.id("acn-dropdown-toggler");
        WebElement dropDownToggler = webDriver.findElement(dropDownTogglerBy);
        return dropDownToggler.getText();
    }

    public void toggleDropDownMenu() {
        By dropDownTogglerBy = By.id("acn-dropdown-toggler");
        CSSeleniumUtil.clickOn(webDriver, dropDownTogglerBy);
    }

    public boolean isNavigationHeaderVisible(){
        By haderBy = By.id("authoringContextNavHeader");
        return isDisplayed(TimeConstants.WAITING_SECONDS_WEB_ELEMENT,haderBy);
    }

    public boolean isLogoLinkVisible(){
        By logoLinkBy = By.id("acn-wcm-logo-link");
        return isDisplayed(TimeConstants.WAITING_SECONDS_WEB_ELEMENT,logoLinkBy);
    }

    public boolean isDropDownTogglerVisible() {
        By dropDownTogglerBy = By.id("acn-dropdown-toggler");
        return isDisplayed(TimeConstants.WAITING_SECONDS_WEB_ELEMENT,dropDownTogglerBy);
    }

    public boolean isDropDownMenuOpen() {
        By dropDownMenuWrapperBy = By.id("acn-dropdown-menu-wrapper");
        return isDisplayed(TimeConstants.WAITING_SECONDS_WEB_ELEMENT,dropDownMenuWrapperBy);
    }

    


    //dropdown
    //logout

    //search
    //logo
    //activecontent

}
