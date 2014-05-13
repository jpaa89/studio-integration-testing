package org.craftercms.web.refactoring;

import org.craftercms.web.refactoring.handlers.CSNavigationHandler;
import org.craftercms.web.refactoring.handlers.CSSessionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;

import static org.junit.Assert.assertTrue;

public class CSBaseTest extends CSSeleniumBaseTest {

    protected CSSessionHandler csSessionHandler;
    protected CSNavigationHandler csNavigationHandler;


    public CSBaseTest() { }

    @Before
    public void setUp() throws Exception {
        csSessionHandler = new CSSessionHandler(driver);
        csNavigationHandler = new CSNavigationHandler(driver);
    }

    /**
     * Commonly used method to navigate to the dashboard page
     */
    public void navigateToSiteDashboardPage(){
        String siteDashboardUrl;

        logger.info("navigate to site dashboard");
        siteDashboardUrl = String.format(unformattedSiteDashboardUrl,siteName);
        csNavigationHandler.navigateTo(siteDashboardUrl);

        logger.info("check current page is site dashboard page");
        assertTrue(csNavigationHandler.isCurrentPage(siteDashboardUrl));
    }

    /**
     * Commonly used method to log in as an admin
     */
    public void loginAsAdmin() {
        String adminDashboardUrl;

        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

        logger.info("logging using admin credentials");
        csSessionHandler.login(adminUserName, adminPassword);

        logger.info("check current page is admin dashboard page");
        adminDashboardUrl = String.format(unformattedUserDashboardUrl,adminUserName);
        assertTrue(csNavigationHandler.isCurrentPage(adminDashboardUrl));

    }

    /**
     * Commonly used method to log in as an author
     */
    public void loginAsAuthor() {
        String authorDashboardUrl;

        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

        logger.info("Logging using author credentials");
        csSessionHandler.login(authorUserName, authorPassword);

        logger.info("check current page is author dashboard page");
        authorDashboardUrl = String.format(unformattedUserDashboardUrl,authorUserName);
        assertTrue(csNavigationHandler.isCurrentPage(authorDashboardUrl));

    }

    /**
     * Commonly used method to log out
     */
    @After
    public void logout() {

        if(csNavigationHandler.isCurrentPageALoginPage()){
            logger.info("already logged out");
        }
        else {
            logger.info("logout");
            csSessionHandler.logout();

            logger.info("check current page is login page");
            assertTrue(csNavigationHandler.isCurrentPageALoginPage());
        }

    }

    //TODO rethink this if necessary
    public String generateUpdateString(String prefix){
        return prefix+System.currentTimeMillis();
    }


}