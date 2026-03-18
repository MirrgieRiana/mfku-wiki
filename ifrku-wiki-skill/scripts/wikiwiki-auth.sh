#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

require_env WIKIWIKI_KEY_ID
require_env WIKIWIKI_SECRET

PAYLOAD=$(jq -n --arg id "$WIKIWIKI_KEY_ID" --arg secret "$WIKIWIKI_SECRET" \
  '{api_key_id: $id, secret: $secret}')

RESPONSE=$(curl --fail --silent --show-error -X POST "$WIKIWIKI_API_BASE/::api/auth" \
  -H "Content-Type: application/json" \
  -d "$PAYLOAD") || die "API request failed"

TOKEN=$(echo "$RESPONSE" | jq -re '.token') || die "Failed to extract token from response"

mkdir -p "$WIKIWIKI_DIR"
echo "$TOKEN" > "$WIKIWIKI_TOKEN_FILE"
chmod 600 "$WIKIWIKI_TOKEN_FILE"
echo "Token saved to $WIKIWIKI_TOKEN_FILE" >&2
