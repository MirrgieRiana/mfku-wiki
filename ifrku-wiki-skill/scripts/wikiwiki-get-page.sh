#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

[[ $# -eq 1 ]] || die "Usage: $0 <page-name>"

PAGE_NAME=$(printf '%s' "$1" | jq -sRr @uri)

if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/page/$PAGE_NAME" 2>/dev/null); then
  echo "$result"
else
  echo "APIが利用できません。all.wiki.jsonにフォールバックします" >&2
  json_path=$(find_wiki_json) || die "all.wiki.jsonが見つかりません"
  jq -r --arg name "$1" '.[$name] // empty' "$json_path"
fi
