$ErrorActionPreference = "Stop"
$GradleVersion = if ($env:GRADLE_VERSION) { $env:GRADLE_VERSION } else { "8.13" }
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Cache = Join-Path $HOME ".cache\pearl-wtf"
$GradleHome = Join-Path $Cache "gradle-$GradleVersion\gradle-$GradleVersion"
$Zip = Join-Path $Cache "gradle-$GradleVersion-bin.zip"

New-Item -ItemType Directory -Force -Path $Cache | Out-Null

if (-not (Test-Path (Join-Path $GradleHome "bin\gradle.bat"))) {
    if (-not (Test-Path $Zip)) {
        Write-Host "Downloading Gradle $GradleVersion..."
        Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip" -OutFile $Zip
    }
    Write-Host "Extracting Gradle $GradleVersion..."
    Expand-Archive -Force $Zip $Cache
}

Set-Location $Root
& (Join-Path $GradleHome "bin\gradle.bat") :app:assembleDebug @args
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$Built = Join-Path $Root "app\build\outputs\apk\debug\Pearl.wtf.apk"
$Output = Join-Path $Root "Pearl.wtf.apk"
if (Test-Path $Built) {
    Copy-Item $Built $Output -Force
    Write-Host ""
    Write-Host "Built: $Output"
} else {
    throw "Build finished but Pearl.wtf.apk was not found at $Built"
}
