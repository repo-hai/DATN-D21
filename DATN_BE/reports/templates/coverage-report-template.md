# Coverage Report Template

## Metadata

| Field | Value |
|---|---|
| Project | DATN_BE |
| Module | Order Management |
| Build Time | {{BUILD_TIME_ISO}} |
| Branch | {{GIT_BRANCH}} |
| Commit | {{GIT_COMMIT}} |
| Coverage Source | JaCoCo (`target/site/jacoco`) |

## Overall Coverage

| Metric | Covered | Missed | Total | Coverage |
|---|---:|---:|---:|---:|
| Instructions | {{INST_COVERED}} | {{INST_MISSED}} | {{INST_TOTAL}} | {{INST_COVERAGE_PERCENT}} |
| Branches | {{BRANCH_COVERED}} | {{BRANCH_MISSED}} | {{BRANCH_TOTAL}} | {{BRANCH_COVERAGE_PERCENT}} |
| Lines | {{LINE_COVERED}} | {{LINE_MISSED}} | {{LINE_TOTAL}} | {{LINE_COVERAGE_PERCENT}} |
| Methods | {{METHOD_COVERED}} | {{METHOD_MISSED}} | {{METHOD_TOTAL}} | {{METHOD_COVERAGE_PERCENT}} |
| Classes | {{CLASS_COVERED}} | {{CLASS_MISSED}} | {{CLASS_TOTAL}} | {{CLASS_COVERAGE_PERCENT}} |

## Coverage Gate (Optional)

| Gate | Threshold | Actual | Result |
|---|---:|---:|---|
| Line Coverage | {{THRESHOLD_LINE_PERCENT}} | {{LINE_COVERAGE_PERCENT_NUMERIC}} | {{LINE_GATE_RESULT}} |
| Branch Coverage | {{THRESHOLD_BRANCH_PERCENT}} | {{BRANCH_COVERAGE_PERCENT_NUMERIC}} | {{BRANCH_GATE_RESULT}} |

## Lowest Coverage Packages

| # | Package | Instruction % | Branch % | Line % | Method % | Class % |
|---:|---|---:|---:|---:|---:|---:|
| 1 | {{PKG_1}} | {{PKG_1_INST_PERCENT}} | {{PKG_1_BRANCH_PERCENT}} | {{PKG_1_LINE_PERCENT}} | {{PKG_1_METHOD_PERCENT}} | {{PKG_1_CLASS_PERCENT}} |
| 2 | {{PKG_2}} | {{PKG_2_INST_PERCENT}} | {{PKG_2_BRANCH_PERCENT}} | {{PKG_2_LINE_PERCENT}} | {{PKG_2_METHOD_PERCENT}} | {{PKG_2_CLASS_PERCENT}} |
| 3 | {{PKG_3}} | {{PKG_3_INST_PERCENT}} | {{PKG_3_BRANCH_PERCENT}} | {{PKG_3_LINE_PERCENT}} | {{PKG_3_METHOD_PERCENT}} | {{PKG_3_CLASS_PERCENT}} |

## Lowest Coverage Classes

| # | Package | Class | Instruction % | Branch % | Line % | Method % |
|---:|---|---|---:|---:|---:|---:|
| 1 | {{CLS_1_PACKAGE}} | {{CLS_1_NAME}} | {{CLS_1_INST_PERCENT}} | {{CLS_1_BRANCH_PERCENT}} | {{CLS_1_LINE_PERCENT}} | {{CLS_1_METHOD_PERCENT}} |
| 2 | {{CLS_2_PACKAGE}} | {{CLS_2_NAME}} | {{CLS_2_INST_PERCENT}} | {{CLS_2_BRANCH_PERCENT}} | {{CLS_2_LINE_PERCENT}} | {{CLS_2_METHOD_PERCENT}} |
| 3 | {{CLS_3_PACKAGE}} | {{CLS_3_NAME}} | {{CLS_3_INST_PERCENT}} | {{CLS_3_BRANCH_PERCENT}} | {{CLS_3_LINE_PERCENT}} | {{CLS_3_METHOD_PERCENT}} |

## Data Source

- JaCoCo CSV: `target/site/jacoco/jacoco.csv`
- JaCoCo XML: `target/site/jacoco/jacoco.xml`
- HTML detail: `target/site/jacoco/index.html`
