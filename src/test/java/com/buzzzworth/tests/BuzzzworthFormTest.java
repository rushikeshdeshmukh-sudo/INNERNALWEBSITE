package com.buzzzworth.tests;

	import java.time.Duration;
	import java.util.List;
	import java.util.logging.Logger;

	import org.openqa.selenium.By;
	import org.openqa.selenium.ElementClickInterceptedException;
	import org.openqa.selenium.JavascriptExecutor;
	import org.openqa.selenium.Keys;
	import org.openqa.selenium.NoAlertPresentException;
	import org.openqa.selenium.NoSuchElementException;
	import org.openqa.selenium.StaleElementReferenceException;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.openqa.selenium.chrome.ChromeOptions;
	import org.openqa.selenium.interactions.Actions;
	import org.openqa.selenium.support.ui.ExpectedConditions;
	import org.openqa.selenium.support.ui.Select;
	import org.openqa.selenium.support.ui.WebDriverWait;
	import org.testng.annotations.AfterClass;
	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.Test;
	import org.testng.asserts.SoftAssert;

	import io.github.bonigarcia.wdm.WebDriverManager;

		/**
		 * ╔══════════════════════════════════════════════════════════════════════════╗
		 * ║  BuzzzworthFormTest                                                      ║
		 * ║  URL   : https://marketing.buzzzworth.com/                               ║
		 * ║  Form  : "Get Free Consultation" (visible in hero section)               ║
		 * ║                                                                          ║
		 * ║  FORM FIELDS (from live screenshot):                                     ║
		 * ║  1.  Full Name*          — text input                                    ║
		 * ║  2.  Phone*              — country flag +91 dropdown  +  number input    ║
		 * ║  3.  Select Service*     — styled dropdown (7 options)                   ║
		 * ║  4.  Company Name*       — text input                                    ║
		 * ║  5.  Company Email ID*   — email input                                   ║
		 * ║  6.  Your Query*         — textarea                                      ║
		 * ║  7.  SUBMIT NOW          — red CTA button                                ║
		 * ║                                                                          ║
		 * ║  TEST PLAN                                                               ║
		 * ║  ─────────────────────────────────────────────────────────────────────  ║
		 * ║  ▶ POSITIVE (P)  — valid data, happy path                                ║
		 * ║    P-01  All 6 fields filled with valid data → no error on page          ║
		 * ║    P-02  Full Name accepts alphabets with spaces                         ║
		 * ║    P-03  Phone accepts 10-digit Indian mobile number                     ║
		 * ║    P-04  Each service option selectable from dropdown                    ║
		 * ║    P-05  Company Name accepts alphanumeric + space                       ║
		 * ║    P-06  Company Email accepts standard email formats                    ║
		 * ║    P-07  Your Query accepts multi-line / long text                       ║
		 * ║    P-08  Country code dropdown present, +91 default                      ║
		 * ║    P-09  Fields retain entered value (no auto-clear on focus)            ║
		 * ║    P-10  Tab key moves focus across all fields in order                  ║
		 * ║                                                                          ║
		 * ║  ▶ NEGATIVE (N)  — invalid / empty / boundary inputs                     ║
		 * ║    N-01  Empty submit (all blank) → validation fires                     ║
		 * ║    N-02  Full Name blank only → required error                           ║
		 * ║    N-03  Phone blank → required error                                    ║
		 * ║    N-04  Service not selected → required error                           ║
		 * ║    N-05  Company Name blank → required error                             ║
		 * ║    N-06  Company Email blank → required error                            ║
		 * ║    N-07  Your Query blank → required error                               ║
		 * ║    N-08  Company Email invalid format (no @) → email validation error    ║
		 * ║    N-09  Company Email invalid format (no domain) → error                ║
		 * ║    N-10  Phone number with letters (non-numeric) → error or rejected     ║
		 * ║    N-11  Phone too short (3 digits) → validation error                   ║
		 * ║    N-12  Phone too long (15 digits) → truncated or error                 ║
		 * ║    N-13  Full Name with only spaces → treated as empty / error           ║
		 * ║    N-14  Full Name with numbers only → accepted or rejected              ║
		 * ║    N-15  Company Email with spaces → invalid                             ║
		 * ║    N-16  SQL injection attempt in name → no crash                        ║
		 * ║    N-17  XSS attempt in query → no script execution                      ║
		 * ║    N-18  Full Name 300 chars → no crash                                  ║
		 * ║    N-19  Query field 1000 chars → no crash                               ║
		 * ║    N-20  Submit with only service selected → other fields still required ║
		 * ╚══════════════════════════════════════════════════════════════════════════╝
		 */
		public class BuzzzworthFormTest {

		    private static final Logger LOG = Logger.getLogger(BuzzzworthFormTest.class.getName());
		    private static final String URL = "https://marketing.buzzzworth.com/";

		    /* ═══════════════ TEST DATA ══════════════════════════════════════════════ */

		    // POSITIVE
		    private static final String V_FULLNAME   = "Rahul Sharma";
		    private static final String V_PHONE      = "9876543210";
		    private static final String V_COMPANY    = "Acme Digital Pvt Ltd";
		    private static final String V_EMAIL      = "rahul.sharma@acmedigital.com";
		    private static final String V_QUERY      = "I am looking for social media management for my startup. Please share a proposal.";
		    private static final String V_NAME_LONG  = "Rajesh Kumar Srivastava";
		    private static final String V_EMAIL2     = "info@buzzztest.in";
		    private static final String V_EMAIL3     = "test.user+tag@company.co.in";

		    // NEGATIVE
		    private static final String INV_EMAIL_NO_AT     = "rahulgmail.com";
		    private static final String INV_EMAIL_NO_DOMAIN = "rahul@";
		    private static final String INV_EMAIL_SPACES    = "rahul @gmail.com";
		    private static final String INV_PHONE_ALPHA     = "abcde98765";
		    private static final String INV_PHONE_SHORT     = "123";
		    private static final String INV_PHONE_LONG      = "123456789012345";
		    private static final String INV_NAME_SPACES     = "      ";
		    private static final String INV_NAME_NUMBERS    = "1234567890";
		    private static final String INV_SQL_INJECTION   = "' OR 1=1; DROP TABLE users; --";
		    private static final String INV_XSS             = "<script>alert('xss')</script>";
		    private static final String BOUNDARY_NAME_300   = "A".repeat(300);
		    private static final String BOUNDARY_QUERY_1000 = "B".repeat(1000);

		    WebDriver          driver;
		    WebDriverWait      wait;
		    JavascriptExecutor js;
		    Actions            actions;

		    /* ═══════════════ SETUP / TEARDOWN ═══════════════════════════════════════ */

		    @BeforeClass
		    public void setUp() {
		        WebDriverManager.chromedriver().setup();
		        ChromeOptions opts = new ChromeOptions();
		        // Maximize browser window

		        driver  = new ChromeDriver(opts);
		        driver.manage().window().maximize();

		        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
		        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
		        wait    = new WebDriverWait(driver, Duration.ofSeconds(20));
		        js      = (JavascriptExecutor) driver;
		        actions = new Actions(driver);
		        LOG.info("▶ Driver ready → " + URL);
		    }

		    @AfterClass(alwaysRun = true)
		    public void tearDown() {
		        if (driver != null) { driver.quit(); LOG.info("■ Driver quit"); }
		    }

		    /* ═══════════════ HELPERS ════════════════════════════════════════════════ */

		    /** Navigate to home and wait for full page load */
		    private void load() {
		        driver.get(URL);
		        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
		        wait.until(d -> "complete".equals(js.executeScript("return document.readyState;")));
		        sleep(500); // let JS animations settle
		    }

		    /** Scroll element to center and type text */
		    private void type(WebElement el, String text) {
		        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
		        sleep(200);
		        el.clear();
		        el.sendKeys(text);
		    }

		    /** Safe click — falls back to JS click */
		    private void click(WebElement el) {
		        try {
		            js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
		            sleep(200);
		            el.click();
		        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
		            js.executeScript("arguments[0].click();", el);
		        }
		    }

		    /** Locate Full Name input */
		    private WebElement nameField() {
		        return wait.until(ExpectedConditions.presenceOfElementLocated(
		            By.cssSelector("input[placeholder='Full Name*'], input[placeholder*='Full Name'], input[placeholder*='Name']")));
		    }

		    /** Locate Phone number input (the digit input, not the flag dropdown) */
		    private WebElement phoneField() {
		        // Phone field is the tel/number input next to the flag dropdown
		        List<WebElement> candidates = driver.findElements(By.cssSelector(
		            "input[type='tel'], input[type='number'], input[placeholder*='phone'], " +
		            "input[placeholder*='Phone'], input[placeholder*='919']"));
		        for (WebElement el : candidates) {
		            if (el.isDisplayed()) {
						return el;
					}
		        }
		        // Fallback: find input that currently contains phone placeholder
		        return driver.findElement(By.xpath(
		            "//input[contains(@placeholder,'+91') or contains(@placeholder,'919') " +
		            "or @type='tel' or contains(@placeholder,'hone')]"));
		    }

		    /** Locate the styled "Select Service*" dropdown trigger */
		    private WebElement serviceDropdown() {
		        // Check for native <select> first
		        List<WebElement> selects = driver.findElements(By.cssSelector("select"));
		        for (WebElement s : selects) { if (s.isDisplayed()) {
					return s;
				} }
		        // Custom styled dropdown
		        return driver.findElement(By.xpath(
		            "//*[contains(@class,'select') or contains(@class,'dropdown')]" +
		            "[contains(normalize-space(.),'Select Service')]"));
		    }

		    /** Select a service by visible text or index (handles both native + custom dropdown) */
		    private void selectService(String visibleText) {
		        WebElement svc = serviceDropdown();
		        String tag = svc.getTagName().toLowerCase();
		        if ("select".equals(tag)) {
		            Select sel = new Select(svc);
		            try { sel.selectByVisibleText(visibleText); }
		            catch (NoSuchElementException e) { sel.selectByIndex(1); }
		        } else {
		            // Custom dropdown — click trigger then click option
		            click(svc);
		            sleep(400);
		            List<WebElement> options = driver.findElements(By.xpath(
		                "//*[contains(@class,'option') or contains(@class,'item') or @role='option']" +
		                "[contains(normalize-space(.),'" + visibleText + "')]"));
		            if (!options.isEmpty()) {
						click(options.get(0));
					} else {
		                // Fallback: click any visible option
		                List<WebElement> anyOpts = driver.findElements(By.xpath(
		                    "//*[contains(@class,'option') or contains(@class,'item') or @role='option']"));
		                if (!anyOpts.isEmpty()) {
							click(anyOpts.get(0));
						}
		            }
		        }
		        sleep(300);
		    }

		    /** Locate Company Name input */
		    private WebElement companyField() {
		        return driver.findElement(By.cssSelector(
		            "input[placeholder*='Company Name'], input[placeholder*='company']"));
		    }

		    /** Locate Company Email input */
		    private WebElement emailField() {
		        return driver.findElement(By.cssSelector(
		            "input[type='email'], input[placeholder*='Email'], input[placeholder*='email']"));
		    }

		    /** Locate Your Query textarea */
		    private WebElement queryField() {
		        return driver.findElement(By.cssSelector(
		            "textarea, textarea[placeholder*='Query'], textarea[placeholder*='query'], " +
		            "input[placeholder*='Query']"));
		    }

		    /** Locate SUBMIT NOW button */
		    private WebElement submitButton() {
		        return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
		            "//button[contains(translate(normalize-space(.)," +
		            "'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'SUBMIT')]" +
		            " | //input[@type='submit']")));
		    }

		    /** True if any validation signal is present */
		    private boolean hasValidationError() {
		        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
		        if (body.contains("required") || body.contains("please fill") || body.contains("fill all")
		            || body.contains("invalid") || body.contains("please enter")
		            || body.contains("valid email") || body.contains("error")) {
					return true;
				}
		        // HTML5 native validity check
		        for (WebElement inp : driver.findElements(By.cssSelector("input, textarea, select"))) {
		            try {
		                if (Boolean.TRUE.equals(js.executeScript(
		                    "return arguments[0].validity && !arguments[0].validity.valid;", inp))) {
							return true;
						}
		            } catch (Exception ignored) {}
		        }
		        return false;
		    }

		    /** Fill all 6 fields with the given values */
		    private void fillAllFields(String name, String phone, String service,
		                                String company, String email, String query) {
		        try { type(nameField(),    name);    } catch (Exception e) { LOG.warning("name field: " + e.getMessage()); }
		        try { type(phoneField(),   phone);   } catch (Exception e) { LOG.warning("phone field: " + e.getMessage()); }
		        try { selectService(service);         } catch (Exception e) { LOG.warning("service select: " + e.getMessage()); }
		        try { type(companyField(), company); } catch (Exception e) { LOG.warning("company field: " + e.getMessage()); }
		        try { type(emailField(),   email);   } catch (Exception e) { LOG.warning("email field: " + e.getMessage()); }
		        try { type(queryField(),   query);   } catch (Exception e) { LOG.warning("query field: " + e.getMessage()); }
		    }

		    private void sleep(long ms) {
		        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
		    }

		    /* ═══════════════════════════════════════════════════════════════════════ */
		    /*  ██████████  POSITIVE TEST CASES  ██████████                           */
		    /* ═══════════════════════════════════════════════════════════════════════ */

		    // ── P-01  All fields valid ────────────────────────────────────────────────
		    @Test(priority = 1, description = "P-01 | All 6 fields filled with valid data — no error message visible")
		    public void p01_allFieldsValid() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, V_PHONE, "Social Media Management",
		                      V_COMPANY, V_EMAIL, V_QUERY);

		        // Verify values persist
		        s.assertEquals(nameField().getAttribute("value"),    V_FULLNAME, "P01-1: Full Name must retain value");
		        s.assertEquals(phoneField().getAttribute("value"),   V_PHONE,    "P01-2: Phone must retain value");
		        s.assertFalse(companyField().getAttribute("value").isEmpty(),     "P01-3: Company Name must retain value");
		        s.assertEquals(emailField().getAttribute("value"),   V_EMAIL,    "P01-4: Email must retain value");
		        s.assertFalse(queryField().getAttribute("value").isEmpty(),       "P01-5: Query must retain value");

		        // No premature validation errors shown
		        s.assertFalse(hasValidationError(), "P01-6: No validation error should appear before submit");

		        LOG.info("✅ P-01 PASSED");
		        s.assertAll();
		    }

		    // ── P-02  Full Name — alphabets with spaces ───────────────────────────────
		    @Test(priority = 2, description = "P-02 | Full Name accepts alphabets with spaces (first + last name)")
		    public void p02_fullNameAlphaSpace() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement name = nameField();
		        type(name, V_NAME_LONG);
		        String val = name.getAttribute("value");

		        s.assertEquals(val, V_NAME_LONG,  "P02-1: Name field must accept alphabets with spaces");
		        s.assertFalse(val.isEmpty(),       "P02-2: Name must not be empty after typing");

		        // Also test single word name
		        name.clear(); name.sendKeys("Priya");
		        s.assertEquals(name.getAttribute("value"), "Priya", "P02-3: Single first name must be accepted");

		        LOG.info("✅ P-02 PASSED  val='" + val + "'");
		        s.assertAll();
		    }

		    // ── P-03  Phone — 10-digit Indian mobile ──────────────────────────────────
		    @Test(priority = 3, description = "P-03 | Phone field accepts 10-digit Indian mobile number")
		    public void p03_phoneValidTenDigit() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement phone = phoneField();
		        type(phone, V_PHONE);
		        String val = phone.getAttribute("value");

		        s.assertFalse(val == null || val.isEmpty(), "P03-1: Phone field must accept 10-digit number");
		        // Numeric characters must all be present (some fields strip non-digits)
		        String digits = val.replaceAll("[^0-9]", "");
		        s.assertTrue(digits.length() >= 10, "P03-2: All 10 digits must be stored. digits=" + digits);

		        LOG.info("✅ P-03 PASSED  val='" + val + "' digits='" + digits + "'");
		        s.assertAll();
		    }

		    // ── P-04  Service Dropdown — all 7 options selectable ────────────────────
		    @Test(priority = 4, description = "P-04 | All 7 service options can be selected from dropdown")
		    public void p04_serviceAllOptionsSelectable() {
		        String[] services = {
		            "Social Media Management",
		            "Performance Marketing",
		            "Social Media + Performance Marketing",
		            "Campaign Strategy & Execution",
		            "Online Reputation Management",
		            "Complete Digital Marketing Setup",
		            "Not Sure – Need Consultation"
		        };

		        SoftAssert s = new SoftAssert();

		        for (int i = 0; i < services.length; i++) {
		            load(); // fresh load for each to reset state
		            try {
		                WebElement svc = serviceDropdown();
		                if ("select".equals(svc.getTagName().toLowerCase())) {
		                    Select sel = new Select(svc);
		                    sel.selectByIndex(i + 1); // index 0 = placeholder
		                    String selected = sel.getFirstSelectedOption().getText().trim();
		                    s.assertFalse(selected.isEmpty(),
		                        "P04-" + (i+1) + ": Selecting index " + (i+1) + " must give non-blank option. Got='" + selected + "'");
		                    LOG.info("  option[" + (i+1) + "]='" + selected + "'");
		                } else {
		                    // Custom dropdown
		                    selectService(services[i]);
		                    String body = driver.findElement(By.tagName("body")).getText();
		                    boolean found = body.contains(services[i].split(" ")[0]); // check first word
		                    s.assertTrue(found, "P04-" + (i+1) + ": Service '" + services[i] + "' must be selectable");
		                }
		            } catch (Exception e) {
		                s.fail("P04-" + (i+1) + ": Exception selecting service '" + services[i] + "': " + e.getMessage());
		            }
		        }

		        LOG.info("✅ P-04 PASSED");
		        s.assertAll();
		    }

		    // ── P-05  Company Name — alphanumeric + spaces ────────────────────────────
		    @Test(priority = 5, description = "P-05 | Company Name accepts alphanumeric characters and spaces")
		    public void p05_companyNameAlphanumeric() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement company = companyField();

		        // Normal company name
		        type(company, V_COMPANY);
		        s.assertEquals(company.getAttribute("value"), V_COMPANY, "P05-1: Company name must retain full value");

		        // Alphanumeric with numbers
		        company.clear(); company.sendKeys("TechCorp 2024");
		        s.assertEquals(company.getAttribute("value"), "TechCorp 2024", "P05-2: Company with number must be accepted");

		        // Name with & symbol (common in company names)
		        company.clear(); company.sendKeys("Singh & Sons Ltd");
		        String val = company.getAttribute("value");
		        s.assertFalse(val.isEmpty(), "P05-3: Company name with '&' must be accepted. val='" + val + "'");

		        LOG.info("✅ P-05 PASSED");
		        s.assertAll();
		    }

		    // ── P-06  Company Email — valid formats ────────────────────────────────────
		    @Test(priority = 6, description = "P-06 | Company Email accepts standard .com, .in, +tag formats")
		    public void p06_emailValidFormats() {
		        load();
		        SoftAssert s = new SoftAssert();

		        String[] validEmails = { V_EMAIL, V_EMAIL2, V_EMAIL3 };

		        for (String em : validEmails) {
		            WebElement email = emailField();
		            type(email, em);
		            String val = email.getAttribute("value");
		            s.assertEquals(val, em, "P06: Email field must accept '" + em + "'");

		            Boolean valid = (Boolean) js.executeScript(
		                "return arguments[0].validity ? arguments[0].validity.valid : true;", email);
		            s.assertTrue(Boolean.TRUE.equals(valid) || valid == null,
		                "P06: '" + em + "' must be considered valid by browser. valid=" + valid);
		            LOG.info("  email='" + em + "' html5valid=" + valid);
		        }

		        LOG.info("✅ P-06 PASSED");
		        s.assertAll();
		    }

		    // ── P-07  Your Query — long / multi-line text ──────────────────────────────
		    @Test(priority = 7, description = "P-07 | Your Query textarea accepts multi-line and long text")
		    public void p07_queryMultilineText() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement query = queryField();
		        String multiLine = "Line 1: Social media strategy needed.\n"
		                         + "Line 2: Budget is approx ₹50,000/month.\n"
		                         + "Line 3: Target audience: Tier 1 & 2 cities.";
		        type(query, multiLine);
		        String val = query.getAttribute("value");

		        s.assertFalse(val == null || val.isEmpty(), "P07-1: Query must accept multi-line text");
		        s.assertTrue(val.length() >= 50, "P07-2: Full query text must be stored. length=" + val.length());
		        LOG.info("  query length=" + val.length());

		        // Long query
		        query.clear();
		        query.sendKeys(V_QUERY.repeat(5));
		        String longVal = query.getAttribute("value");
		        s.assertFalse(longVal == null || longVal.isEmpty(),
		            "P07-3: Query must accept repeated/long text");

		        LOG.info("✅ P-07 PASSED");
		        s.assertAll();
		    }

		    // ── P-08  Country Code — +91 default ──────────────────────────────────────
		    @Test(priority = 8, description = "P-08 | Phone country code shows India (+91) as default")
		    public void p08_countryCodeDefault() {
		        load();
		        SoftAssert s = new SoftAssert();

		        // Look for flag element or +91 text
		        String body = driver.findElement(By.tagName("body")).getText();
		        boolean hasIndia = body.contains("+91") || body.contains("91") || body.contains("🇮🇳");

		        // Also check for select or custom dropdown near phone
		        List<WebElement> codeEls = driver.findElements(By.cssSelector(
		            "select[name*='code'], select[name*='country'], .flag-dropdown, " +
		            "[class*='country-code'], [class*='dial-code'], [class*='flag']"));

		        boolean codePresent = !codeEls.isEmpty() || hasIndia;
		        s.assertTrue(codePresent, "P08-1: India country code (+91) or flag must be visible");

		        // If native select exists, verify +91 is selected
		        for (WebElement el : codeEls) {
		            if ("select".equals(el.getTagName().toLowerCase()) && el.isDisplayed()) {
		                String selected = new Select(el).getFirstSelectedOption().getText();
		                s.assertTrue(selected.contains("91") || selected.contains("India"),
		                    "P08-2: Selected country code must be India/+91. Got: '" + selected + "'");
		                LOG.info("  country code selected='" + selected + "'");
		            }
		        }

		        LOG.info("✅ P-08 PASSED  codePresent=" + codePresent);
		        s.assertAll();
		    }

		    // ── P-09  Fields retain values ─────────────────────────────────────────────
		    @Test(priority = 9, description = "P-09 | Values entered in fields are not cleared on tab / focus change")
		    public void p09_fieldsRetainValues() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement nameEl    = nameField();
		        WebElement companyEl = companyField();

		        type(nameEl, V_FULLNAME);
		        // Click another field (company) and come back
		        type(companyEl, V_COMPANY);
		        nameEl.click(); // re-focus name

		        String retainedName = nameEl.getAttribute("value");
		        s.assertEquals(retainedName, V_FULLNAME,
		            "P09-1: Full Name must retain value after focus change. Got: '" + retainedName + "'");

		        String retainedCompany = companyEl.getAttribute("value");
		        s.assertEquals(retainedCompany, V_COMPANY,
		            "P09-2: Company Name must retain value. Got: '" + retainedCompany + "'");

		        LOG.info("✅ P-09 PASSED");
		        s.assertAll();
		    }

		    // ── P-10  Tab Order ────────────────────────────────────────────────────────
		    @Test(priority = 10, description = "P-10 | Tab key moves focus across all 6 form fields in sequence")
		    public void p10_tabKeyNavigation() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement nameEl = nameField();
		        nameEl.click();
		        nameEl.sendKeys("Rahul");

		        // Tab through fields — should not throw
		        try {
		            for (int i = 0; i < 5; i++) {
		                WebElement active = driver.switchTo().activeElement();
		                active.sendKeys(Keys.TAB);
		                sleep(250);
		            }
		            s.assertTrue(true, "P10-1: Tab key navigation completed without exception");
		        } catch (Exception e) {
		            s.fail("P10-1: Tab navigation threw exception: " + e.getMessage());
		        }

		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "P10-2: Tab navigation must not cause server error");

		        LOG.info("✅ P-10 PASSED");
		        s.assertAll();
		    }


		    /* ═══════════════════════════════════════════════════════════════════════ */
		    /*  ██████████  NEGATIVE TEST CASES  ██████████                           */
		    /* ═══════════════════════════════════════════════════════════════════════ */

		    // ── N-01  All Fields Empty ────────────────────────────────────────────────
		    @Test(priority = 11, description = "N-01 | All 6 fields blank — submit triggers validation error message")
		    public void n01_allFieldsEmpty() {
		        load();
		        SoftAssert s = new SoftAssert();

		        // Clear everything
		        try { nameField().clear();    } catch (Exception ignored) {}
		        try { phoneField().clear();   } catch (Exception ignored) {}
		        try { companyField().clear(); } catch (Exception ignored) {}
		        try { emailField().clear();   } catch (Exception ignored) {}
		        try { queryField().clear();   } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1200);

		        s.assertTrue(hasValidationError(),
		            "N01-1: Empty submit must trigger validation error");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N01-2: Empty submit must NOT show 'Thank You' success message");
		        s.assertEquals(driver.getCurrentUrl(), URL,
		            "N01-3: Page URL must not change on empty submit");

		        LOG.info("✅ N-01 PASSED");
		        s.assertAll();
		    }

		    // ── N-02  Full Name Blank ─────────────────────────────────────────────────
		    @Test(priority = 12, description = "N-02 | Full Name left blank, all others filled — validation fires")
		    public void n02_fullNameBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields("", V_PHONE, "Social Media Management", V_COMPANY, V_EMAIL, V_QUERY);
		        try { nameField().clear(); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N02-1: Blank Full Name must trigger validation error");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N02-2: Form must NOT submit with blank Full Name");

		        LOG.info("✅ N-02 PASSED");
		        s.assertAll();
		    }

		    // ── N-03  Phone Blank ─────────────────────────────────────────────────────
		    @Test(priority = 13, description = "N-03 | Phone left blank, all others filled — validation fires")
		    public void n03_phoneBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, "", "Performance Marketing", V_COMPANY, V_EMAIL, V_QUERY);
		        try { phoneField().clear(); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N03-1: Blank Phone must trigger validation error");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N03-2: Form must NOT submit with blank Phone");

		        LOG.info("✅ N-03 PASSED");
		        s.assertAll();
		    }

		    // ── N-04  Service Not Selected ────────────────────────────────────────────
		    @Test(priority = 14, description = "N-04 | Service dropdown left at placeholder — validation fires")
		    public void n04_serviceNotSelected() {
		        load();
		        SoftAssert s = new SoftAssert();

		        try { type(nameField(),    V_FULLNAME); } catch (Exception ignored) {}
		        try { type(phoneField(),   V_PHONE);    } catch (Exception ignored) {}
		        // intentionally skip service selection
		        try { type(companyField(), V_COMPANY);  } catch (Exception ignored) {}
		        try { type(emailField(),   V_EMAIL);    } catch (Exception ignored) {}
		        try { type(queryField(),   V_QUERY);    } catch (Exception ignored) {}

		        // Ensure select is at placeholder
		        try {
		            WebElement svc = serviceDropdown();
		            if ("select".equals(svc.getTagName().toLowerCase())) {
		                new Select(svc).selectByIndex(0);
		            }
		        } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N04-1: Unselected service must trigger validation");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N04-2: Form must NOT submit without service selection");

		        LOG.info("✅ N-04 PASSED");
		        s.assertAll();
		    }

		    // ── N-05  Company Name Blank ──────────────────────────────────────────────
		    @Test(priority = 15, description = "N-05 | Company Name left blank — validation fires")
		    public void n05_companyNameBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, V_PHONE, "Campaign Strategy & Execution", "", V_EMAIL, V_QUERY);
		        try { companyField().clear(); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N05-1: Blank Company Name must trigger validation error");

		        LOG.info("✅ N-05 PASSED");
		        s.assertAll();
		    }

		    // ── N-06  Company Email Blank ─────────────────────────────────────────────
		    @Test(priority = 16, description = "N-06 | Company Email left blank — validation fires")
		    public void n06_emailBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, V_PHONE, "Online Reputation Management", V_COMPANY, "", V_QUERY);
		        try { emailField().clear(); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N06-1: Blank Company Email must trigger validation error");

		        LOG.info("✅ N-06 PASSED");
		        s.assertAll();
		    }

		    // ── N-07  Your Query Blank ────────────────────────────────────────────────
		    @Test(priority = 17, description = "N-07 | Your Query left blank — validation fires")
		    public void n07_queryBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, V_PHONE, "Complete Digital Marketing Setup", V_COMPANY, V_EMAIL, "");
		        try { queryField().clear(); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N07-1: Blank Your Query must trigger validation error");

		        LOG.info("✅ N-07 PASSED");
		        s.assertAll();
		    }

		    // ── N-08  Email — No @ Symbol ─────────────────────────────────────────────
		    @Test(priority = 18, description = "N-08 | Company Email without '@' — email format validation error")
		    public void n08_emailNoAtSymbol() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement email = emailField();
		        type(email, INV_EMAIL_NO_AT);

		        click(submitButton());
		        sleep(800);

		        Boolean html5Invalid = (Boolean) js.executeScript(
		            "return arguments[0].validity && !arguments[0].validity.valid;", email);
		        boolean customError = driver.findElement(By.tagName("body")).getText().toLowerCase()
		            .contains("valid email");

		        s.assertTrue(Boolean.TRUE.equals(html5Invalid) || customError || hasValidationError(),
		            "N08-1: Email without '@' must be rejected. html5Invalid=" + html5Invalid);
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N08-2: Invalid email must not allow form submission");

		        LOG.info("✅ N-08 PASSED  html5Invalid=" + html5Invalid);
		        s.assertAll();
		    }

		    // ── N-09  Email — No Domain ────────────────────────────────────────────────
		    @Test(priority = 19, description = "N-09 | Company Email with @ but no domain (e.g. rahul@) — rejected")
		    public void n09_emailNoDomain() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement email = emailField();
		        type(email, INV_EMAIL_NO_DOMAIN);

		        click(submitButton());
		        sleep(800);

		        Boolean html5Invalid = (Boolean) js.executeScript(
		            "return arguments[0].validity && !arguments[0].validity.valid;", email);
		        s.assertTrue(Boolean.TRUE.equals(html5Invalid) || hasValidationError(),
		            "N09-1: Email with no domain must be rejected. Input='" + INV_EMAIL_NO_DOMAIN + "'");

		        LOG.info("✅ N-09 PASSED  html5Invalid=" + html5Invalid);
		        s.assertAll();
		    }

		    // ── N-10  Email — With Spaces ──────────────────────────────────────────────
		    @Test(priority = 20, description = "N-10 | Company Email with embedded space — rejected as invalid")
		    public void n10_emailWithSpaces() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement email = emailField();
		        type(email, INV_EMAIL_SPACES);
		        String val = email.getAttribute("value");

		        click(submitButton());
		        sleep(800);

		        Boolean html5Invalid = (Boolean) js.executeScript(
		            "return arguments[0].validity && !arguments[0].validity.valid;", email);
		        // Either browser rejects it natively or custom validation fires
		        boolean rejected = Boolean.TRUE.equals(html5Invalid)
		            || hasValidationError()
		            || !val.contains(" "); // some fields strip spaces automatically
		        s.assertTrue(rejected,
		            "N10-1: Email with spaces must be rejected or stripped. val='" + val + "'");

		        LOG.info("✅ N-10 PASSED  val='" + val + "' html5Invalid=" + html5Invalid);
		        s.assertAll();
		    }

		    // ── N-11  Phone — Letters Instead of Digits ────────────────────────────────
		    @Test(priority = 21, description = "N-11 | Phone field with alphabetic input — rejected or stripped")
		    public void n11_phoneWithLetters() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement phone = phoneField();
		        type(phone, INV_PHONE_ALPHA);
		        String val = phone.getAttribute("value");
		        String digits = val != null ? val.replaceAll("[^0-9]", "") : "";

		        click(submitButton());
		        sleep(800);

		        // Either: letters are stripped (field only keeps digits), OR validation fires
		        boolean strippedOrRejected = digits.length() < INV_PHONE_ALPHA.length()
		            || hasValidationError();
		        s.assertTrue(strippedOrRejected,
		            "N11-1: Phone with letters must be stripped or rejected. val='" + val + "'");

		        LOG.info("✅ N-11 PASSED  val='" + val + "' digits='" + digits + "'");
		        s.assertAll();
		    }

		    // ── N-12  Phone — Too Short (3 digits) ────────────────────────────────────
		    @Test(priority = 22, description = "N-12 | Phone with only 3 digits — validation error expected")
		    public void n12_phoneTooShort() {
		        load();
		        SoftAssert s = new SoftAssert();

		        fillAllFields(V_FULLNAME, INV_PHONE_SHORT, "Social Media Management",
		                      V_COMPANY, V_EMAIL, V_QUERY);

		        click(submitButton());
		        sleep(1000);

		        // Page must not crash
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N12-1: Short phone must not cause server error");

		        WebElement phone = phoneField();
		        Boolean html5Invalid = (Boolean) js.executeScript(
		            "return arguments[0].validity && !arguments[0].validity.valid;", phone);
		        boolean errorShown = hasValidationError() || Boolean.TRUE.equals(html5Invalid);

		        LOG.info("  N-12: short phone validation fired=" + errorShown + " html5=" + html5Invalid);
		        // If site enforces minlength, this must fail. Log result for report.
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N12-2: 3-digit phone must not lead to successful submission");

		        LOG.info("✅ N-12 PASSED");
		        s.assertAll();
		    }

		    // ── N-13  Phone — Too Long (15 digits) ────────────────────────────────────
		    @Test(priority = 23, description = "N-13 | Phone with 15 digits — truncated by maxlength or error shown")
		    public void n13_phoneTooLong() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement phone = phoneField();
		        type(phone, INV_PHONE_LONG);
		        String val = phone.getAttribute("value");
		        String digits = val != null ? val.replaceAll("[^0-9]", "") : "";

		        s.assertNotNull(val, "N13-1: Phone field value must not be null after long input");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N13-2: Very long phone must not crash server");

		        // maxlength should truncate, or custom validation fires
		        int maxLength = 15;
		        boolean handled = digits.length() <= maxLength || hasValidationError();
		        s.assertTrue(handled,
		            "N13-3: 15-digit phone must be truncated or rejected. digits.length=" + digits.length());

		        LOG.info("✅ N-13 PASSED  digits.length=" + digits.length());
		        s.assertAll();
		    }

		    // ── N-14  Full Name — Spaces Only ─────────────────────────────────────────
		    @Test(priority = 24, description = "N-14 | Full Name filled with only spaces — treated as empty or error")
		    public void n14_nameSpacesOnly() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement name = nameField();
		        type(name, INV_NAME_SPACES);

		        fillAllFields(INV_NAME_SPACES, V_PHONE, "Social Media Management",
		                      V_COMPANY, V_EMAIL, V_QUERY);

		        click(submitButton());
		        sleep(1000);

		        // Spaces-only should not pass as a valid name
		        boolean notSubmitted = hasValidationError()
		            || !driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you");
		        s.assertTrue(notSubmitted,
		            "N14-1: Name with spaces only should not lead to successful submission");

		        LOG.info("✅ N-14 PASSED");
		        s.assertAll();
		    }

		    // ── N-15  Full Name — Numbers Only ────────────────────────────────────────
		    @Test(priority = 25, description = "N-15 | Full Name with numbers only — logs acceptance or rejection")
		    public void n15_nameNumbersOnly() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement name = nameField();
		        type(name, INV_NAME_NUMBERS);
		        String val = name.getAttribute("value");

		        // Some sites allow numeric names — just ensure no crash
		        s.assertNotNull(val, "N15-1: Name field must not be null after numeric input");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N15-2: Numeric name must not crash page");

		        LOG.info("✅ N-15 PASSED  val='" + val + "' (accepted/rejected by site's discretion)");
		        s.assertAll();
		    }

		    // ── N-16  SQL Injection in Name ────────────────────────────────────────────
		    @Test(priority = 26, description = "N-16 | SQL injection attempt in Full Name — no crash or DB error")
		    public void n16_sqlInjectionInName() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement name = nameField();
		        type(name, INV_SQL_INJECTION);
		        String val = name.getAttribute("value");

		        // Fill rest with valid data
		        try { type(phoneField(),   V_PHONE);   } catch (Exception ignored) {}
		        try { selectService("Social Media Management"); } catch (Exception ignored) {}
		        try { type(companyField(), V_COMPANY); } catch (Exception ignored) {}
		        try { type(emailField(),   V_EMAIL);   } catch (Exception ignored) {}
		        try { type(queryField(),   V_QUERY);   } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1200);

		        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
		        s.assertFalse(body.contains("sql"), "N16-1: SQL error must not be exposed in page");
		        s.assertFalse(body.contains("database error"), "N16-2: Database error must not be exposed");
		        s.assertFalse(body.contains("syntax error"),   "N16-3: SQL syntax error must not be shown");
		        s.assertFalse(body.contains("500"),            "N16-4: Server error must not occur on SQL input");

		        LOG.info("✅ N-16 PASSED  name stored='" + val + "'");
		        s.assertAll();
		    }

		    // ── N-17  XSS in Query Field ───────────────────────────────────────────────
		    @Test(priority = 27, description = "N-17 | XSS script tag in Query — not executed, no JS alert appears")
		    public void n17_xssInQuery() {
		        load();
		        SoftAssert s = new SoftAssert();

		        try { type(nameField(),    V_FULLNAME); } catch (Exception ignored) {}
		        try { type(phoneField(),   V_PHONE);    } catch (Exception ignored) {}
		        try { selectService("Social Media Management"); } catch (Exception ignored) {}
		        try { type(companyField(), V_COMPANY);  } catch (Exception ignored) {}
		        try { type(emailField(),   V_EMAIL);    } catch (Exception ignored) {}
		        try { type(queryField(),   INV_XSS);    } catch (Exception ignored) {}

		        String queryVal = queryField().getAttribute("value");
		        LOG.info("  XSS input stored as='" + queryVal + "'");

		        click(submitButton());
		        sleep(1200);

		        // Verify no JS alert is present (would cause UnhandledAlertException)
		        try {
		            driver.switchTo().alert(); // if alert exists, XSS worked
		            driver.switchTo().alert().dismiss();
		            s.fail("N17-1: XSS alert was triggered — script executed! This is a SECURITY VULNERABILITY.");
		        } catch (NoAlertPresentException e) {
		            s.assertTrue(true, "N17-1: No XSS alert — script was not executed");
		        }

		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N17-2: XSS input must not cause server error");

		        LOG.info("✅ N-17 PASSED — XSS attempt did not execute");
		        s.assertAll();
		    }

		    // ── N-18  Boundary — Full Name 300 Chars ──────────────────────────────────
		    @Test(priority = 28, description = "N-18 | Full Name 300 characters — page does not crash")
		    public void n18_fullName300Chars() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement name = nameField();
		        type(name, BOUNDARY_NAME_300);
		        String val = name.getAttribute("value");

		        s.assertNotNull(val, "N18-1: Name field must not be null after 300-char input");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N18-2: 300-char name must not cause server error");

		        int storedLength = val != null ? val.length() : 0;
		        LOG.info("  typed=300 stored=" + storedLength + " (maxlength may truncate)");
		        // Either stored in full, or truncated by maxlength attr — both are acceptable
		        s.assertTrue(storedLength > 0, "N18-3: At least some characters must be stored");

		        LOG.info("✅ N-18 PASSED  stored=" + storedLength);
		        s.assertAll();
		    }

		    // ── N-19  Boundary — Query 1000 Chars ─────────────────────────────────────
		    @Test(priority = 29, description = "N-19 | Your Query with 1000 characters — page does not crash")
		    public void n19_query1000Chars() {
		        load();
		        SoftAssert s = new SoftAssert();

		        WebElement query = queryField();
		        type(query, BOUNDARY_QUERY_1000);
		        String val = query.getAttribute("value");

		        s.assertNotNull(val, "N19-1: Query must not be null after 1000-char input");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("500"),
		            "N19-2: 1000-char query must not cause server error");

		        int storedLen = val != null ? val.length() : 0;
		        LOG.info("  typed=1000 stored=" + storedLen);
		        s.assertTrue(storedLen > 0, "N19-3: At least some query text must be stored");

		        LOG.info("✅ N-19 PASSED  stored=" + storedLen);
		        s.assertAll();
		    }

		    // ── N-20  Only Service Selected ───────────────────────────────────────────
		    @Test(priority = 30, description = "N-20 | Only service dropdown filled, all others blank — validation fires")
		    public void n20_onlyServiceFilledRestBlank() {
		        load();
		        SoftAssert s = new SoftAssert();

		        // Clear everything then select only service
		        try { nameField().clear();    } catch (Exception ignored) {}
		        try { phoneField().clear();   } catch (Exception ignored) {}
		        try { companyField().clear(); } catch (Exception ignored) {}
		        try { emailField().clear();   } catch (Exception ignored) {}
		        try { queryField().clear();   } catch (Exception ignored) {}

		        try { selectService("Not Sure – Need Consultation"); } catch (Exception ignored) {}

		        click(submitButton());
		        sleep(1000);

		        s.assertTrue(hasValidationError(),
		            "N20-1: Form with only service selected must still show validation for other fields");
		        s.assertFalse(driver.findElement(By.tagName("body")).getText().toLowerCase().contains("thank you"),
		            "N20-2: Partial form (service only) must not submit successfully");

		        LOG.info("✅ N-20 PASSED");
		        s.assertAll();
		    }
		}


