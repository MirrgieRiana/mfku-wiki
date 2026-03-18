#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

KEY_ID=$(read_file "$WIKIWIKI_DIR/WIKIWIKI_KEY_ID")
SECRET=$(read_file "$WIKIWIKI_DIR/WIKIWIKI_SECRET")

PAYLOAD=$(jq -n --arg id "$KEY_ID" --arg secret "$SECRET" \
  '{api_key_id: $id, secret: $secret}')

RESPONSE=$(curl --fail --silent --show-error -X POST "$WIKIWIKI_API_BASE/::api/auth" \
  -H "Content-Type: application/json" \
  -d "$PAYLOAD") || die "API request failed"

TOKEN=$(echo "$RESPONSE" | jq -re '.token') || die "Failed to extract token from response"

mkdir -p "$WIKIWIKI_DIR"
echo "$TOKEN" > "$WIKIWIKI_TOKEN_FILE"
chmod 600 "$WIKIWIKI_TOKEN_FILE"
echo "Token saved to $WIKIWIKI_TOKEN_FILE" >&2
