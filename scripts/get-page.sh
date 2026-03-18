#!/bin/bash
# WikiWiki APIを使って指定ページの内容を取得するスクリプト。
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
API_SCRIPTS="$REPO_ROOT/ifrku-wiki-skill/scripts"

[[ $# -eq 1 ]] || { echo "Usage: $0 <page-name>" >&2; exit 1; }

exec bash "$API_SCRIPTS/wikiwiki-get-page.sh" "$1"
