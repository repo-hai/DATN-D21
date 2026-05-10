# Execution Report Template

## Metadata

| Field | Value |
|---|---|
| Project | DATN_BE |
| Module | Order Management |
| Build Time | {{BUILD_TIME_ISO}} |
| Branch | {{GIT_BRANCH}} |
| Commit | {{GIT_COMMIT}} |
| Maven Command | {{MAVEN_COMMAND}} |
| Java Version | {{JAVA_VERSION}} |
| Maven Version | {{MAVEN_VERSION}} |

## Test Summary

| Metric | Value |
|---|---|
| Total Tests | {{TOTAL_TESTS}} |
| Passed | {{PASSED_TESTS}} |
| Failed | {{FAILED_TESTS}} |
| Errors | {{ERROR_TESTS}} |
| Skipped | {{SKIPPED_TESTS}} |
| Success Rate | {{SUCCESS_RATE_PERCENT}} |
| Total Duration (s) | {{TOTAL_DURATION_SECONDS}} |

## Split By Runner

| Runner | Tests | Passed | Failed | Errors | Skipped |
|---|---:|---:|---:|---:|---:|
| Surefire (Unit) | {{SUREFIRE_TOTAL}} | {{SUREFIRE_PASSED}} | {{SUREFIRE_FAILED}} | {{SUREFIRE_ERRORS}} | {{SUREFIRE_SKIPPED}} |
| Failsafe (Integration) | {{FAILSAFE_TOTAL}} | {{FAILSAFE_PASSED}} | {{FAILSAFE_FAILED}} | {{FAILSAFE_ERRORS}} | {{FAILSAFE_SKIPPED}} |

## Test Classes (Top Slowest)

| # | Test Class | Tests | Failures | Errors | Skipped | Time (s) |
|---:|---|---:|---:|---:|---:|---:|
| 1 | {{CLASS_1}} | {{CLASS_1_TESTS}} | {{CLASS_1_FAILURES}} | {{CLASS_1_ERRORS}} | {{CLASS_1_SKIPPED}} | {{CLASS_1_TIME}} |
| 2 | {{CLASS_2}} | {{CLASS_2_TESTS}} | {{CLASS_2_FAILURES}} | {{CLASS_2_ERRORS}} | {{CLASS_2_SKIPPED}} | {{CLASS_2_TIME}} |
| 3 | {{CLASS_3}} | {{CLASS_3_TESTS}} | {{CLASS_3_FAILURES}} | {{CLASS_3_ERRORS}} | {{CLASS_3_SKIPPED}} | {{CLASS_3_TIME}} |

## Failed/Error Tests

| Test Class | Test Method | Type | Message |
|---|---|---|---|
| {{FAILED_CLASS}} | {{FAILED_METHOD}} | {{FAILED_TYPE}} | {{FAILED_MESSAGE}} |

## Data Sources

- Surefire XML: `target/surefire-reports/*.xml`
- Failsafe XML: `target/failsafe-reports/*.xml`
- JaCoCo XML: `target/site/jacoco/jacoco.xml`
- JaCoCo CSV: `target/site/jacoco/jacoco.csv`
