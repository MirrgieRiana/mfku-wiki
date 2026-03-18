#!/bin/bash
set -euo pipefail
source "$(dirname "$0")/wikiwiki-common.sh"

wikiwiki_curl "$WIKIWIKI_API_BASE/pages"
