#!/usr/bin/env bash
# Runs what CI runs: unit tests + screenshot comparison, lint, debug build.
#   --quick   skip instrumented UI tests (there are none yet; kept for the AGENTS.md contract)
set -euo pipefail
cd "$(dirname "$0")/.."

if [ -z "${JAVA_HOME:-}" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q '"21'; then
  if [ -x /usr/libexec/java_home ] && JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null); then
    export JAVA_HOME
  elif [ -d "/Applications/Android Studio.app/Contents/jbr/Contents/Home" ]; then
    export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  else
    echo "JDK 21 not found; set JAVA_HOME." >&2
    exit 1
  fi
fi
echo "Using JAVA_HOME=$JAVA_HOME"

./gradlew verifyPaparazziDebug lintDebug assembleDebug
echo "verify: OK"
