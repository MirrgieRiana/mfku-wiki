#!/bin/bash
cd "$(dirname "$0")/.."
BUNDLE_PATH="$PWD/build/vendor/bundle" bundle exec jekyll build \
    --config "$PWD/_config.yml" \
    --source "$PWD/build/jekyll-work" \
    --destination "$PWD/build/jekyll"
