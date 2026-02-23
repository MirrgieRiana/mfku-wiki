# IFRKU非公式Wiki ダンプ

## 概要

これは [IFRKU非公式Wiki Wiki*](https://wikiwiki.jp/mifai2024/) のダンプです。
静的なテキストファイルとして展開されており、AIツールから利用ができます。

## リンク

- [GitHubリポジトリ](https://github.com/MirrgieRiana/mfku-wiki)
- [公開ページ](https://mirrgieriana.github.io/mfku-wiki/)

### 各種ファイル

- [all.wiki.json](all.wiki.json)
   - JSON形式のダンプファイルです。
   - ツールで処理しやすい代わりに、1行が長く、直接読むのには向きません。
- [all.wiki.txt](all.wiki.txt): テキスト形式のダンプファイルです。
   - プレーンテキスト形式のダンプファイルです。
   - 本来のWiki構文の塊であり、直接読むのに向いていますが、ツールで正確に処理することは困難です。

## 更新手順

1. [WikiWikiの管理ページ](https://c.wikiwiki.jp/wiki/mifai2024/advanced/dump)からダンプファイルをダウンロードする。
   - `エンコードされているページ名をディレクトリ階層つきのファイルにデコード` はオフでよい。
2. [/dumps](/dumps) に `*.tar.gz` ファイルを配置する。
3. `gradlew dump` を実行する.
