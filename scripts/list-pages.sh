#!/bin/bash
# ローカルの all.wiki.json からページ一覧を取得するスクリプト。
# WikiWiki APIが利用可能な場合はAPIを使い、不可の場合はローカルデータにフォールバックする。
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
API_SCRIPTS="$REPO_ROOT/ifrku-wiki-skill/scripts"
JSON_FILE="$REPO_ROOT/all.wiki.json"

# APIスクリプトが使えるか試みる
if [[ -f "$API_SCRIPTS/wikiwiki-list-pages.sh" ]]; then
  if result=$(bash "$API_SCRIPTS/wikiwiki-list-pages.sh"); then
    echo "$result"
    exit 0
  fi
fi

# ローカルのall.wiki.jsonからページ一覧を取得
[[ -f "$JSON_FILE" ]] || { echo "Error: $JSON_FILE not found" >&2; exit 1; }
jq -r 'keys[]' "$JSON_FILE"
