# WikiWiki API認証

認証情報ファイルの保管場所:
- `~/.ssh/WIKIWIKI_KEY_ID`
- `~/.ssh/WIKIWIKI_SECRET`

認証の実行例:

```
WIKIWIKI_KEY_ID=$(cat ~/.ssh/WIKIWIKI_KEY_ID) WIKIWIKI_SECRET=$(cat ~/.ssh/WIKIWIKI_SECRET) scripts/wikiwiki-auth.sh
```

# Wikiページの取得方法

Wikiページの内容を取得するには `scripts/wikiwiki-get-page.sh` を使う。WebFetchは使わないこと。

# Bash権限パターン

`.claude/settings.json` の許可パターンはコマンド文字列に対してマッチする。スクリプトは絶対パスではなく相対パス（`scripts/wikiwiki-get-page.sh 磁鉄鉱`）で実行すること。

# Wikiダンプの技術的特性

- ダンプ元ファイル（`wiki-dump/dumps/*.tar.gz`）の中身はEUC-JPエンコード
- ファイル名は16進数エンコード（EUC-JPバイト列の16進表現）
- `build-logic/src/main/kotlin/Utils.kt` にデコード処理がある
- `:` で始まる特殊ページ（`:Footer`, `:Header`等）はダンプ生成時にスキップされる
- ダンプを更新するには `wiki-dump/build.gradle.kts` の `srcZipFileName` を新しいファイル名に変更してビルド

# Wiki記述規約とノウハウ

- Wiki記法はPukiWiki構文（見出し: `*`, `**`, `***`、リスト: `-`, `--`）
- `ifrku-wiki-skill/src/main/jekyll/ifrku-wiki-skill.md` に記述規約の詳細がある
- `ifrku-wiki-skill/src/main/jekyll/KNOWHOW.md` に教訓や資料リンクがまとまっている

# 編集提案ビューワー

`ifrku-wiki-skill/src/main/resources/wiki-proposals-template.html` は、diff形式の編集提案を表示するHTMLテンプレート。JSONデータを埋め込んで使う。各行の `type` は `a`（追加）, `d`（削除）, `c`（変更なし）。

# ビルドシステム

- Gradleマルチプロジェクト構成（`wiki-dump`, `ifrku-wiki-skill`）
- `build-logic/` がカスタムプラグインを提供
- `ifrku-wiki-skill` のビルドにはRuby（Jekyll + Kramdown）が必要
- `./gradlew build` で全体ビルド
