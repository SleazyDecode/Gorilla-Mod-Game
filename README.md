# Pearl.wtf

Android starter/client for the Pearl.wtf project.

## Included
- Wireless ADB setup screen
- Pairing code, IP and pairing-port fields
- Copyable adb pair IP:PORT command
- Client / Terminal / Settings tabs
- Red / Blue / Purple / Black themes
- Attach UI
- Placeholder VR menu settings

## Build prerequisites

Local Android Studio:
- Android Studio
- JDK 17
- Android SDK Platform 36
- Android SDK Build-Tools 36.0.0
- Android SDK Platform-Tools

The repository also includes scripts/build.sh and scripts/build.ps1. They bootstrap Gradle 8.13 for a local debug build.

GitHub Actions provisions JDK 17, Android SDK components and Gradle automatically. A push to main builds a debug APK and publishes it as a GitHub Release asset.

## Wireless ADB
1. Enable Developer Options on the headset.
2. Enable Wireless debugging.
3. Select Pair device with pairing code.
4. Enter the displayed IP, pairing port and pairing code in Pearl.wtf.
5. The current client generates/copies the official adb pair IP:PORT command.

The current UI deliberately does not fake a successful ADB connection; pairing state is a placeholder until a scoped ADB transport is added.

## Scope
This starter does not contain Play Integrity / Meta Integrity bypasses, root-detection bypasses, falsified attestation, anti-cheat evasion, or game-process injection.
