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
  local tmpfile
  tmpfile=$(mktemp)
  local http_code
  http_code=$(curl --silent --show-error -w '%{http_code}' -o "$tmpfile" -H "Authorization: Bearer $token" "$@" 2>"$tmpfile.err")
  local curl_exit=$?
  if [[ $curl_exit -ne 0 ]]; then
    local err_msg
    err_msg=$(<"$tmpfile.err")
    rm -f "$tmpfile" "$tmpfile.err"
    die "API request failed (curl exit $curl_exit): $err_msg"
  fi
  if [[ "$http_code" -ge 400 ]]; then
    local body
    body=$(<"$tmpfile")
    rm -f "$tmpfile" "$tmpfile.err"
    die "API request failed (HTTP $http_code): $body"
  fi
  cat "$tmpfile"
  rm -f "$tmpfile" "$tmpfile.err"
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
