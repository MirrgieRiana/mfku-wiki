#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

[[ $# -eq 1 ]] || die "Usage: $0 <page-name>"

PAGE_NAME=$(printf '%s' "$1" | jq -sRr @uri)

wikiwiki_curl "$WIKIWIKI_API_BASE/page/$PAGE_NAME"
