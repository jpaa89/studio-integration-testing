studio-integration-testing
==========================

Crafter Studio integration testing framework.

Setup
=====

Crafter Studio
    Login to alfresco and import the provided selenium-corporate.acp blueprint
	Create website with the selenium-corporate blueprint
	Create author user
	Add author user to created site with 'Collaborator' Role
Install Chromedriver
    https://code.google.com/p/selenium/wiki/ChromeDriver
Clone crafter-selenium-tests project from github
	https://github.com/craftercms/studio-integration-testing
Edit and update required properties in selenium.properties file (src/test/resources/selenium.properties)
    craftercms.live.deployer.path = Location where live content is deployed
    craftercms.preview.deployer.path = Location where preview content is deployed
	Server URL's and credentials
	Website Name and Title
Run required testcases to run above scenarios
	Location: studio-integration-testing/src/test/java/org/craftercms/web/
