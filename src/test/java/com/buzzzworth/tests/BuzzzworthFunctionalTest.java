package com.buzzzworth.tests;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.buzzzworth.base.BaseDriver;

/**
	 * ═══════════════════════════════════════════════════════════════════════
	 *  Functional Test Suite — marketing.buzzzworth.com
	 *
	 *  FUNC-01  Consultation form structural
	 *  FUNC-02  Service dropdown — option count & content
	 *  FUNC-03  Form positive — valid data fills all fields
	 *  FUNC-04  Form negative — empty submit shows validation
	 *  FUNC-05  Form negative — invalid email rejected
	 *  FUNC-06  Form negative — non-numeric/short phone
	 *  FUNC-07  Form negative — only service selected, rest empty
	 *  FUNC-08  Form boundary — 300-char company name accepted
	 *  FUNC-09  Form boundary — special chars in name field
	 *  FUNC-10  Form UX — fields clear after clear()
	 *  FUNC-11  Form UX — Tab key navigates between fields
	 *  FUNC-12  CTA — "Connect with us" scrolls/anchors to form
	 *  FUNC-13  CTA — "Talk to a growth expert" anchors to form
	 *  FUNC-14  CTA — "Get a Free Audit" anchors to form
	 *  FUNC-15  CTA — "Enquire Package Details" anchors to form
	 *  FUNC-16  CTA — all "Talk to an Expert" anchors to form
	 *  FUNC-17  Anchor — #hero-section element exists & navigable
	 *  FUNC-18  Email link — valid mailto: href in footer
	 *  FUNC-19  Scroll — page scrolls full height without error
	 *  FUNC-20  Scroll — scroll-to-top from footer works
	 * ═══════════════════════════════════════════════════════════════════════
	 */
	public class BuzzzworthFunctionalTest extends BaseDriver {

	    /* ─── test data ─────────────────────────────────────────────── */
	    private static final String VALID_NAME    = "Test User";
	    private static final String VALID_EMAIL   = "testuser@example.com";
	    private static final String INVALID_EMAIL = "notvalidemail";
	    private static final String VALID_PHONE   = "9876543210";
	    private static final String SHORT_PHONE   = "12";
	    private static final String LONG_NAME     = "A".repeat(300);
	    private static final String SPECIAL_CHARS = "!@#$%^&*()<>?/|{}~`";
	    private static final String VALID_COMPANY = "Acme Pvt Ltd";

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-01  Form structural
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 1, groups = "functional",
	          description = "FUNC-01 | Form structural — service select, name, email, phone inputs + submit button present")
	    public void func01_formStructural() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Service select
	        WebElement svc = find("select");
	        s.assertNotNull(svc, "F1: Service <select> must be present");
	        if (svc != null) {
	            s.assertTrue(svc.isDisplayed(), "F1b: Service dropdown must be visible");
	            s.assertTrue(svc.isEnabled(),   "F1c: Service dropdown must be enabled");
	        }

	        // Name
	        WebElement name = find("input[placeholder*='Name']", "input[name*='name']", "input[type='text']");
	        s.assertNotNull(name, "F2: Name input must be present");
	        if (name != null) { s.assertTrue(name.isDisplayed(), "F2b: Name input visible"); s.assertTrue(name.isEnabled(), "F2c: Name input enabled"); }

	        // Email
	        WebElement email = find("input[type='email']", "input[placeholder*='email']", "input[placeholder*='Email']");
	        s.assertNotNull(email, "F3: Email input must be present");
	        if (email != null) { s.assertTrue(email.isDisplayed(), "F3b"); s.assertTrue(email.isEnabled(), "F3c"); }

	        // Phone
	        WebElement phone = find("input[type='tel']", "input[placeholder*='Phone']", "input[placeholder*='phone']", "input[placeholder*='Mobile']");
	        s.assertNotNull(phone, "F4: Phone input must be present");
	        if (phone != null) { s.assertTrue(phone.isDisplayed(), "F4b"); s.assertTrue(phone.isEnabled(), "F4c"); }

	        // Submit button
	        WebElement submit = find(xpathText("submit now"), "button[type='submit']", "input[type='submit']");
	        s.assertNotNull(submit, "F5: Submit button must be present");
	        if (submit != null) {
	            s.assertTrue(submit.isDisplayed(), "F5b: Submit button visible");
	            s.assertTrue(submit.isEnabled(),   "F5c: Submit button enabled");
	        }

	        LOG.info("✅ FUNC-01");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-02  Service Dropdown Options
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 2, groups = "functional",
	          description = "FUNC-02 | Service dropdown — 8 options (1 placeholder + 7), all correct labels")
	    public void func02_serviceDropdownOptions() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement svc = find("select");
	        s.assertNotNull(svc, "D1: Service dropdown must exist");

	        if (svc != null) {
	            Select sel = new Select(svc);
	            List<WebElement> opts = sel.getOptions();

	            s.assertEquals(opts.size(), 8,
	                "D2: Dropdown must have 8 options (1 placeholder + 7 services). Found: " + opts.size());

	            // Check all 7 services from live scrape
	            String[] expected = {
	                "social media management",
	                "performance marketing",
	                "social media + performance marketing",
	                "campaign strategy & execution",
	                "online reputation management",
	                "complete digital marketing setup",
	                "not sure"
	            };
	            String allText = opts.stream().map(o -> o.getText().toLowerCase()).reduce("", (a, b) -> a + "|" + b);
	            for (String exp : expected) {
	                boolean found = allText.contains(exp.toLowerCase());
	                s.assertTrue(found, "D3: Option '" + exp + "' must exist in dropdown. allOpts=" + allText);
	            }

	            LOG.info("  options=" + opts.size() + " opts=" + allText);
	        }

	        LOG.info("✅ FUNC-02");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-03  Positive — Fill All Fields
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 3, groups = "functional",
	          description = "FUNC-03 | Form positive — all fields accept valid data, values persist")
	    public void func03_formPositive() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Select service
	        WebElement svcEl = find("select");
	        if (svcEl != null) {
	            Select sel = new Select(svcEl);
	            if (sel.getOptions().size() > 1) {
	                sel.selectByIndex(1); // "Social Media Management"
	                s.assertFalse(sel.getFirstSelectedOption().getText().isBlank(),
	                    "P1: Selected service must not be blank");
	                LOG.info("  selected service='" + sel.getFirstSelectedOption().getText() + "'");
	            }
	        }

	        // Name
	        WebElement nameEl = find("input[placeholder*='Name']", "input[name*='name']", "input[type='text']");
	        if (nameEl != null) {
	            clearType(nameEl, VALID_NAME);
	            s.assertEquals(nameEl.getAttribute("value"), VALID_NAME, "P2: Name field must accept valid name");
	        }

	        // Email
	        WebElement emailEl = find("input[type='email']", "input[placeholder*='Email']");
	        if (emailEl != null) {
	            clearType(emailEl, VALID_EMAIL);
	            s.assertEquals(emailEl.getAttribute("value"), VALID_EMAIL, "P3: Email field must accept valid email");
	        }

	        // Phone
	        WebElement phoneEl = find("input[type='tel']", "input[placeholder*='Phone']", "input[placeholder*='phone']");
	        if (phoneEl != null) {
	            clearType(phoneEl, VALID_PHONE);
	            s.assertFalse(phoneEl.getAttribute("value").isEmpty(), "P4: Phone field must accept valid phone");
	        }

	        // Company (if 4th text input exists)
	        List<WebElement> textInputs = findAll("input[type='text']");
	        if (textInputs.size() >= 2) {
	            WebElement companyEl = textInputs.get(textInputs.size() - 1);
	            if (!companyEl.getAttribute("value").equals(VALID_NAME)) {
	                clearType(companyEl, VALID_COMPANY);
	                s.assertFalse(companyEl.getAttribute("value").isEmpty(), "P5: Company field must accept input");
	            }
	        }

	        LOG.info("✅ FUNC-03");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-04  Negative — Empty Submit
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 4, groups = "functional",
	          description = "FUNC-04 | Form negative — empty submit shows 'please fill all required fields' message")
	    public void func04_emptySubmit() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Clear all inputs
	        driver.findElements(By.cssSelector("input[type='text'],input[type='email'],input[type='tel'],textarea"))
	              .stream().filter(WebElement::isDisplayed).forEach(WebElement::clear);

	        // Reset select to placeholder
	        WebElement svcEl = find("select");
	        if (svcEl != null) {
				new Select(svcEl).selectByIndex(0);
			}

	        // Click submit
	        WebElement submit = find(xpathText("submit now"), "button[type='submit']");
	        s.assertNotNull(submit, "C1: Submit button must be present");
	        if (submit != null) {
	            safeClick(submit);
	            sleep(1200);
	        }

	        // Check validation — site shows "Please fill all required fields correctly."
	        boolean validationShown = validationFired()
	            || bodyText().contains("please fill all required fields")
	            || bodyText().contains("fill all required");
	        s.assertTrue(validationShown, "C2: Empty submit must show required-field validation message");

	        // Page must NOT navigate away or crash
	        s.assertFalse(bodyText().contains("500"),          "C3: No 500 error on empty submit");
	        s.assertEquals(driver.getCurrentUrl(), BASE_URL,   "C4: URL must not change on empty submit");

	        LOG.info("✅ FUNC-04  validation=" + validationShown);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-05  Negative — Invalid Email
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 5, groups = "functional",
	          description = "FUNC-05 | Form — invalid email format rejected via HTML5 or custom error")
	    public void func05_invalidEmail() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement emailEl = find("input[type='email']", "input[placeholder*='Email']");
	        s.assertNotNull(emailEl, "E1: Email field must be present");

	        if (emailEl != null) {
	            clearType(emailEl, INVALID_EMAIL);
	            s.assertEquals(emailEl.getAttribute("value"), INVALID_EMAIL,
	                "E2: Email field must accept typed text (validation fires on submit)");

	            WebElement submit = find(xpathText("submit now"), "button[type='submit']");
	            if (submit != null) { safeClick(submit); sleep(800); }

	            Boolean html5Invalid = (Boolean) js.executeScript(
	                "return arguments[0].validity && !arguments[0].validity.valid;", emailEl);
	            boolean customError = bodyText().contains("valid email")
	                || bodyText().contains("invalid email")
	                || bodyText().contains("fill all required");
	            s.assertTrue(Boolean.TRUE.equals(html5Invalid) || customError,
	                "E3: Invalid email must trigger HTML5 validity or custom error. html5=" + html5Invalid);
	        }

	        LOG.info("✅ FUNC-05");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-06  Negative — Short / Non-numeric Phone
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 6, groups = "functional",
	          description = "FUNC-06 | Form — short phone (2 digits) doesn't crash page; validation may fire")
	    public void func06_shortPhone() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement phoneEl = find("input[type='tel']", "input[placeholder*='Phone']", "input[placeholder*='phone']");
	        if (phoneEl != null) {
	            clearType(phoneEl, SHORT_PHONE);
	            String val = phoneEl.getAttribute("value");
	            s.assertNotNull(val, "Ph1: Phone field value must not be null");

	            WebElement submit = find(xpathText("submit now"), "button[type='submit']");
	            if (submit != null) { safeClick(submit); sleep(800); }

	            s.assertFalse(bodyText().contains("500"), "Ph2: Short phone must not cause server error");
	            Boolean html5Invalid = (Boolean) js.executeScript(
	                "return arguments[0].validity && !arguments[0].validity.valid;", phoneEl);
	            LOG.info("  short phone html5Invalid=" + html5Invalid + " value='" + val + "'");
	        }

	        LOG.info("✅ FUNC-06");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-07  Negative — Only Service Selected
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 7, groups = "functional",
	          description = "FUNC-07 | Form — selecting service but leaving other fields empty still shows validation")
	    public void func07_onlyServiceSelected() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Select a service
	        WebElement svcEl = find("select");
	        if (svcEl != null) {
	            Select sel = new Select(svcEl);
	            if (sel.getOptions().size() > 1) {
					sel.selectByIndex(2);
				}
	        }

	        // Leave other fields empty
	        driver.findElements(By.cssSelector("input[type='text'],input[type='email'],input[type='tel']"))
	              .stream().filter(WebElement::isDisplayed).forEach(WebElement::clear);

	        WebElement submit = find(xpathText("submit now"), "button[type='submit']");
	        if (submit != null) { safeClick(submit); sleep(1000); }

	        boolean validationShown = validationFired()
	            || bodyText().contains("fill all required")
	            || bodyText().contains("please fill");
	        s.assertTrue(validationShown, "OS1: Partial form (service only) submit must show validation");

	        LOG.info("✅ FUNC-07  validation=" + validationShown);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-08  Boundary — Long Input
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 8, groups = "functional",
	          description = "FUNC-08 | Form boundary — 300-character input doesn't crash page")
	    public void func08_longInput() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement nameEl = find("input[placeholder*='Name']", "input[type='text']");
	        if (nameEl != null) {
	            clearType(nameEl, LONG_NAME);
	            String val = nameEl.getAttribute("value");
	            s.assertNotNull(val, "LI1: Name field must not be null after long input");
	            s.assertFalse(bodyText().contains("500"), "LI2: Long input must not cause server error");
	            LOG.info("  typed=" + LONG_NAME.length() + " stored=" + (val != null ? val.length() : 0));
	        }

	        LOG.info("✅ FUNC-08");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-09  Boundary — Special Characters
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 9, groups = "functional",
	          description = "FUNC-09 | Form boundary — special characters in name field accepted without crash")
	    public void func09_specialChars() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement nameEl = find("input[placeholder*='Name']", "input[type='text']");
	        if (nameEl != null) {
	            clearType(nameEl, SPECIAL_CHARS);
	            String val = nameEl.getAttribute("value");
	            s.assertNotNull(val, "SC1: Name field must not be null after special chars");
	            s.assertFalse(bodyText().contains("500"), "SC2: Special chars must not cause server error");
	            LOG.info("  special chars stored='" + val + "'");
	        }

	        LOG.info("✅ FUNC-09");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-10  UX — Field Clear
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 10, groups = "functional",
	          description = "FUNC-10 | Form UX — fields return to empty after clear()")
	    public void func10_fieldClear() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        WebElement nameEl  = find("input[placeholder*='Name']", "input[type='text']");
	        WebElement emailEl = find("input[type='email']");
	        WebElement phoneEl = find("input[type='tel']", "input[placeholder*='Phone']");

	        for (WebElement[] pair : new WebElement[][]{{nameEl, null}, {emailEl, null}, {phoneEl, null}}) {
	            WebElement el = pair[0];
	            if (el != null) {
	                clearType(el, "somevalue");
	                el.clear();
	                String val = el.getAttribute("value");
	                s.assertTrue(val == null || val.isEmpty(),
	                    "CL1: Field must be empty after clear(). tag=" + el.getTagName()
	                    + " placeholder=" + el.getAttribute("placeholder"));
	            }
	        }

	        LOG.info("✅ FUNC-10");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-11  UX — Tab Navigation
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 11, groups = "functional",
	          description = "FUNC-11 | Form UX — Tab key moves focus between fields without errors")
	    public void func11_tabNavigation() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> inputs = findAll("input[type='text'],input[type='email'],input[type='tel']");
	        s.assertTrue(inputs.size() >= 2, "TN1: At least 2 input fields must be visible for tab test");

	        if (inputs.size() >= 2) {
	            inputs.get(0).click();
	            inputs.get(0).sendKeys(Keys.TAB);
	            sleep(300);
	            // No exception = Tab works
	            s.assertFalse(bodyText().contains("500"), "TN2: Tab navigation must not cause errors");
	            LOG.info("  tab navigation ok across " + inputs.size() + " fields");
	        }

	        LOG.info("✅ FUNC-11");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-12  CTA — "Connect with us"
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 12, groups = "functional",
	          description = "FUNC-12 | 'Connect with us' CTA — href targets #hero-section, click scrolls to form")
	    public void func12_connectWithUsCta() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> ctas = driver.findElements(By.xpath(xpathText("connect with us")));
	        s.assertFalse(ctas.isEmpty(), "CW1: 'Connect with us' must be present");

	        if (!ctas.isEmpty()) {
	            WebElement cta = ctas.get(0);
	            String href = cta.getAttribute("href");
	            s.assertTrue(href != null && href.contains("#hero-section"),
	                "CW2: href must contain #hero-section. Got: " + href);

	            long before = scrollY();
	            safeClick(cta);
	            sleep(900);
	            long after = scrollY();

	            // Should scroll down or already at form
	            List<WebElement> form = findAll("select");
	            s.assertFalse(form.isEmpty(), "CW3: Form (select) must be reachable after CTA click");
	            LOG.info("  scrollY before=" + before + " after=" + after);
	        }

	        LOG.info("✅ FUNC-12");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-13  CTA — "Talk to a growth expert"
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 13, groups = "functional",
	          description = "FUNC-13 | 'Talk to a growth expert' — href=#hero-section, scrolls to form")
	    public void func13_talkToGrowthExpert() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> ctas = driver.findElements(By.xpath(xpathText("talk to a growth expert")));
	        s.assertFalse(ctas.isEmpty(), "GE1: 'Talk to a growth expert' CTA must exist");

	        if (!ctas.isEmpty()) {
	            String href = ctas.get(0).getAttribute("href");
	            s.assertTrue(href != null && href.contains("#hero-section"),
	                "GE2: href must target #hero-section. Got: " + href);

	            safeClick(ctas.get(0));
	            sleep(900);
	            s.assertFalse(findAll("select").isEmpty(), "GE3: Form must be reachable after CTA click");
	        }

	        LOG.info("✅ FUNC-13");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-14  CTA — "Get a Free Audit"
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 14, groups = "functional",
	          description = "FUNC-14 | 'Get a Free Audit' CTA — href=#hero-section")
	    public void func14_getFreeAudit() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> ctas = driver.findElements(By.xpath(xpathText("get a free audit")));
	        s.assertFalse(ctas.isEmpty(), "GA1: 'Get a Free Audit' CTA must be present");

	        if (!ctas.isEmpty()) {
	            String href = ctas.get(0).getAttribute("href");
	            s.assertTrue(href != null && href.contains("#hero-section"),
	                "GA2: href must contain #hero-section. Got: " + href);
	        }

	        LOG.info("✅ FUNC-14");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-15  CTA — "Enquire Package Details"
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 15, groups = "functional",
	          description = "FUNC-15 | 'Enquire Package Details' CTA — href is anchor link")
	    public void func15_enquirePackage() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> ctas = driver.findElements(By.xpath(xpathText("enquire package details")));
	        s.assertFalse(ctas.isEmpty(), "EP1: 'Enquire Package Details' CTA must be present");

	        if (!ctas.isEmpty()) {
	            String href = ctas.get(0).getAttribute("href");
	            s.assertTrue(href != null && href.contains("#"),
	                "EP2: Enquire CTA must be an anchor link. Got: " + href);
	            s.assertTrue(href.contains("#hero-section"),
	                "EP3: href must target #hero-section specifically. Got: " + href);
	        }

	        LOG.info("✅ FUNC-15");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-16  CTAs — All "Talk to an Expert"
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 16, groups = "functional",
	          description = "FUNC-16 | All 'Talk to an Expert' package CTAs — each href=#hero-section")
	    public void func16_talkToExpertAll() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> ctas = driver.findElements(By.xpath(xpathText("talk to an expert")));
	        s.assertTrue(ctas.size() >= 3,
	            "TE1: At least 3 'Talk to an Expert' CTAs must exist. Found: " + ctas.size());

	        for (int i = 0; i < ctas.size(); i++) {
	            String href = ctas.get(i).getAttribute("href");
	            s.assertTrue(href != null && href.contains("#hero-section"),
	                "TE2[" + i + "]: href must target #hero-section. Got: " + href);
	        }

	        LOG.info("✅ FUNC-16  ctaCount=" + ctas.size());
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-17  Anchor — #hero-section
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 17, groups = "functional",
	          description = "FUNC-17 | #hero-section — element exists in DOM, direct URL navigation works")
	    public void func17_heroSectionAnchor() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Exists in DOM
	        List<WebElement> heroEls = driver.findElements(By.cssSelector("#hero-section"));
	        s.assertFalse(heroEls.isEmpty(), "HS1: #hero-section element must exist in DOM");

	        // Direct URL
	        driver.get(BASE_URL + "#hero-section");
	        waitForPageReady();
	        sleep(500);

	        s.assertFalse(driver.getTitle().isBlank(), "HS2: Page must load with #hero-section URL");
	        s.assertFalse(bodyText().contains("404"),  "HS3: #hero-section URL must not 404");

	        // Form present at anchor
	        s.assertFalse(findAll("select").isEmpty(), "HS4: Consultation form must be accessible at #hero-section");

	        LOG.info("✅ FUNC-17  heroEls=" + heroEls.size());
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-18  Email Link
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 18, groups = "functional",
	          description = "FUNC-18 | Footer email link — valid mailto: href, address non-blank")
	    public void func18_emailLink() {
	        loadHome();
	        scrollToBottom();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> emails = driver.findElements(By.cssSelector("a[href^='mailto:']"));
	        s.assertFalse(emails.isEmpty(), "ML1: At least one mailto: link must exist in footer");

	        for (WebElement link : emails) {
	            String href = link.getAttribute("href");
	            s.assertNotNull(href, "ML2: Email link href must not be null");
	            s.assertTrue(href.startsWith("mailto:"), "ML3: href must start with 'mailto:'");
	            // Address may be Cloudflare-obfuscated — check either decoded or data-cfemail attr
	            boolean hasAddress = href.replace("mailto:", "").trim().contains("@")
	                || link.getAttribute("data-cfemail") != null;
	            s.assertTrue(hasAddress,
	                "ML4: Email link must have address or Cloudflare obfuscation. href=" + href);
	            LOG.info("  email href=" + href);
	        }

	        LOG.info("✅ FUNC-18");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-19  Scroll — Full Page
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 19, groups = "functional",
	          description = "FUNC-19 | Page scrolls to bottom and back; no errors during scroll")
	    public void func19_scrollFullPage() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Scroll to bottom
	        scrollToBottom();
	        long atBottom = scrollY();
	        s.assertTrue(atBottom > 0, "PS1: scrollY must be > 0 at page bottom. Got: " + atBottom);
	        s.assertFalse(bodyText().contains("500"), "PS2: No 500 error at bottom of page");

	        // Footer copyright visible
	        s.assertTrue(bodyText().contains("all rights reserved"), "PS3: Footer must be visible at bottom");

	        LOG.info("✅ FUNC-19  atBottom=" + atBottom);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // FUNC-20  Scroll — Back to Top
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 20, groups = "functional",
	          description = "FUNC-20 | Scroll-to-top — page scrollY decreases back toward 0")
	    public void func20_scrollToTop() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Go to bottom first
	        scrollToBottom();
	        long atBottom = scrollY();

	        // Scroll back to top
	        scrollToTop();
	        long atTop = scrollY();

	        s.assertTrue(atTop < atBottom,
	            "ST1: scrollY after scroll-to-top must be less than at bottom. bottom=" + atBottom + " top=" + atTop);
	        s.assertTrue(atTop <= 100, "ST2: scrollY at top must be ≤ 100px. Got: " + atTop);

	        // Logo/nav still in view
	        s.assertNotNull(find("img[alt='logo']", "img[src*='buzzworth-logo']"),
	            "ST3: Logo must be visible after scroll to top");

	        LOG.info("✅ FUNC-20  bottom=" + atBottom + " top=" + atTop);
	        s.assertAll();
	    }
	}


