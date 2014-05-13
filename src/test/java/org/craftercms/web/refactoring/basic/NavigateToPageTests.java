/**
 * 
 */
package org.craftercms.web.refactoring.basic;

import org.craftercms.web.refactoring.CSBaseTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Praveen C Elineni
 * @author Juan Avila
 *
 * The tests of this class use "raw" implementations
 * rather than calling the navigation methods found
 * in the parent class (which are meant to be used for common navigation in
 * other tests). This is to isolate the purpose of these tests (which is testing
 * navigation).
 *
 */
public class NavigateToPageTests extends CSBaseTest {

    /**
     * Navigate to Dashboard Page after Login
     */
    @Test
    public void testNavigateToDashboardPage() {
        String siteDashboardUrl;

        loginAsAdmin();

        logger.info("navigate to site dashboard page");
        siteDashboardUrl = String.format(unformattedSiteDashboardUrl,siteName);
        csNavigationHandler.navigateTo(siteDashboardUrl);

        logger.info("check current page is site dashboard page");
        assertTrue(csNavigationHandler.isCurrentPage(siteDashboardUrl));

    }

    /**
     * Navigate To Un-Authorized Access Page after failed Login 
     */
    @Test
    public void testUnauthorizedAccess() {
        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

        logger.info("logging using wrong credentials");
        csSessionHandler.login("wronguser", "worngpassword");

        logger.info("check current page is login error page");
        assertTrue(csNavigationHandler.isCurrentPage(loginErrorPageUrl));

        logger.info("navigate to site dashboard page");
        csNavigationHandler.navigateTo(String.format(unformattedSiteDashboardUrl,siteName));

        logger.info("check current page is a login page");
        assertTrue(csNavigationHandler.isCurrentPageALoginPage());

    }


}