#!/usr/bin/env bash
set -euo pipefail
GRADLE_VERSION="${GRADLE_VERSION:-8.13}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CACHE="${HOME}/.cache/pearl-wtf"
DIST="$CACHE/gradle-$GRADLE_VERSION"
ZIP="$CACHE/gradle-$GRADLE_VERSION-bin.zip"
mkdir -p "$CACHE"
if [ ! -x "$DIST/bin/gradle" ]; then
  command -v java >/dev/null || { echo "Java 17+ is required."; exit 1; }
  if [ ! -f "$ZIP" ]; then
    curl -fL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
  fi
  rm -rf "$DIST.tmp"
  mkdir -p "$DIST.tmp"
  unzip -q "$ZIP" -d "$DIST.tmp"
  mv "$DIST.tmp/gradle-$GRADLE_VERSION" "$DIST"
  rm -rf "$DIST.tmp"
fi
cd "$ROOT"
"$DIST/bin/gradle" :app:assembleDebug "$@"
echo "APK: $ROOT/app/build/outputs/apk/debug/app-debug.apk"
