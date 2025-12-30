# End-to-End Testing using Playwright in Java
### v1.1, 2025-12-31

This is a project to showcase basic examples for E2E testing using Playwright, written in Java.

## 1. playwright-demo-site
In order to test it, we need to start up the website. To do this, in the terminal console in the project folder, run the following command:

``` node server.js ```

The output should result in the website opening at [http://localhost:8080/](http://localhost:8080/).


## 2. e2e-tests
After initializing the maven project, run the following command on the terminal console from the project root:

``` mvn clean test ```

## CI Pipelines
The defined E2E tests will be run after every commit to the main branch, making sure that there is no faulty code being committed. If a commit to the main branch fails, it is likely that the tests are failing.
