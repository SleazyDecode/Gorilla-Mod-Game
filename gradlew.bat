@echo off
setlocal
set "GRADLE_VERSION=8.13"
set "ROOT=%~dp0"
set "CACHE=%USERPROFILE%\.gradle\pearl-wtf"
set "DIST=%CACHE%\gradle-%GRADLE_VERSION%"
set "ZIP=%CACHE%\gradle-%GRADLE_VERSION%-bin.zip"
if not exist "%DIST%\bin\gradle.bat" (
  if not exist "%CACHE%" mkdir "%CACHE%"
  if not exist "%ZIP%" powershell -NoProfile -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
  powershell -NoProfile -Command "Expand-Archive -Force '%ZIP%' '%CACHE%'"
)
cd /d "%ROOT%"
call "%DIST%\bin\gradle.bat" :app:assembleDebug %*
if errorlevel 1 exit /b %errorlevel%
copy /Y "app\build\outputs\apk\debug\Pearl.wtf.apk" "Pearl.wtf.apk" >nul
echo.
echo Built: %ROOT%Pearl.wtf.apk
