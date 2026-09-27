$ErrorActionPreference = "Stop"
$GradleVersion = if ($env:GRADLE_VERSION) { $env:GRADLE_VERSION } else { "8.13" }
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$Cache = Join-Path $HOME ".cache\pearl-wtf"
$Dist = Join-Path $Cache "gradle-$GradleVersion"
$Zip = Join-Path $Cache "gradle-$GradleVersion-bin.zip"
New-Item -ItemType Directory -Force -Path $Cache | Out-Null
if (-not (Test-Path (Join-Path $Dist "bin\gradle.bat"))) {
    if (-not (Test-Path $Zip)) {
        Invoke-WebRequest "https://services.gradle.org/distributions/gradle-$GradleVersion-bin.zip" -OutFile $Zip
    }
    Expand-Archive -Force $Zip $Cache
}
Set-Location $Root
& (Join-Path $Dist "gradle-$GradleVersion\bin\gradle.bat") :app:assembleDebug @args
Write-Host "APK: $Root\app\build\outputs\apk\debug\app-debug.apk"
