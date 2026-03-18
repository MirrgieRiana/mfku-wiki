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

read_file() {
  local path="$1"
  [[ -f "$path" ]] || die "$path not found"
  local content
  content=$(<"$path")
  [[ -n "$content" ]] || die "$path is empty"
  echo "$content"
}

load_token() {
  read_file "$WIKIWIKI_TOKEN_FILE"
}

wikiwiki_curl() {
  local token
  token=$(load_token)
  curl --fail --silent --show-error -H "Authorization: Bearer $token" "$@"
}

# リポジトリルートの all.wiki.json を探す
find_wiki_json() {
  local dir
  dir="$(cd "$(dirname "$0")/../.." && pwd)"
  local json="$dir/all.wiki.json"
  if [[ -f "$json" ]]; then
    echo "$json"
  else
    return 1
  fi
}
