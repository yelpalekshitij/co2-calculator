#!/usr/bin/env bash
set -e

if [ -z "$ORS_TOKEN" ]; then
  echo "ERROR: ORS_TOKEN not set"
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

APP_UNIX="$SCRIPT_DIR/build/install/co2-calculator/bin/co2-calculator"
APP_WIN="$SCRIPT_DIR/build/install/co2-calculator/bin/co2-calculator.bat"

OS="$(uname -s)"

case "$OS" in
  Linux*|Darwin*)
    if [ ! -x "$APP_UNIX" ]; then
      echo "ERROR: Unix executable not found or not executable."
      echo "Run: ./gradlew installDist"
      exit 1
    fi
    exec "$APP_UNIX" "$@"
    ;;
  MINGW*|MSYS*|CYGWIN*)
    if [ ! -f "$APP_WIN" ]; then
      echo "ERROR: Windows executable not found."
      echo "Run: gradlew installDist"
      exit 1
    fi
    exec "$APP_WIN" "$@"
    ;;
  *)
    echo "Unsupported OS"
    exit 1
    ;;
esac
