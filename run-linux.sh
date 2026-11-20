#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$(realpath "$0")")"
if ! command -v java >/dev/null 2>&1; then
  echo "Error: instala Java 17 o superior." >&2
  exit 1
fi
if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: instala Maven 3.9 o superior." >&2
  exit 1
fi
exec mvn -q javafx:run
