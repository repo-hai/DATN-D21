# Execution Report Template

## Metadata

| Field | Value |
|---|---|
| Project | DATN_BE |
| Module | Order Management |
| Build Time | 2026-04-12T18:55:27 |
| Branch | main |
| Commit | 1fd76f6 |
| Maven Command | .\\mvnw.cmd jacoco:prepare-agent test jacoco:report -DskipITs=true |
| Java Version | java version "22.0.2" 2024-07-16 |
| Maven Version | Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937) |

## Test Summary

| Metric | Value |
|---|---|
| Total Tests | 44 |
| Passed | 44 |
| Failed | 0 |
| Errors | 0 |
| Skipped | 0 |
| Success Rate | 100.00% |
| Total Duration (s) | 32.144 |

## Split By Runner

| Runner | Tests | Passed | Failed | Errors | Skipped |
|---|---:|---:|---:|---:|---:|
| Surefire (Unit) | 44 | 44 | 0 | 0 | 0 |
| Failsafe (Integration) | 0 | 0 | 0 | 0 | 0 |

## Test Classes (Top Slowest)

| # | Test Class | Tests | Failures | Errors | Skipped | Time (s) |
|---:|---|---:|---:|---:|---:|---:|
| 1 | com.DATN.Bej.BejApplicationTests | 1 | 0 | 0 | 0 | 22.926 |
| 2 | com.DATN.Bej.repository.product.OrderRepositoryDataJpaTest | 3 | 0 | 0 | 0 | 3.206 |
| 3 | com.DATN.Bej.controller.cart.CartControllerOrderTest | 2 | 0 | 0 | 0 | 1.513 |

## Failed/Error Tests

| Test Class | Test Method | Type | Message |
|---|---|---|---|
| N/A | N/A | N/A | No failed/error tests |

## Data Sources

- Surefire XML: `target/surefire-reports/*.xml`
- Failsafe XML: `target/failsafe-reports/*.xml`
- JaCoCo XML: `target/site/jacoco/jacoco.xml`
- JaCoCo CSV: `target/site/jacoco/jacoco.csv`

