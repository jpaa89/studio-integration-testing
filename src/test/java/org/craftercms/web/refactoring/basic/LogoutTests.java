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
 */
public class LogoutTests extends CSBaseTest {

    /**
     * Test Logout Functionality
     */
    @Test
    public void testLogout() {

        loginAsAdmin();

        navigateToSiteDashboardPage();

        logger.info("logout");
        csSessionHandler.logout();

        logger.info("check current page is a login page");
        assertTrue(csNavigationHandler.isCurrentPageALoginPage());

    }
}