#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/pages" 2>/dev/null); then
  echo "$result"
else
  echo "Warning: API unreachable; falling back to local data" >&2
  json=$(find_wiki_json) || die "all.wiki.json not found"
  jq -r 'keys[]' "$json"
fi
