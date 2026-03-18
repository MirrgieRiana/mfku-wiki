#!/bin/bash
# ローカルの all.wiki.json から指定ページの内容を取得するスクリプト。
# WikiWiki APIが利用可能な場合はAPIを使い、不可の場合はローカルデータにフォールバックする。
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
API_SCRIPTS="$REPO_ROOT/ifrku-wiki-skill/scripts"
JSON_FILE="$REPO_ROOT/all.wiki.json"

[[ $# -eq 1 ]] || { echo "Usage: $0 <page-name>" >&2; exit 1; }
PAGE_NAME="$1"

# APIスクリプトが使えるか試みる
if [[ -f "$API_SCRIPTS/wikiwiki-get-page.sh" ]]; then
  result=$(bash "$API_SCRIPTS/wikiwiki-get-page.sh" "$PAGE_NAME" 2>/dev/null) && {
    echo "$result"
    exit 0
  }
  echo "(API unreachable; falling back to local data)" >&2
fi

# ローカルのall.wiki.jsonからページ内容を取得
[[ -f "$JSON_FILE" ]] || { echo "Error: $JSON_FILE not found" >&2; exit 1; }
CONTENT=$(jq -re --arg name "$PAGE_NAME" '.[$name]' "$JSON_FILE") || {
  echo "Error: Page '$PAGE_NAME' not found" >&2
  exit 1
}
echo "$CONTENT"
