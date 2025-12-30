package org.testing.e2etests;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomePageE2ETest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    static void teardownAll() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();

        context.tracing().start(
                new Tracing.StartOptions()
                        .setSnapshots(true)
                        .setScreenshots(true)
                        .setSources(true)
        );

        page = context.newPage();
    }

    @AfterEach
    void teardown(TestInfo testInfo) {
        String testName = testInfo.getDisplayName().replace(" ", "_");
        context.tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/" + testName + "-trace.zip"))
        );

        context.close();
    }

    @Test
    void homePageLoads() {
        page.navigate("http://localhost:8080");

        Assertions.assertTrue(
                page.getByTestId("page-title").textContent().contains("Playwright E2E Demo")
        );
    }

    @Test
    void userCanLogin() {
        page.navigate("http://localhost:8080");

        page.getByLabel("Username").fill("testUser1");
        page.getByLabel("Password").fill("pwd1");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in")).click();

        page.waitForURL("http://localhost:8080/welcome.html?username=testUser1");

        Assertions.assertTrue(
                page.getByTestId("welcome-message").textContent().contains("testUser1")
        );

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("target/screenshots/success.png"))
                .setFullPage(true)
        );
    }

    @Test
    void homePageShouldHaveNoAccessibilityViolations() {
        page.navigate("http://localhost:8080");
        AxeResults results = new AxeBuilder(page).analyze();
        assertEquals(Collections.emptyList(), results.getViolations(),
                () -> "Accessibility violations found: " + results.getViolations().toString());
    }

    @Test
    void passwordInputShouldHaveNoAccessibilityViolations() {
        page.navigate("http://localhost:8080");
        // It is important to waitFor() the page to be in the desired
        // state *before* running analyze(). Otherwise, axe might not
        // find all the elements your test expects it to scan.
        // Wait until the password field is present and ready
        page.getByTestId("password-field").waitFor();
        AxeResults results = new AxeBuilder(page)
                .include("[data-testid='password-field']")
                .analyze();
        assertEquals(
                Collections.emptyList(),
                results.getViolations(),
                () -> "Accessibility violations found: " + results.getViolations()
        );
    }

    @Test
    void homePageShouldNotHaveWCAGViolations() {
        page.navigate("http://localhost:8080");
        AxeResults accessibilityScanResults = new AxeBuilder(page)
                .withTags(Arrays.asList("wcag2a", "wcag2aa", "wcag21a", "wcag21aa"))
                .analyze();
        assertEquals(Collections.emptyList(), accessibilityScanResults.getViolations());
    }
}
