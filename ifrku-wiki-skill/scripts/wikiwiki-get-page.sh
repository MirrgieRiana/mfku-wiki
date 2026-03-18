#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

[[ $# -eq 1 ]] || die "Usage: $0 <page-name>"

PAGE_NAME=$(printf '%s' "$1" | jq -sRr @uri)

if result=$(wikiwiki_curl "$WIKIWIKI_API_BASE/page/$PAGE_NAME" 2>/dev/null); then
  echo "$result"
else
  echo "Warning: API unreachable; falling back to local data" >&2
  json=$(find_wiki_json) || die "all.wiki.json not found"
  jq -re --arg name "$1" '.[$name]' "$json" || die "Page '$1' not found"
fi
