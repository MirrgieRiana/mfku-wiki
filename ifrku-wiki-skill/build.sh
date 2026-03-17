#!/bin/bash
bundle exec jekyll build
rsync -a _site/ build/skill/
sed '1d' src/main/md/ifrku-wiki-skill.md | sed '1,/^---$/d' | sed '/^{% raw %}$/d; /^{% endraw %}$/d' > build/skill/ifrku-wiki-skill.md
sed '1d' src/main/md/SKILL.md | sed '1,/^---$/d' > build/skill/SKILL.md
