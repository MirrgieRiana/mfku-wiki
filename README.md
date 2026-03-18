# IFRKU非公式Wiki ダンプ

## 概要

これは [IFRKU非公式Wiki Wiki*](https://wikiwiki.jp/mifai2024/) のダンプです。
静的なテキストファイルとして展開されており、AIツールから利用ができます。

## リンク

- [GitHubリポジトリ](https://github.com/MirrgieRiana/mfku-wiki)
- [公開ページ](https://mirrgieriana.github.io/mfku-wiki/)

### ビルド生成物

ビルドすると `build/` ディレクトリ内に以下が生成されます：

- `all.wiki.json`: JSON形式のダンプファイル
- `all.wiki.txt`: テキスト形式のダンプファイル
- `wiki/`: ページ毎のテキストファイル

### APIスクリプト

- `scripts/get-page.sh <ページ名>`: WikiWiki APIから指定ページの内容を取得
- `scripts/list-pages.sh`: WikiWiki APIからページ一覧を取得

## 更新手順

1. [WikiWikiの管理ページ](https://c.wikiwiki.jp/wiki/mifai2024/advanced/dump)からダンプファイルをダウンロードする。
   - `エンコードされているページ名をディレクトリ階層つきのファイルにデコード` はオフでよい。
2. [/dumps](/dumps) に `*.tar.gz` ファイルを配置する。
3. `gradlew dump` を実行する.
