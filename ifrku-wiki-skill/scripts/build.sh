#!/bin/bash
cd "$(dirname "$0")/.."
bash scripts/jekyll-build.sh
mkdir -p build/skill
rsync -a _site/ build/skill/
sed '1d' src/main/jekyll/ifrku-wiki-skill.md | sed '1,/^---$/d' | sed '/^{% raw %}$/d; /^{% endraw %}$/d' > build/skill/ifrku-wiki-skill.md
sed '1d' src/main/jekyll/SKILL.md | sed '1,/^---$/d' > build/skill/SKILL.md
