/**
 * 
 */
package org.craftercms.web.refactoring.basic;

import org.craftercms.web.refactoring.CSBaseTest;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.logging.Logger;

/**
 * @author Praveen C Elineni
 * @author Juan Avila
 *
 * The tests of this class use "raw" implementations
 * rather than calling the login methods found
 * in the parent class (which are meant to be used for common access in
 * other tests). This is to isolate the purpose of these tests (which is testing
 * login functionality).
 *
 */
public class LoginTests extends CSBaseTest {

    private static final Logger logger = Logger.getLogger("LoginTests.class");

    /**
     * Test wrong credentials login Functionality.
     */
	@Test
	public void testWrongCredentials() {
        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

		logger.info("logging using wrong credentials");
        csSessionHandler.login("wronguser", "worngpassword");

        logger.info("check current page is login error page");
        assertTrue(csNavigationHandler.isCurrentPage(loginErrorPageUrl));

	}

    /**
     * Test admin credentials login Functionality.
     */
    @Test
    public void testAdminLogin() {

        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

        logger.info("logging using admin credentials");
        csSessionHandler.login(adminUserName,adminPassword);

        logger.info("check current page is admin dashboard page");
        assertTrue(csNavigationHandler.isCurrentPage(String.format(unformattedUserDashboardUrl,adminUserName)));

    }

    /**
     * Test author credentials login Functionality.
     */
    @Test
    public void testAuthorLogin() {

        logger.info("navigate to login page");
        csNavigationHandler.navigateTo(loginPageUrl);

        logger.info("Logging using author credentials");
        csSessionHandler.login(authorUserName,authorPassword);

        logger.info("check current page is author dashboard page");
        assertTrue(csNavigationHandler.isCurrentPage(String.format(unformattedUserDashboardUrl,authorUserName)));

    }

}