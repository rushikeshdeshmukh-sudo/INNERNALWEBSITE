package com.buzzzworth.tests;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.buzzzworth.base.BaseDriver;

/**
	 * ═══════════════════════════════════════════════════════════════
	 *  UI Test Suite — marketing.buzzzworth.com
	 *  Covers every visible section scraped from the live page:
	 *
	 *  UI-01  Page Load & Title
	 *  UI-02  Navigation Bar
	 *  UI-03  Hero Section
	 *  UI-04  Consultation Form (UI layer)
	 *  UI-05  Google Rating Badge
	 *  UI-06  Brand Logo Strip
	 *  UI-07  "What We Do" Service Cards
	 *  UI-08  SMM Starter Pack Section
	 *  UI-09  Pricing / Package Cards
	 *  UI-10  Awards & Recognition Section
	 *  UI-11  Stats / Metrics Section
	 *  UI-12  Footer
	 *  UI-13  All Images — src + load check
	 *  UI-14  Responsive — 375 px mobile viewport
	 *  UI-15  Responsive — 768 px tablet viewport
	 *  UI-16  Page title & meta
	 * ═══════════════════════════════════════════════════════════════
	 */
	public class BuzzzworthUITest extends BaseDriver {

	    // ─────────────────────────────────────────────────────────────
	    // UI-01  Page Load
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 1, groups = "ui",
	          description = "UI-01 | Page loads — correct title, HTTP 200, readyState complete, no error body")
	    public void ui01_pageLoad() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        String title = driver.getTitle();
	        s.assertNotNull(title,                                    "Title must not be null");
	        s.assertFalse(title.isBlank(),                            "Title must not be blank");
	        s.assertTrue(title.toLowerCase().contains("buzzzworth")
	                  || title.toLowerCase().contains("social")
	                  || title.toLowerCase().contains("build your"),
	                  "Title should contain brand keywords. Got: " + title);

	        String body = bodyText();
	        s.assertFalse(body.isBlank(),                             "Body text must not be empty");
	        s.assertFalse(body.contains("404"),                       "Body must not contain '404'");
	        s.assertFalse(body.contains("500"),                       "Body must not contain '500'");
	        s.assertFalse(body.contains("error establishing a database connection"),
	                                                                  "No DB connection error");

	        String readyState = (String) js.executeScript("return document.readyState;");
	        s.assertEquals(readyState, "complete",                    "readyState must be 'complete'");

	        LOG.info("✅ UI-01  title='" + title + "'");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-02  Navigation
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 2, groups = "ui",
	          description = "UI-02 | Nav — logo visible & loaded, 'Connect with us' CTA present")
	    public void ui02_navigation() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Logo
	        WebElement logo = find("img[alt='logo']", "img[src*='buzzworth-logo']", "nav img", "header img");
	        s.assertNotNull(logo,                   "Logo image must be in nav/header");
	        if (logo != null) {
	            s.assertTrue(logo.isDisplayed(),    "Logo must be visible");
	            s.assertTrue(imgLoaded(logo),       "Logo must load (naturalWidth > 0)");
	            String src = logo.getAttribute("src");
	            s.assertFalse(src == null || src.isBlank(), "Logo src must not be blank");
	            LOG.info("  logo src=" + src);
	        }

	        // Nav CTA
	        List<WebElement> navCta = driver.findElements(By.xpath(xpathText("connect with us")));
	        s.assertFalse(navCta.isEmpty(),         "'Connect with us' CTA must exist in nav");
	        if (!navCta.isEmpty()) {
	            s.assertTrue(navCta.get(0).isDisplayed(), "'Connect with us' must be visible");
	            String href = navCta.get(0).getAttribute("href");
	            s.assertTrue(href != null && href.contains("#hero-section"),
	                         "'Connect with us' href must point to #hero-section. Got: " + href);
	        }

	        LOG.info("✅ UI-02");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-03  Hero Section
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 3, groups = "ui",
	          description = "UI-03 | Hero — headline, sub-copy, hero CTA visible; #hero-section anchor exists")
	    public void ui03_heroSection() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        String body = bodyText();

	        // Headline
	        s.assertTrue(body.contains("social presence") || body.contains("drive performance")
	                  || body.contains("scale faster"),
	                  "Hero headline must contain brand tagline keywords");

	        // Sub-copy
	        s.assertTrue(body.contains("creative-first") || body.contains("performance-led")
	                  || body.contains("sme") || body.contains("visibility"),
	                  "Hero sub-copy must be present");

	        // "Talk to a growth expert" CTA
	        List<WebElement> heroCta = driver.findElements(By.xpath(xpathText("talk to a growth expert")));
	        s.assertFalse(heroCta.isEmpty(), "'Talk to a growth expert' CTA must be present");
	        if (!heroCta.isEmpty()) {
	            s.assertTrue(heroCta.get(0).isDisplayed(), "Hero CTA must be visible");
	            s.assertTrue(heroCta.get(0).getAttribute("href").contains("#hero-section"),
	                         "Hero CTA href must target #hero-section");
	        }

	        // Section anchor
	        List<WebElement> anchor = driver.findElements(By.cssSelector("#hero-section"));
	        s.assertFalse(anchor.isEmpty(), "#hero-section anchor must exist in DOM");

	        // Marketing label
	        s.assertTrue(body.contains("marketing that helps") || body.contains("build your"),
	                     "Pre-heading 'marketing that helps' must be on page");

	        LOG.info("✅ UI-03");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-04  Consultation Form — UI Layer
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 4, groups = "ui",
	          description = "UI-04 | Form UI — heading, select, 4 input fields, avatars, submit button visible")
	    public void ui04_formUI() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Form heading
	        s.assertTrue(bodyText().contains("get free consultation") || bodyText().contains("free consultation"),
	                     "Form heading 'Get Free Consultation' must be visible");

	        // Service select
	        WebElement svc = find("select");
	        s.assertNotNull(svc, "Service <select> dropdown must be present");
	        if (svc != null) {
	            s.assertTrue(svc.isDisplayed(), "Service dropdown must be visible");
	            s.assertEquals(new Select(svc).getOptions().size(), 8,
	                           "Service dropdown must have 8 options (1 placeholder + 7 services)");
	        }

	        // Input fields (name, email, phone + any extra)
	        List<WebElement> inputs = findAll("input[type='text'],input[type='email'],input[type='tel'],input[type='number']");
	        s.assertTrue(inputs.size() >= 3, "At least 3 input fields must be visible in form. Found: " + inputs.size());

	        // Avatar / decorative images inside form
	        List<WebElement> fomImgs = driver.findElements(By.cssSelector("img[src*='fom-img']"));
	        s.assertFalse(fomImgs.isEmpty(), "Form decorative images (fom-img*) must be present");

	        // Submit button
	        WebElement submit = find(xpathText("submit now"), "button[type='submit']", "input[type='submit']");
	        s.assertNotNull(submit, "'Submit Now' button must be present");
	        if (submit != null) {
	            s.assertTrue(submit.isDisplayed(), "Submit button must be visible");
	            s.assertTrue(submit.isEnabled(),   "Submit button must be enabled");
	            s.assertTrue(submit.getText().toLowerCase().contains("submit"),
	                         "Submit button text must contain 'submit'. Got: '" + submit.getText() + "'");
	        }

	        // Validation message element (hidden initially)
	        s.assertTrue(bodyText().contains("please fill all required fields"),
	                     "Hidden validation message element must be present in DOM");

	        // Thank You element present in DOM
	        s.assertTrue(bodyText().contains("thank you") || bodyText().contains("received your request"),
	                     "Thank You confirmation element must be present in DOM (hidden initially)");

	        LOG.info("✅ UI-04");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-05  Google Rating Badge
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 5, groups = "ui",
	          description = "UI-05 | Google rating badge — image loads, 5.0 rating text visible")
	    public void ui05_googleRatingBadge() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Google image
	        WebElement googleImg = find("img[src*='google']");
	        s.assertNotNull(googleImg, "Google rating image must be present");
	        if (googleImg != null) {
	            s.assertTrue(googleImg.isDisplayed(), "Google image must be visible");
	            s.assertTrue(imgLoaded(googleImg),    "Google image must load");
	        }

	        // Rating text
	        s.assertTrue(bodyText().contains("5.0"), "Google rating '(5.0)' must appear on page");

	        LOG.info("✅ UI-05");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-06  Brand Logo Strip
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 6, groups = "ui",
	          description = "UI-06 | Brand strip — label visible, ≥10 logos in DOM, ≥8 load successfully")
	    public void ui06_brandLogoStrip() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Section label
	        s.assertTrue(bodyText().contains("serving brands") || bodyText().contains("b2b, b2c"),
	                     "'Serving brands across B2B, B2C and D2C' label must be visible");

	        // Logo images
	        List<WebElement> logos = driver.findElements(By.cssSelector("img[src*='bannershaps']"));
	        s.assertTrue(logos.size() >= 10,
	                     "Brand logo strip must have ≥10 images. Found: " + logos.size());

	        int loaded = (int) logos.stream().filter(this::imgLoaded).count();
	        s.assertTrue(loaded >= 8,
	                     "At least 8 brand logos must load (naturalWidth>0). Loaded: " + loaded + "/" + logos.size());

	        LOG.info("✅ UI-06  logos=" + logos.size() + " loaded=" + loaded);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-07  What We Do
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 7, groups = "ui",
	          description = "UI-07 | 'What We Do' — section heading, all 4 cards with icons & bullets, 'Get a Free Audit' CTA")
	    public void ui07_whatWeDo() {
	        loadHome();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        s.assertTrue(body.contains("what we do"),                         "Section heading 'What We Do' must be visible");
	        s.assertTrue(body.contains("brand & strategy")
	                  || body.contains("brand and strategy"),                 "'Brand & Strategy' card must be present");
	        s.assertTrue(body.contains("performance & growth")
	                  || body.contains("performance and growth"),             "'Performance & Growth' card must be present");
	        s.assertTrue(body.contains("social media marketing"),             "'Social Media Marketing' card must be present");
	        s.assertTrue(body.contains("online reputation management"),       "'Online Reputation Management' card must be present");

	        // Card icons
	        List<WebElement> icons = driver.findElements(By.cssSelector("img[src*='wedoicon']"));
	        s.assertTrue(icons.size() >= 4, "4 service icons (wedoicon*) must be present. Found: " + icons.size());
	        int iconsLoaded = (int) icons.stream().filter(this::imgLoaded).count();
	        s.assertTrue(iconsLoaded >= 3, "At least 3 service icons must load. Loaded: " + iconsLoaded);

	        // Bullet content
	        s.assertTrue(body.contains("brand positioning"),                  "'Brand Positioning' bullet must appear");
	        s.assertTrue(body.contains("content & narrative")
	                  || body.contains("content and narrative"),              "'Content & Narrative' bullet must appear");
	        s.assertTrue(body.contains("paid media"),                         "'Paid Media Campaigns' bullet must appear");
	        s.assertTrue(body.contains("funnel"),                             "'Funnel & Conversion' bullet must appear");
	        s.assertTrue(body.contains("influencer"),                         "'Influencer & Creator' bullet must appear");
	        s.assertTrue(body.contains("sentiment analysis"),                 "'Sentiment Analysis' bullet must appear");
	        s.assertTrue(body.contains("crisis"),                             "'Crisis & Response' bullet must appear");

	        // CTA
	        List<WebElement> auditCta = driver.findElements(By.xpath(xpathText("get a free audit")));
	        s.assertFalse(auditCta.isEmpty(), "'Get a Free Audit' CTA must exist");
	        if (!auditCta.isEmpty()) {
				s.assertTrue(auditCta.get(0).getAttribute("href").contains("#hero-section"),
	                         "'Get a Free Audit' must link to #hero-section");
			}

	        LOG.info("✅ UI-07");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-08  SMM Starter Pack Feature Section
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 8, groups = "ui",
	          description = "UI-08 | Starter pack feature section — heading, ₹75,000 price, 4 step images, 'Enquire' CTA")
	    public void ui08_smmStarterPack() {
	        loadHome();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        s.assertTrue(body.contains("smm starter pack") || body.contains("starter pack"),
	                     "'BuzzzWorth SMM Starter Pack' heading must be visible");
	        s.assertTrue(body.contains("75,000"),             "₹75,000 price must be visible in starter pack");
	        s.assertTrue(body.contains("why this package"),   "'Why This Package?' sub-heading must be visible");

	        // Step images (stpimg)
	        List<WebElement> stepImgs = driver.findElements(By.cssSelector("img[src*='stpimg']"));
	        s.assertTrue(stepImgs.size() >= 3, "At least 3 step images (stpimg*) must be present. Found: " + stepImgs.size());

	        // Step labels
	        s.assertTrue(body.contains("engagement growth"),    "'Engagement Growth' step label must be visible");
	        s.assertTrue(body.contains("lead generation"),      "'Lead Generation' step label must be visible");
	        s.assertTrue(body.contains("consideration funnel") || body.contains("consideration"),
	                                                            "'Consideration Funnels' step labels must be visible");

	        // Enquire CTA
	        List<WebElement> enquireCta = driver.findElements(By.xpath(xpathText("enquire package details")));
	        s.assertFalse(enquireCta.isEmpty(), "'Enquire Package Details' CTA must be present");

	        LOG.info("✅ UI-08");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-09  Pricing / Package Cards
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 9, groups = "ui",
	          description = "UI-09 | Pricing — all 4 package cards visible with name, price, bullets, CTA")
	    public void ui09_pricingPackages() {
	        loadHome();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        // Section heading
	        s.assertTrue(body.contains("drive measurable growth") || body.contains("roi-focused"),
	                     "Pricing section heading must be visible");

	        // Starter Pack tab/card
	        s.assertTrue(body.contains("starter pack"),           "'Starter Pack' package card must be present");
	        s.assertTrue(body.contains("12%") || body.contains("management fee"),
	                                                              "Starter Pack pricing (12% mgmt fee) must be visible");

	        // Performance Package
	        s.assertTrue(body.contains("performance package"),    "'Performance Package' card must be present");
	        s.assertTrue(body.contains("ecommerce") || body.contains("fmcg") || body.contains("saas"),
	                                                              "Performance Package target audience copy must be visible");

	        // Creative Pack
	        s.assertTrue(body.contains("creative pack") || body.contains("creative retainer"),
	                                                              "'Creative Pack / Retainer' card must be present");
	        s.assertTrue(body.contains("75,000"),                 "Creative Pack ₹75,000/month price must be visible");
	        s.assertTrue(body.contains("creative concept"),       "'Creative concept ideation' bullet must be visible");
	        s.assertTrue(body.contains("brand asset library"),    "'Brand asset library' bullet must be visible");

	        // LinkedIn Package
	        s.assertTrue(body.contains("linkedin package"),       "'LinkedIn Package' card must be present");
	        s.assertTrue(body.contains("50,000"),                 "LinkedIn Package ₹50,000/month price must be visible");
	        s.assertTrue(body.contains("linkedin profile optimis"), "'LinkedIn Profile Optimisation' bullet must be visible");
	        s.assertTrue(body.contains("performance dashboard"),  "'Performance dashboard' bullet must be visible");

	        // "Talk to an Expert" CTAs (one per package = 3 CTAs)
	        List<WebElement> expertCtas = driver.findElements(By.xpath(xpathText("talk to an expert")));
	        s.assertTrue(expertCtas.size() >= 3,
	                     "'Talk to an Expert' CTAs must appear ≥3 times. Found: " + expertCtas.size());

	        // Check CTA pricing notes
	        s.assertTrue(body.contains("pricing varies") || body.contains("prices scale"),
	                     "Pricing disclaimer note must be visible");

	        LOG.info("✅ UI-09  expertCtas=" + expertCtas.size());
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-10  Awards & Recognition
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 10, groups = "ui",
	          description = "UI-10 | Awards — section heading, 8 award images load, all 8 captions visible")
	    public void ui10_awardsSection() {
	        loadHome();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        s.assertTrue(body.contains("awards") || body.contains("recognition"),
	                     "'Awards & Recognition' section heading must be visible");

	        // Award images
	        List<WebElement> awardImgs = driver.findElements(By.cssSelector("img[src*='aword']"));
	        s.assertEquals(awardImgs.size(), 8, "Exactly 8 award images must be present");
	        int awardLoaded = (int) awardImgs.stream().filter(this::imgLoaded).count();
	        s.assertTrue(awardLoaded >= 6, "At least 6 of 8 award images must load. Loaded: " + awardLoaded);

	        // All 8 captions
	        String[][] captions = {
	            {"brandwagon ace award", "new product launch"},
	            {"digixx", "video", "consumer goods"},
	            {"mommy award"},
	            {"gold award", "digixx", "social media"},
	            {"gold award", "digixx", "social media"},
	            {"silver award", "csr"},
	            {"brandwagon ace award", "youth marketing"},
	            {"brandwagon ace award", "consistent marketing"}
	        };
	        for (int i = 0; i < captions.length; i++) {
	            final int idx = i;
	            boolean found = java.util.Arrays.stream(captions[i]).allMatch(body::contains);
	            s.assertTrue(found, "Award caption #" + (idx + 1) + " must contain: "
	                    + java.util.Arrays.toString(captions[i]));
	        }

	        LOG.info("✅ UI-10  awardImgs=" + awardImgs.size() + " loaded=" + awardLoaded);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-11  Stats / Metrics Section
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 11, groups = "ui",
	          description = "UI-11 | Stats — 'Growth-Focused Campaigns' and 'Average ROAS' metric labels visible")
	    public void ui11_statsSection() {
	        loadHome();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        s.assertTrue(body.contains("campaigns launched") || body.contains("growth-focused campaigns"),
	                     "'Growth-Focused Campaigns Launched' counter label must be visible");
	        s.assertTrue(body.contains("average roas") || body.contains("roas"),
	                     "'Average ROAS' metric label must be visible");
	        s.assertTrue(body.contains("efficiency") || body.contains("vanity metrics"),
	                     "ROAS sub-label copy must be visible");
	        s.assertTrue(body.contains("consumer and b2b") || body.contains("sme brands"),
	                     "Campaign counter sub-label must be visible");

	        LOG.info("✅ UI-11");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-12  Footer
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 12, groups = "ui",
	          description = "UI-12 | Footer — copyright year, brand name, 'All Rights Reserved', email link")
	    public void ui12_footer() {
	        loadHome();
	        scrollToBottom();
	        SoftAssert s = new SoftAssert();
	        String body = bodyText();

	        s.assertTrue(body.contains("2026") || body.contains("2025"), "Footer must contain copyright year");
	        s.assertTrue(body.contains("buzzworth"),                      "Footer must contain brand name");
	        s.assertTrue(body.contains("all rights reserved"),            "Footer must say 'All Rights Reserved'");

	        // Email link
	        List<WebElement> emailLinks = driver.findElements(By.cssSelector("a[href^='mailto:']"));
	        s.assertFalse(emailLinks.isEmpty(), "Footer must have at least one mailto: email link");
	        if (!emailLinks.isEmpty()) {
	            String href = emailLinks.get(0).getAttribute("href");
	            s.assertTrue(href != null && href.startsWith("mailto:"), "Email link must start with mailto:");
	            LOG.info("  email href=" + href);
	        }

	        // Decorative shape images
	        List<WebElement> shapeImgs = driver.findElements(By.cssSelector("img[src*='shaps']"));
	        s.assertFalse(shapeImgs.isEmpty(), "Footer decorative shape images must be present");

	        LOG.info("✅ UI-12");
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-13  All Images — src + load
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 13, groups = "ui",
	          description = "UI-13 | All <img> — non-blank src; ≤3 broken images across full page")
	    public void ui13_allImages() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        List<WebElement> all = driver.findElements(By.tagName("img"));
	        s.assertFalse(all.isEmpty(), "Page must have at least 1 image");

	        int blankSrc = 0, broken = 0;
	        for (WebElement img : all) {
	            String src = img.getAttribute("src");
	            if (src == null || src.isBlank()) {
					blankSrc++;
				} else if (!imgLoaded(img)) {
					broken++;
				}
	        }

	        s.assertEquals(blankSrc, 0,
	                "No <img> must have null/blank src. Blank count=" + blankSrc);
	        s.assertTrue(broken <= 3,
	                "Fewer than 3 images should be broken. Broken=" + broken + "/" + all.size());

	        LOG.info("✅ UI-13  total=" + all.size() + " blankSrc=" + blankSrc + " broken=" + broken);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-14  Responsive — 375 px Mobile
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 14, groups = "ui",
	          description = "UI-14 | Mobile 375px — no horizontal overflow, logo & form still accessible")
	    public void ui14_responsiveMobile() {
	        driver.manage().window().setSize(new Dimension(375, 812));
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        long bodyW  = ((Number) js.executeScript("return document.body.scrollWidth;")).longValue();
	        long windowW = ((Number) js.executeScript("return window.innerWidth;")).longValue();
	        s.assertTrue(bodyW <= windowW + 10,
	                "No horizontal scroll at 375px. bodyScrollWidth=" + bodyW + " innerWidth=" + windowW);

	        // Logo still present
	        s.assertNotNull(find("img[alt='logo']", "img[src*='buzzworth-logo']"),
	                "Logo must be present at 375px viewport");

	        // Form accessible
	        List<WebElement> selects = findAll("select");
	        s.assertFalse(selects.isEmpty(), "Service dropdown must be accessible on mobile");

	        // Key content visible
	        s.assertTrue(bodyText().contains("social presence") || bodyText().contains("consultation"),
	                "Key hero/form text must be accessible on mobile");

	        driver.manage().window().setSize(new Dimension(1440, 900)); // reset
	        LOG.info("✅ UI-14  bodyW=" + bodyW + " windowW=" + windowW);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-15  Responsive — 768 px Tablet
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 15, groups = "ui",
	          description = "UI-15 | Tablet 768px — no horizontal overflow, all sections remain accessible")
	    public void ui15_responsiveTablet() {
	        driver.manage().window().setSize(new Dimension(768, 1024));
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        long bodyW   = ((Number) js.executeScript("return document.body.scrollWidth;")).longValue();
	        long windowW = ((Number) js.executeScript("return window.innerWidth;")).longValue();
	        s.assertTrue(bodyW <= windowW + 10,
	                "No horizontal scroll at 768px. bodyScrollWidth=" + bodyW + " innerWidth=" + windowW);

	        s.assertTrue(bodyText().contains("what we do"),    "'What We Do' must be accessible on tablet");
	        s.assertTrue(bodyText().contains("awards"),        "'Awards' section must be accessible on tablet");

	        driver.manage().window().setSize(new Dimension(1440, 900)); // reset
	        LOG.info("✅ UI-15  bodyW=" + bodyW + " windowW=" + windowW);
	        s.assertAll();
	    }

	    // ─────────────────────────────────────────────────────────────
	    // UI-16  Page Meta / Title
	    // ─────────────────────────────────────────────────────────────
	    @Test(priority = 16, groups = "ui",
	          description = "UI-16 | Page <title> and Facebook Pixel present in page source")
	    public void ui16_metaAndTracking() {
	        loadHome();
	        SoftAssert s = new SoftAssert();

	        // Title tag non-blank (already tested in UI-01, here we check specific wording)
	        String title = driver.getTitle();
	        s.assertTrue(title.contains("Build Your") || title.contains("Social Presence")
	                  || title.contains("Buzzzworth"),
	                  "Page title must match expected brand title. Got: " + title);

	        // Facebook Pixel img (noscript fallback)
	        List<WebElement> fbPx = driver.findElements(By.cssSelector("img[src*='facebook.com/tr']"));
	        s.assertFalse(fbPx.isEmpty(), "Facebook Pixel noscript image must be present");

	        LOG.info("✅ UI-16  title='" + title + "'");
	        s.assertAll();
	    }
	}


