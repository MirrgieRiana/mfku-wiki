#!/bin/bash
# wikiwikiスクリプト群の共通定義。直接実行せず source して使う。

WIKIWIKI_ID="mifai2024"
WIKIWIKI_API_BASE="https://api.wikiwiki.jp/$WIKIWIKI_ID"
WIKIWIKI_DIR="$HOME/.wikiwiki"
WIKIWIKI_TOKEN_FILE="$WIKIWIKI_DIR/TOKEN"

die() {
  echo "Error: $*" >&2
  exit 1
}

require_env() {
  local name="$1"
  [[ -n "${!name:-}" ]] || die "Environment variable $name is not set"
}

load_token() {
  [[ -f "$WIKIWIKI_TOKEN_FILE" ]] || die "$WIKIWIKI_TOKEN_FILE not found. Run wikiwiki-auth.sh first."
  local token
  token=$(<"$WIKIWIKI_TOKEN_FILE")
  [[ -n "$token" ]] || die "$WIKIWIKI_TOKEN_FILE is empty"
  echo "$token"
}

wikiwiki_curl() {
  local token
  token=$(load_token)
  curl --fail --silent --show-error -H "Authorization: Bearer $token" "$@"
}

# リポジトリルートの all.wiki.json を探す
find_wiki_json() {
  local dir
  dir=$(cd "$(dirname "$0")" && pwd)
  while [[ "$dir" != "/" ]]; do
    if [[ -f "$dir/all.wiki.json" ]]; then
      echo "$dir/all.wiki.json"
      return 0
    fi
    dir=$(dirname "$dir")
  done
  return 1
}
