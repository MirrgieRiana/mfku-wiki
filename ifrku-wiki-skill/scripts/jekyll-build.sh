#!/bin/bash
cd "$(dirname "$0")/.."
mkdir -p build/jekyll-work
rsync -a --delete src/main/jekyll/ build/jekyll-work/
BUNDLE_PATH="$PWD/build/vendor/bundle" bundle exec jekyll build \
    --config "$PWD/_config.yml" \
    --source "$PWD/build/jekyll-work" \
    --destination "$PWD/build/jekyll"
