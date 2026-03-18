#!/bin/bash
# WikiWiki APIを使ってページ一覧を取得するスクリプト。
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
API_SCRIPTS="$REPO_ROOT/ifrku-wiki-skill/scripts"

exec bash "$API_SCRIPTS/wikiwiki-list-pages.sh"
