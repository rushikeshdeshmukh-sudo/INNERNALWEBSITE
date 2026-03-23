package com.buzzzworth.base;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.github.bonigarcia.wdm.WebDriverManager;

	/**
	 * BaseDriver — shared driver lifecycle + low-level Selenium helpers.
	 * Every test class extends this.
	 */
	public class BaseDriver {

	    protected static final Logger LOG = Logger.getLogger(BaseDriver.class.getName());

	    public static final String BASE_URL = "https://marketing.buzzzworth.com/";

	    protected WebDriver          driver;
	    protected WebDriverWait      wait;
	    protected JavascriptExecutor js;
	    protected Actions            actions;

	    // ── Driver init ────────────────────────────────────────────────────────────

	    @BeforeClass(alwaysRun = true)
	    public void initDriver() {
	        String browser   = System.getProperty("browser",   "chrome").toLowerCase();

	        if ("firefox".equals(browser)) {
	            WebDriverManager.firefoxdriver().setup();
	            FirefoxOptions opts = new FirefoxOptions();
	            driver = new FirefoxDriver(opts);
	        } else {
	            WebDriverManager.chromedriver().setup();
	            ChromeOptions opts = new ChromeOptions();
	            opts.addArguments("--no-sandbox", "--disable-dev-shm-usage",
	                              "--window-size=1440,900", "--disable-extensions");
	            driver = new ChromeDriver(opts);
	        }
	     // Maximize browser window
	        driver.manage().window().maximize();

	        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	        wait    = new WebDriverWait(driver, Duration.ofSeconds(20));
	        js      = (JavascriptExecutor) driver;
	        actions = new Actions(driver);
	    }

	    @AfterClass(alwaysRun = true)
	    public void quitDriver() {
	        if (driver != null) { driver.quit(); LOG.info("■ Driver quit"); }
	    }

	    // ── Navigation ─────────────────────────────────────────────────────────────

	    protected void loadHome() {
	        driver.get(BASE_URL);
	        waitForPageReady();
	    }

	    protected void waitForPageReady() {
	        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
	        wait.until(d -> "complete".equals(js.executeScript("return document.readyState;")));
	    }

	    // ── Element helpers ────────────────────────────────────────────────────────

	    /** Multi-strategy element finder — CSS first, then XPath. */
	    protected WebElement find(String... selectors) {
	        for (String sel : selectors) {
	            try {
	                List<WebElement> els = sel.startsWith("//")
	                    ? driver.findElements(By.xpath(sel))
	                    : driver.findElements(By.cssSelector(sel));
	                for (WebElement el : els) {
	                    if (el.isDisplayed()) {
							return el;
						}
	                }
	            } catch (Exception ignored) {}
	        }
	        return null;
	    }

	    /** Returns all visible elements matching any of the given selectors. */

	    protected List<WebElement> findAll(String selector) {
	        try {
	            return driver.findElements(By.cssSelector(selector));
	        } catch (Exception e) {
	            return List.of();
	        }
	    }

	    protected void scrollTo(WebElement el) {
	        js.executeScript("arguments[0].scrollIntoView({block:'center',behavior:'smooth'});", el);
	        sleep(350);
	    }

	    protected void safeClick(WebElement el) {
	        try { scrollTo(el); el.click(); }
	        catch (ElementClickInterceptedException | StaleElementReferenceException ex) {
	            js.executeScript("arguments[0].click();", el);
	        }
	    }

	    protected void clearType(WebElement el, String text) {
	        scrollTo(el); el.clear(); el.sendKeys(text);
	    }

	    protected boolean imgLoaded(WebElement img) {
	        try {
	            return Boolean.TRUE.equals(js.executeScript(
	                "return arguments[0].complete && arguments[0].naturalWidth > 0;", img));
	        } catch (Exception e) { return false; }
	    }

	    protected String bodyText() {
	        try { return driver.findElement(By.tagName("body")).getText().toLowerCase(); }
	        catch (Exception e) { return ""; }
	    }

	    protected long scrollY() {
	        return ((Number) js.executeScript("return window.scrollY;")).longValue();
	    }

	    protected void scrollToBottom() {
	        js.executeScript("window.scrollTo(0,document.body.scrollHeight);"); sleep(700);
	    }

	    protected void scrollToTop() {
	        js.executeScript("window.scrollTo(0,0);"); sleep(400);
	    }

	    protected void sleep(long ms) {
	        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
	    }

	    /** XPath text-match helper (case-insensitive via translate). */
	    protected String xpathText(String text) {
	        String low = text.toLowerCase();
	        return "//*[contains(translate(normalize-space(.)," +
	               "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + low + "')]";
	    }

	    /** True if page body or any HTML5 field signals a validation error. */
	    protected boolean validationFired() {
	        String body = bodyText();
	        if (body.contains("required") || body.contains("invalid") || body.contains("please fill")
	                || body.contains("fill all") || body.contains("please enter")) {
				return true;
			}
	        for (WebElement inp : driver.findElements(By.cssSelector("input,select,textarea"))) {
	            try {
	                if (Boolean.TRUE.equals(js.executeScript(
	                        "return arguments[0].validity && !arguments[0].validity.valid;", inp))) {
						return true;
					}
	            } catch (Exception ignored) {}
	        }
	        return false;
	    }
	
	
	protected void click(WebElement el) {
        try {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
            el.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", el);
        }
    }
}
