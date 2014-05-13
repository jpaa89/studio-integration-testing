package org.craftercms.web.refactoring.basic;

import org.craftercms.web.refactoring.CSBaseTest;
import org.craftercms.web.refactoring.handlers.CSDashboardHandler;
import org.craftercms.web.basic.dashboard.widget.helpers.CSDashboardWidgetHandler;
import org.craftercms.web.basic.dashboard.widget.helpers.CSSimpleDashboardWidgetHandler;
import org.craftercms.web.refactoring.handlers.editors.pages.impl.CSAboutPageEditorHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan Méndez
 * @author Juan Avila
 *
 */
public  class DashboardTestsBase extends CSBaseTest {

    protected CSDashboardHandler csDashboardHandler;
    protected CSAboutPageEditorHandler aboutPageEditorHandler;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        csDashboardHandler = new CSDashboardHandler(driver);
        aboutPageEditorHandler = new CSAboutPageEditorHandler(driver,siteName);
    }



    /**
     * Test Dashboard Page Context Nav Functionality
     */
    @Test
    public void testContextNav() {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("check if context navigation bar header is visible");
        assertTrue(csDashboardHandler.getCsContextNavigationBarHandler().isNavigationHeaderVisible());

        logger.info("check if context navigation bar logo link is visible");
        assertTrue(csDashboardHandler.getCsContextNavigationBarHandler().isLogoLinkVisible());

        logger.info("check if context navigation drop-down toggler is visible");
        assertTrue(csDashboardHandler.getCsContextNavigationBarHandler().isDropDownTogglerVisible());

        logger.info("check if context navigation drop-down toggler text equals 'Site Content'");
        assertEquals(csDashboardHandler.getCsContextNavigationBarHandler().dropDownTogglerText(),"Site Content");

        logger.info("toggle/click drop-down menu");
        csDashboardHandler.getCsContextNavigationBarHandler().toggleDropDownMenu();

        logger.info("check drop-down menu is open/visible");
        assertTrue(csDashboardHandler.getCsContextNavigationBarHandler().isDropDownMenuOpen());

    }

    /**
     * Test Dashboard Page Title Functionality
     */
    @Test
    public void testSiteDashboardTitle() {
        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("Check if page title is visible");
        assertTrue(csDashboardHandler.isPageTitleVisible());

        logger.info("Check if the dashboard page title contains the site title");
        assertTrue(csDashboardHandler.pageTitle().contains(siteTitle));

    }

    /**
     * Test Dashboard Page My Recent Activity Functionality
     *
     * @throws InterruptedException
     */
    @Test
    public void testMyRecentActivity() throws InterruptedException {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("edit and save page");
        aboutPageEditorHandler.openEditFormJS();
        csNavigationHandler.switchToRecentlyOpenWindow();
        aboutPageEditorHandler.editInternalNameField(generateUpdateString("Updated-"),true);
        aboutPageEditorHandler.saveAndClose();
        csNavigationHandler.switchBackToPreviousWindow();

        logger.info("refresh dashboard");
        csNavigationHandler.refreshCurrentPage();

        logger.info("Check my-recent-activity widget for edited page");
        assertTrue(csDashboardHandler.getCsMyRecentActivityWidgetHandler().containsContent(aboutPageEditorHandler.getUri()));

    }

    /**
     * Test Dashboard Page Icon Guide
     */
    @Test
    public void testIconGuide() {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("Check icon-guide is displayed");
        assertTrue(csDashboardHandler.getCsIconGuideSimpleDashboardWidgetHandler().isVisible());

        logger.info("Check icon-guide text");
        assertTrue(csDashboardHandler.getCsIconGuideSimpleDashboardWidgetHandler().headerTitle().contains("Icon Guide"));
    }

    /**
     * Test Dashboard Page Footer
     */
    @Test
    public void testFooter() {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("Check footer exists");
        assertTrue(csDashboardHandler.isPageFooterVisible());

        logger.info("Check footer texts");
        //TODO fix this so it works for 2.3.X
        assertTrue(csDashboardHandler.pageFooter().contains("Alfresco Software"));
    }


    /**
     * Test Admin Dashboard Widgets collapsible states
     */
    @Test
    public void testAdminDashboardWidgets() throws InterruptedException {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        CSSimpleDashboardWidgetHandler[] simpleWidgetsHandlers = {
                csDashboardHandler.getCsGoLiveQueueWidgetHandler(),
                csDashboardHandler.getCsApprovedScheduledItemsWidgetHandler(),
                csDashboardHandler.getCsRecentlyMadeLiveWidgetHandler(),
                csDashboardHandler.getCsMyRecentActivityWidgetHandler(),
                csDashboardHandler.getCsIconGuideSimpleDashboardWidgetHandler()
        };

        CSDashboardWidgetHandler[] widgetsHandlers = {
                csDashboardHandler.getCsGoLiveQueueWidgetHandler(),
                csDashboardHandler.getCsApprovedScheduledItemsWidgetHandler(),
                csDashboardHandler.getCsRecentlyMadeLiveWidgetHandler(),
                csDashboardHandler.getCsMyRecentActivityWidgetHandler(),
        };

        logger.info("close/collapse the widgets");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            simpleWidgetHandler.toggleCloseOpen();
        }

        logger.info("check the widgets are closed/collapsed");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isClosed());
        }

        logger.info("check widgets menus are no longer displayed");
        for(CSDashboardWidgetHandler widgetHandler : widgetsHandlers){
            assertTrue(widgetHandler.allMenusHidden());
        }

        logger.info("refresh dashboard");
        csNavigationHandler.refreshCurrentPage();


        logger.info("check the widgets remained collapsed");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isClosed());
        }

        logger.info("open/expand the widgets");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            simpleWidgetHandler.toggleCloseOpen();
        }

        logger.info("check the widgets are open/expanded");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isOpen());
        }

        logger.info("check widgets menus are now visible");
        for(CSDashboardWidgetHandler widgetHandler : widgetsHandlers){
            assertTrue(widgetHandler.allMenusVisible());
        }

    }


    /**
     * Test Author Dashboard Widgets collapsible states
     */
    @Test
    public void testAuthorDashboardWidgets() throws InterruptedException {

        loginAsAuthor();

        navigateToSiteDashboardPage();

        CSSimpleDashboardWidgetHandler[] simpleWidgetsHandlers = {
                csDashboardHandler.getCsMyRecentActivityWidgetHandler(),
                csDashboardHandler.getCsIconGuideSimpleDashboardWidgetHandler()
        };

        CSDashboardWidgetHandler[] widgetsHandlers = {
                csDashboardHandler.getCsMyRecentActivityWidgetHandler(),
        };

        logger.info("close/collapse the widgets");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            simpleWidgetHandler.toggleCloseOpen();
        }

        logger.info("check the widgets are closed/collapsed");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isClosed());
        }

        logger.info("check widgets menus are no longer displayed");
        for(CSDashboardWidgetHandler widgetHandler : widgetsHandlers){
            assertTrue(widgetHandler.allMenusHidden());
        }

        logger.info("refresh dashboard");
        csNavigationHandler.refreshCurrentPage();


        logger.info("check the widgets remained collapsed");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isClosed());
        }

        logger.info("open/expand the widgets");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            simpleWidgetHandler.toggleCloseOpen();
        }

        logger.info("check the widgets are open/expanded");
        for(CSSimpleDashboardWidgetHandler simpleWidgetHandler : simpleWidgetsHandlers){
            assertTrue(simpleWidgetHandler.isOpen());
        }

        logger.info("check widgets menus are now visible");
        for(CSDashboardWidgetHandler widgetHandler : widgetsHandlers){
            assertTrue(widgetHandler.allMenusVisible());
        }

    }

}
