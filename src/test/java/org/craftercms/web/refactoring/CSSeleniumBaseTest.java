package org.craftercms.web.refactoring;

import org.craftercms.web.refactoring.util.CSTimeConstants;
import org.craftercms.web.util.CStudioSeleniumUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Juan Avila
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-config.xml")
@Configuration
@PropertySource("classpath:selenium.properties")
public class CSSeleniumBaseTest {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    protected WebDriver driver;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    //####### Configuration Path ######

    @Value("${craftercms.preview.deployer.content.root}")
    protected String previewDeployerContentRoot;

    @Value("${craftercms.preview.deployer.metadata.root}")
    protected String previewDeployerMetadataRoot;

    @Value("${craftercms.live.deployer.path}")
    protected String liveDeployerPath;

    @Value("${craftercms.preview.deployer.path}")
    protected String previewDeployerPath;

    //###### Crafter URL's ######

    @Value("${craftercms.base.url}")
    protected String baseUrl;

    @Value("${craftercms.base.live.url}")
    protected String baseLiveUrl;

    @Value("${craftercms.login.page.url}")
    protected String loginPageUrl;

    @Value("${craftercms.login.error.page.url}")
    protected String loginErrorPageUrl;

    //craftercms.user.dashboard.url = http://127.0.0.1:8080/share/page/user/%s/dashboard
    @Value("${craftercms.user.dashboard.url}")
    protected String unformattedUserDashboardUrl;

    //craftercms.site.dashboard.url = http://127.0.0.1:8080/share/page/site/%s/dashboard
    @Value("${craftercms.site.dashboard.url}")
    protected String unformattedSiteDashboardUrl;

    //craftercms.site.admin.console.url = http://127.0.0.1:8080/share/page/site/%s/cstudio-admin-console
    @Value("${craftercms.site.admin.console.url}")
    protected String unformattedSiteAdminConsoleUrl;

    //craftercms.site.search.url = /search?q=%s
    @Value("${craftercms.site.search.url}")
    protected String unformattedSiteSearchUrl;

    @Value("${craftercms.site.site.finder}")
    protected String siteSiteFinderUrl;

    //###### Users ######

    @Value("${craftercms.admin.username}")
    protected String adminUserName;

    @Value("${craftercms.admin.password}")
    protected String adminPassword;

    @Value("${craftercms.author.username}")
    protected String authorUserName;

    @Value("${craftercms.author.password}")
    protected String authorPassword;

    //###### Misc ######

    @Value("${craftercms.sitename}")
    protected String siteName;

    @Value("${craftercms.sitetitle}")
    protected String siteTitle;

    @Value("${craftercms.assets.to.tests.path}")
    protected String assetsToTestPath;

    public CSSeleniumBaseTest() { }

    @Before
    public void setUp() throws Exception {
        driver.manage().timeouts().implicitlyWait(CSTimeConstants.WAITING_SECONDS_WEB_ELEMENT, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @PreDestroy
    public void tearDown() throws Exception {
        CStudioSeleniumUtil.exit(driver);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }




}
