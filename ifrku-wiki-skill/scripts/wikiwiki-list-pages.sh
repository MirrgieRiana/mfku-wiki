#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

api_err_file=$(mktemp)
if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/pages" 2>"$api_err_file"); then
  rm -f "$api_err_file"
  echo "$result"
else
  echo "Warning: API failed; falling back to local data" >&2
  echo "  Reason: $(cat "$api_err_file" 2>/dev/null || echo "(unknown)")" >&2
  rm -f "$api_err_file"
  json=$(find_wiki_json) || die "all.wiki.json not found"
  jq -r 'keys[]' "$json"
fi
