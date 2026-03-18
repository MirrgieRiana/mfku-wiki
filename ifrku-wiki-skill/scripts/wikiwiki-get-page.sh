#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

[[ $# -eq 1 ]] || die "Usage: $0 <page-name>"

PAGE_NAME=$(printf '%s' "$1" | jq -sRr @uri)

api_err_file=$(mktemp)
if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/page/$PAGE_NAME" 2>"$api_err_file"); then
  rm -f "$api_err_file"
  echo "$result"
else
  echo "Warning: API failed; falling back to local data" >&2
  echo "  Reason: $(cat "$api_err_file" 2>/dev/null || echo "(unknown)")" >&2
  rm -f "$api_err_file"
  json=$(find_wiki_json) || die "all.wiki.json not found"
  jq -re --arg name "$1" '.[$name]' "$json" || die "Page '$1' not found"
fi
