#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/pages" 2>/dev/null); then
  echo "$result"
else
  echo "APIが利用できません。all.wiki.jsonにフォールバックします" >&2
  json_path=$(find_wiki_json) || die "all.wiki.jsonが見つかりません"
  jq -r 'keys[]' "$json_path"
fi
