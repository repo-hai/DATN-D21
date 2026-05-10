param(
    [string]$ProjectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$MavenCommand = ".\\mvnw.cmd verify",
    [double]$LineThreshold = 80,
    [double]$BranchThreshold = 70
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-StringOrDefault {
    param(
        [AllowNull()][object]$Value,
        [string]$Default = "N/A"
    )

    if ($null -eq $Value) { return $Default }
    $text = [string]$Value
    if ([string]::IsNullOrWhiteSpace($text)) { return $Default }
    return $text
}

function To-Int {
    param([AllowNull()][object]$Value)
    if ($null -eq $Value -or [string]::IsNullOrWhiteSpace([string]$Value)) { return 0 }
    return [int]$Value
}

function To-Double {
    param([AllowNull()][object]$Value)
    if ($null -eq $Value -or [string]::IsNullOrWhiteSpace([string]$Value)) { return 0.0 }
    return [double]$Value
}

function Format-Percent {
    param(
        [double]$Covered,
        [double]$Missed
    )

    $total = $Covered + $Missed
    if ($total -le 0) { return "N/A" }
    return "{0:N2}%" -f (($Covered / $total) * 100.0)
}

function Format-PercentNumeric {
    param(
        [double]$Covered,
        [double]$Missed
    )

    $total = $Covered + $Missed
    if ($total -le 0) { return "0.00" }
    return "{0:N2}" -f (($Covered / $total) * 100.0)
}

function Get-TestStats {
    param([string]$ReportDir)

    $result = [ordered]@{
        Total = 0
        Failures = 0
        Errors = 0
        Skipped = 0
        Passed = 0
        Duration = 0.0
        Classes = @()
        FailedCases = @()
    }

    if (-not (Test-Path $ReportDir)) {
        return $result
    }

    $files = Get-ChildItem -Path $ReportDir -Filter "TEST-*.xml" -File -ErrorAction SilentlyContinue
    foreach ($file in $files) {
        [xml]$xml = Get-Content -Path $file.FullName
        $suite = $xml.testsuite
        if ($null -eq $suite) { continue }

        $tests = To-Int $suite.tests
        $failures = To-Int $suite.failures
        $errors = To-Int $suite.errors
        $skipped = To-Int $suite.skipped
        $time = To-Double $suite.time
        $passed = $tests - $failures - $errors - $skipped

        $result.Total += $tests
        $result.Failures += $failures
        $result.Errors += $errors
        $result.Skipped += $skipped
        $result.Passed += $passed
        $result.Duration += $time

        $className = Get-StringOrDefault $suite.name "(unknown)"
        $result.Classes += [pscustomobject]@{
            Name = $className
            Tests = $tests
            Failures = $failures
            Errors = $errors
            Skipped = $skipped
            Time = $time
        }

        foreach ($case in @($suite.testcase)) {
            if ($null -eq $case) { continue }
            $hasFailure = $case.PSObject.Properties.Name -contains "failure"
            $hasError = $case.PSObject.Properties.Name -contains "error"
            if ($hasFailure -or $hasError) {
                $failureNode = if ($hasFailure) { $case.failure } else { $case.error }
                $failureType = if ($hasFailure) { "FAILURE" } else { "ERROR" }
                $message = Get-StringOrDefault $failureNode.message "(no message)"
                $result.FailedCases += [pscustomobject]@{
                    Class = Get-StringOrDefault $case.classname "(unknown)"
                    Method = Get-StringOrDefault $case.name "(unknown)"
                    Type = $failureType
                    Message = $message.Replace("`r", " ").Replace("`n", " ")
                }
            }
        }
    }

    return $result
}

function Get-Counter {
    param(
        [xml]$Xml,
        [string]$Type
    )

    $counterNodes = @($Xml.SelectNodes("/*[local-name()='report']/*[local-name()='counter']"))
    $counter = @($counterNodes | Where-Object { $_.type -eq $Type }) | Select-Object -First 1
    if ($null -eq $counter) {
        return [pscustomobject]@{ Missed = 0; Covered = 0 }
    }

    return [pscustomobject]@{
        Missed = To-Int $counter.missed
        Covered = To-Int $counter.covered
    }
}

function Get-CoverageRows {
    param([string]$JacocoCsvPath)

    if (-not (Test-Path $JacocoCsvPath)) {
        return @()
    }

    return Import-Csv -Path $JacocoCsvPath
}

function Get-PercentageFromRow {
    param(
        [object]$Row,
        [string]$CoveredColumn,
        [string]$MissedColumn
    )

    $covered = To-Double $Row.$CoveredColumn
    $missed = To-Double $Row.$MissedColumn
    return Format-PercentNumeric -Covered $covered -Missed $missed
}

function Ensure-MapValue {
    param(
        [hashtable]$Map,
        [string]$Key,
        [AllowNull()][object]$Value
    )

    $Map[$Key] = Get-StringOrDefault $Value
}

function Fill-Template {
    param(
        [string]$TemplatePath,
        [string]$OutputPath,
        [hashtable]$Tokens
    )

    $content = Get-Content -Path $TemplatePath -Raw
    foreach ($key in $Tokens.Keys) {
        $content = $content -replace [regex]::Escape("{{${key}}}"), [string]$Tokens[$key]
    }
    Set-Content -Path $OutputPath -Value $content -Encoding UTF8
}

$templatesDir = Join-Path $ProjectRoot "reports/templates"
$outputDir = Join-Path $ProjectRoot "reports/generated"
$surefireDir = Join-Path $ProjectRoot "target/surefire-reports"
$failsafeDir = Join-Path $ProjectRoot "target/failsafe-reports"
$jacocoXmlPath = Join-Path $ProjectRoot "target/site/jacoco/jacoco.xml"
$jacocoCsvPath = Join-Path $ProjectRoot "target/site/jacoco/jacoco.csv"

if (-not (Test-Path $templatesDir)) {
    throw "Template directory not found: $templatesDir"
}

New-Item -Path $outputDir -ItemType Directory -Force | Out-Null

$nowIso = (Get-Date).ToString("s")
$gitBranch = "N/A"
$gitCommit = "N/A"
$javaVersion = "N/A"
$mavenVersion = "N/A"

try {
    $gitBranch = (git -C $ProjectRoot rev-parse --abbrev-ref HEAD 2>$null).Trim()
} catch {}
try {
    $gitCommit = (git -C $ProjectRoot rev-parse --short HEAD 2>$null).Trim()
} catch {}
try {
    $javaOutput = @(& cmd /c "java -version 2>&1")
    $javaLine = $javaOutput | Select-Object -First 1
    $javaVersion = Get-StringOrDefault $javaLine
} catch {}
try {
    $mvnLine = (& mvn -version 2>&1 | Select-Object -First 1)
    $mavenVersion = Get-StringOrDefault $mvnLine
} catch {}
if ($mavenVersion -eq "N/A") {
    try {
        $mvnwLine = (& "$ProjectRoot\mvnw.cmd" -version 2>&1 | Select-Object -First 1)
        $mavenVersion = Get-StringOrDefault $mvnwLine
    } catch {}
}

$surefire = Get-TestStats -ReportDir $surefireDir
$failsafe = Get-TestStats -ReportDir $failsafeDir

$totalTests = $surefire.Total + $failsafe.Total
$totalFailed = $surefire.Failures + $failsafe.Failures
$totalErrors = $surefire.Errors + $failsafe.Errors
$totalSkipped = $surefire.Skipped + $failsafe.Skipped
$totalPassed = $surefire.Passed + $failsafe.Passed
$totalDuration = $surefire.Duration + $failsafe.Duration
$successRate = if ($totalTests -gt 0) { "{0:N2}%" -f (($totalPassed / $totalTests) * 100.0) } else { "N/A" }

$allClasses = @($surefire.Classes + $failsafe.Classes) | Sort-Object -Property Time -Descending
$top3Classes = @($allClasses | Select-Object -First 3)
while ($top3Classes.Count -lt 3) {
    $top3Classes += [pscustomobject]@{ Name = "N/A"; Tests = 0; Failures = 0; Errors = 0; Skipped = 0; Time = 0.0 }
}

$failedCases = @($surefire.FailedCases + $failsafe.FailedCases)
$firstFailure = if ($failedCases.Count -gt 0) { $failedCases[0] } else {
    [pscustomobject]@{ Class = "N/A"; Method = "N/A"; Type = "N/A"; Message = "No failed/error tests" }
}

$execTokens = @{}
Ensure-MapValue -Map $execTokens -Key "BUILD_TIME_ISO" -Value $nowIso
Ensure-MapValue -Map $execTokens -Key "GIT_BRANCH" -Value $gitBranch
Ensure-MapValue -Map $execTokens -Key "GIT_COMMIT" -Value $gitCommit
Ensure-MapValue -Map $execTokens -Key "MAVEN_COMMAND" -Value $MavenCommand
Ensure-MapValue -Map $execTokens -Key "JAVA_VERSION" -Value $javaVersion
Ensure-MapValue -Map $execTokens -Key "MAVEN_VERSION" -Value $mavenVersion
Ensure-MapValue -Map $execTokens -Key "TOTAL_TESTS" -Value $totalTests
Ensure-MapValue -Map $execTokens -Key "PASSED_TESTS" -Value $totalPassed
Ensure-MapValue -Map $execTokens -Key "FAILED_TESTS" -Value $totalFailed
Ensure-MapValue -Map $execTokens -Key "ERROR_TESTS" -Value $totalErrors
Ensure-MapValue -Map $execTokens -Key "SKIPPED_TESTS" -Value $totalSkipped
Ensure-MapValue -Map $execTokens -Key "SUCCESS_RATE_PERCENT" -Value $successRate
Ensure-MapValue -Map $execTokens -Key "TOTAL_DURATION_SECONDS" -Value ("{0:N3}" -f $totalDuration)
Ensure-MapValue -Map $execTokens -Key "SUREFIRE_TOTAL" -Value $surefire.Total
Ensure-MapValue -Map $execTokens -Key "SUREFIRE_PASSED" -Value $surefire.Passed
Ensure-MapValue -Map $execTokens -Key "SUREFIRE_FAILED" -Value $surefire.Failures
Ensure-MapValue -Map $execTokens -Key "SUREFIRE_ERRORS" -Value $surefire.Errors
Ensure-MapValue -Map $execTokens -Key "SUREFIRE_SKIPPED" -Value $surefire.Skipped
Ensure-MapValue -Map $execTokens -Key "FAILSAFE_TOTAL" -Value $failsafe.Total
Ensure-MapValue -Map $execTokens -Key "FAILSAFE_PASSED" -Value $failsafe.Passed
Ensure-MapValue -Map $execTokens -Key "FAILSAFE_FAILED" -Value $failsafe.Failures
Ensure-MapValue -Map $execTokens -Key "FAILSAFE_ERRORS" -Value $failsafe.Errors
Ensure-MapValue -Map $execTokens -Key "FAILSAFE_SKIPPED" -Value $failsafe.Skipped

for ($i = 1; $i -le 3; $i++) {
    $item = $top3Classes[$i - 1]
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}") -Value $item.Name
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}_TESTS") -Value $item.Tests
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}_FAILURES") -Value $item.Failures
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}_ERRORS") -Value $item.Errors
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}_SKIPPED") -Value $item.Skipped
    Ensure-MapValue -Map $execTokens -Key ("CLASS_${i}_TIME") -Value ("{0:N3}" -f [double]$item.Time)
}

Ensure-MapValue -Map $execTokens -Key "FAILED_CLASS" -Value $firstFailure.Class
Ensure-MapValue -Map $execTokens -Key "FAILED_METHOD" -Value $firstFailure.Method
Ensure-MapValue -Map $execTokens -Key "FAILED_TYPE" -Value $firstFailure.Type
Ensure-MapValue -Map $execTokens -Key "FAILED_MESSAGE" -Value $firstFailure.Message

$covTokens = @{}
Ensure-MapValue -Map $covTokens -Key "BUILD_TIME_ISO" -Value $nowIso
Ensure-MapValue -Map $covTokens -Key "GIT_BRANCH" -Value $gitBranch
Ensure-MapValue -Map $covTokens -Key "GIT_COMMIT" -Value $gitCommit
Ensure-MapValue -Map $covTokens -Key "THRESHOLD_LINE_PERCENT" -Value ("{0:N2}%" -f $LineThreshold)
Ensure-MapValue -Map $covTokens -Key "THRESHOLD_BRANCH_PERCENT" -Value ("{0:N2}%" -f $BranchThreshold)

if (Test-Path $jacocoXmlPath) {
    [xml]$jacocoXml = Get-Content -Path $jacocoXmlPath -Raw

    $inst = Get-Counter -Xml $jacocoXml -Type "INSTRUCTION"
    $branch = Get-Counter -Xml $jacocoXml -Type "BRANCH"
    $line = Get-Counter -Xml $jacocoXml -Type "LINE"
    $method = Get-Counter -Xml $jacocoXml -Type "METHOD"
    $class = Get-Counter -Xml $jacocoXml -Type "CLASS"

    Ensure-MapValue -Map $covTokens -Key "INST_COVERED" -Value $inst.Covered
    Ensure-MapValue -Map $covTokens -Key "INST_MISSED" -Value $inst.Missed
    Ensure-MapValue -Map $covTokens -Key "INST_TOTAL" -Value ($inst.Covered + $inst.Missed)
    Ensure-MapValue -Map $covTokens -Key "INST_COVERAGE_PERCENT" -Value (Format-Percent -Covered $inst.Covered -Missed $inst.Missed)

    Ensure-MapValue -Map $covTokens -Key "BRANCH_COVERED" -Value $branch.Covered
    Ensure-MapValue -Map $covTokens -Key "BRANCH_MISSED" -Value $branch.Missed
    Ensure-MapValue -Map $covTokens -Key "BRANCH_TOTAL" -Value ($branch.Covered + $branch.Missed)
    Ensure-MapValue -Map $covTokens -Key "BRANCH_COVERAGE_PERCENT" -Value (Format-Percent -Covered $branch.Covered -Missed $branch.Missed)

    Ensure-MapValue -Map $covTokens -Key "LINE_COVERED" -Value $line.Covered
    Ensure-MapValue -Map $covTokens -Key "LINE_MISSED" -Value $line.Missed
    Ensure-MapValue -Map $covTokens -Key "LINE_TOTAL" -Value ($line.Covered + $line.Missed)
    Ensure-MapValue -Map $covTokens -Key "LINE_COVERAGE_PERCENT" -Value (Format-Percent -Covered $line.Covered -Missed $line.Missed)

    Ensure-MapValue -Map $covTokens -Key "METHOD_COVERED" -Value $method.Covered
    Ensure-MapValue -Map $covTokens -Key "METHOD_MISSED" -Value $method.Missed
    Ensure-MapValue -Map $covTokens -Key "METHOD_TOTAL" -Value ($method.Covered + $method.Missed)
    Ensure-MapValue -Map $covTokens -Key "METHOD_COVERAGE_PERCENT" -Value (Format-Percent -Covered $method.Covered -Missed $method.Missed)

    Ensure-MapValue -Map $covTokens -Key "CLASS_COVERED" -Value $class.Covered
    Ensure-MapValue -Map $covTokens -Key "CLASS_MISSED" -Value $class.Missed
    Ensure-MapValue -Map $covTokens -Key "CLASS_TOTAL" -Value ($class.Covered + $class.Missed)
    Ensure-MapValue -Map $covTokens -Key "CLASS_COVERAGE_PERCENT" -Value (Format-Percent -Covered $class.Covered -Missed $class.Missed)

    $lineCoverageNumeric = [double](Format-PercentNumeric -Covered $line.Covered -Missed $line.Missed)
    $branchCoverageNumeric = [double](Format-PercentNumeric -Covered $branch.Covered -Missed $branch.Missed)

    Ensure-MapValue -Map $covTokens -Key "LINE_COVERAGE_PERCENT_NUMERIC" -Value ("{0:N2}%" -f $lineCoverageNumeric)
    Ensure-MapValue -Map $covTokens -Key "BRANCH_COVERAGE_PERCENT_NUMERIC" -Value ("{0:N2}%" -f $branchCoverageNumeric)
    Ensure-MapValue -Map $covTokens -Key "LINE_GATE_RESULT" -Value ($(if ($lineCoverageNumeric -ge $LineThreshold) { "PASS" } else { "FAIL" }))
    Ensure-MapValue -Map $covTokens -Key "BRANCH_GATE_RESULT" -Value ($(if ($branchCoverageNumeric -ge $BranchThreshold) { "PASS" } else { "FAIL" }))
} else {
    $defaults = @(
        "INST_COVERED","INST_MISSED","INST_TOTAL","INST_COVERAGE_PERCENT",
        "BRANCH_COVERED","BRANCH_MISSED","BRANCH_TOTAL","BRANCH_COVERAGE_PERCENT",
        "LINE_COVERED","LINE_MISSED","LINE_TOTAL","LINE_COVERAGE_PERCENT",
        "METHOD_COVERED","METHOD_MISSED","METHOD_TOTAL","METHOD_COVERAGE_PERCENT",
        "CLASS_COVERED","CLASS_MISSED","CLASS_TOTAL","CLASS_COVERAGE_PERCENT",
        "LINE_COVERAGE_PERCENT_NUMERIC","BRANCH_COVERAGE_PERCENT_NUMERIC",
        "LINE_GATE_RESULT","BRANCH_GATE_RESULT"
    )
    foreach ($name in $defaults) {
        Ensure-MapValue -Map $covTokens -Key $name -Value "N/A"
    }
}

$coverageRows = @(Get-CoverageRows -JacocoCsvPath $jacocoCsvPath)

$packageRows = @()
if ($coverageRows.Count -gt 0) {
    $packageRows = @($coverageRows |
        Group-Object -Property PACKAGE |
        ForEach-Object {
            $pkg = $_.Name
            $groupRows = $_.Group

            $iMissed = ($groupRows | Measure-Object -Property INSTRUCTION_MISSED -Sum).Sum
            $iCovered = ($groupRows | Measure-Object -Property INSTRUCTION_COVERED -Sum).Sum
            $bMissed = ($groupRows | Measure-Object -Property BRANCH_MISSED -Sum).Sum
            $bCovered = ($groupRows | Measure-Object -Property BRANCH_COVERED -Sum).Sum
            $lMissed = ($groupRows | Measure-Object -Property LINE_MISSED -Sum).Sum
            $lCovered = ($groupRows | Measure-Object -Property LINE_COVERED -Sum).Sum
            $mMissed = ($groupRows | Measure-Object -Property METHOD_MISSED -Sum).Sum
            $mCovered = ($groupRows | Measure-Object -Property METHOD_COVERED -Sum).Sum
            [pscustomobject]@{
                Package = $pkg
                InstPct = (Format-PercentNumeric -Covered (To-Double $iCovered) -Missed (To-Double $iMissed))
                BranchPct = (Format-PercentNumeric -Covered (To-Double $bCovered) -Missed (To-Double $bMissed))
                LinePct = (Format-PercentNumeric -Covered (To-Double $lCovered) -Missed (To-Double $lMissed))
                MethodPct = (Format-PercentNumeric -Covered (To-Double $mCovered) -Missed (To-Double $mMissed))
                ClassPct = "N/A"
            }
        } |
        Sort-Object -Property @{ Expression = { [double]$_.LinePct } }, @{ Expression = { [double]$_.BranchPct } })
}

$classRows = @()
if ($coverageRows.Count -gt 0) {
    $classRows = @($coverageRows |
        Select-Object @{
            Name = "Package"; Expression = { $_.PACKAGE }
        }, @{
            Name = "Class"; Expression = { $_.CLASS }
        }, @{
            Name = "InstPct"; Expression = { Get-PercentageFromRow -Row $_ -CoveredColumn "INSTRUCTION_COVERED" -MissedColumn "INSTRUCTION_MISSED" }
        }, @{
            Name = "BranchPct"; Expression = { Get-PercentageFromRow -Row $_ -CoveredColumn "BRANCH_COVERED" -MissedColumn "BRANCH_MISSED" }
        }, @{
            Name = "LinePct"; Expression = { Get-PercentageFromRow -Row $_ -CoveredColumn "LINE_COVERED" -MissedColumn "LINE_MISSED" }
        }, @{
            Name = "MethodPct"; Expression = { Get-PercentageFromRow -Row $_ -CoveredColumn "METHOD_COVERED" -MissedColumn "METHOD_MISSED" }
        } |
        Sort-Object -Property @{ Expression = { [double]$_.LinePct } }, @{ Expression = { [double]$_.BranchPct } })
}

$top3Packages = @($packageRows | Select-Object -First 3)
$top3ClassesCov = @($classRows | Select-Object -First 3)

while ($top3Packages.Count -lt 3) {
    $top3Packages += [pscustomobject]@{ Package = "N/A"; InstPct = "N/A"; BranchPct = "N/A"; LinePct = "N/A"; MethodPct = "N/A"; ClassPct = "N/A" }
}
while ($top3ClassesCov.Count -lt 3) {
    $top3ClassesCov += [pscustomobject]@{ Package = "N/A"; Class = "N/A"; InstPct = "N/A"; BranchPct = "N/A"; LinePct = "N/A"; MethodPct = "N/A" }
}

for ($i = 1; $i -le 3; $i++) {
    $pkg = $top3Packages[$i - 1]
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}") -Value $pkg.Package
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}_INST_PERCENT") -Value (Get-StringOrDefault $pkg.InstPct)
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}_BRANCH_PERCENT") -Value (Get-StringOrDefault $pkg.BranchPct)
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}_LINE_PERCENT") -Value (Get-StringOrDefault $pkg.LinePct)
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}_METHOD_PERCENT") -Value (Get-StringOrDefault $pkg.MethodPct)
    Ensure-MapValue -Map $covTokens -Key ("PKG_${i}_CLASS_PERCENT") -Value (Get-StringOrDefault $pkg.ClassPct)

    $cls = $top3ClassesCov[$i - 1]
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_PACKAGE") -Value $cls.Package
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_NAME") -Value $cls.Class
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_INST_PERCENT") -Value (Get-StringOrDefault $cls.InstPct)
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_BRANCH_PERCENT") -Value (Get-StringOrDefault $cls.BranchPct)
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_LINE_PERCENT") -Value (Get-StringOrDefault $cls.LinePct)
    Ensure-MapValue -Map $covTokens -Key ("CLS_${i}_METHOD_PERCENT") -Value (Get-StringOrDefault $cls.MethodPct)
}

Fill-Template -TemplatePath (Join-Path $templatesDir "execution-report-template.md") -OutputPath (Join-Path $outputDir "execution-report.md") -Tokens $execTokens
Fill-Template -TemplatePath (Join-Path $templatesDir "execution-report-template.csv") -OutputPath (Join-Path $outputDir "execution-report.csv") -Tokens $execTokens
Fill-Template -TemplatePath (Join-Path $templatesDir "coverage-report-template.md") -OutputPath (Join-Path $outputDir "coverage-report.md") -Tokens $covTokens
Fill-Template -TemplatePath (Join-Path $templatesDir "coverage-report-template.csv") -OutputPath (Join-Path $outputDir "coverage-report.csv") -Tokens $covTokens

Write-Host "Generated reports:"
Write-Host " - $outputDir/execution-report.md"
Write-Host " - $outputDir/execution-report.csv"
Write-Host " - $outputDir/coverage-report.md"
Write-Host " - $outputDir/coverage-report.csv"