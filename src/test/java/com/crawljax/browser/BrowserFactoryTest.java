package com.crawljax.browser;

import static org.junit.Assert.fail;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfigurationReader;

/**
 * This test, test the (public) operations from the BrowserFactory.
 * 
 * @author Stefan Lenselink <S.R.Lenselink@student.tudelft.nl>
 * @version $Id$
 */
public class BrowserFactoryTest {
	private static final int TIMEOUT = 10000; // 10 Sec.
	private BrowserFactory factory =
	        new BrowserFactory(BrowserType.firefox, 1, null, null, 400, 500);

	/**
	 * Request don't release and close the factory.
	 * 
	 * @throws InterruptedException
	 *             thrown from requestBrowser
	 */
	@Test
	public void testRequestClose() throws InterruptedException {

		factory.requestBrowser();
		factory.close();
	}

	/**
	 * Request a browser, release it and close the factory.
	 * 
	 * @throws InterruptedException
	 *             thrown from requestBrowser
	 */
	@Test
	public void testRequestReleaseClose() throws InterruptedException {
		EmbeddedBrowser b = factory.requestBrowser();
		factory.freeBrowser(b);
		factory.close();
	}

	/**
	 * Test a call to two times the close operation, after a browser request.
	 * 
	 * @throws InterruptedException
	 *             thrown from requestBrowser
	 */
	@Test(timeout = TIMEOUT)
	public void testDoubleClose() throws InterruptedException {
		factory.requestBrowser();
		factory.close();
		factory.close();

	}

	/**
	 * Test a call to close only.
	 */
	@Test(timeout = TIMEOUT)
	public void testCloseOnly() {
		factory.close();
	}

	/**
	 * Test a call to close only twice.
	 */
	@Test(timeout = TIMEOUT)
	public void testCloseOnlyTwoTimes() {
		factory.close();
		factory.close();
	}

	/**
	 * Test opening 4 browsers, 3 requested, 1 returned. close should be done within TIMEOUT.
	 * 
	 * @throws ConfigurationException
	 *             when config is not correct
	 * @throws InterruptedException
	 *             when the request for a browser is interupped
	 */
	@Test(timeout = TIMEOUT)
	public void testMultipleBrowsers() throws ConfigurationException, InterruptedException {
		CrawlSpecification spec = new CrawlSpecification("about:blank");
		// TODO Stefan. when NuberOfBrowsers specified; use that in stead...
		spec.setNumberOfThreads(4);
		CrawljaxConfiguration cfg = new CrawljaxConfiguration();
		cfg.setCrawlSpecification(spec);

		CrawljaxConfigurationReader reader = new CrawljaxConfigurationReader(cfg);

		try {

			BrowserFactory factory =
			        new BrowserFactory(reader.getBrowser(), reader.getCrawlSpecificationReader()
			                .getNumberOfThreads(), reader.getProxyConfiguration(), null, 1, 1);

			factory.requestBrowser();
			factory.requestBrowser();
			EmbeddedBrowser b1 = factory.requestBrowser();
			factory.freeBrowser(b1);

			factory.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}