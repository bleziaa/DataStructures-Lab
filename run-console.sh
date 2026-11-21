#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"
OUT="target/console-classes"
mkdir -p "$OUT"
mapfile -t SOURCES < <(find src/main/java/com/datastructureslab/models src/main/java/com/datastructureslab/structures -name '*.java' -print | sort)
SOURCES+=(src/main/java/com/datastructureslab/console/ConsoleApp.java)
javac --release 17 -encoding UTF-8 -d "$OUT" "${SOURCES[@]}"
exec java -cp "$OUT" com.datastructureslab.console.ConsoleApp
