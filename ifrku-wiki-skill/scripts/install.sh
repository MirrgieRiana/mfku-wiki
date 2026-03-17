#!/bin/bash
cd "$(dirname "$0")/.."
BUNDLE_PATH=build/vendor/bundle bundle install
