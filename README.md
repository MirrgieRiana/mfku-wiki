# IFRKU非公式Wiki ダンプ

## 概要

これはIFRKU非公式Wikiに関するツールをまとめたリポジトリです。

## リンク

- [IFRKU非公式Wiki Wiki*](https://wikiwiki.jp/mifai2024/)
- [本リポジトリのGitHubリポジトリ](https://github.com/MirrgieRiana/mfku-wiki)
- [本リポジトリの公開ページ](https://mirrgieriana.github.io/mfku-wiki/)

## Wikiダンプ

IFRKU非公式Wikiの記事のダンプです。
静的なテキストファイルとして展開されており、AIツールから利用ができます。

### 各種ファイル

リポジトリルート上に展開されています。

- [all.wiki.json](all.wiki.json)
   - JSON形式のダンプファイルです。
   - ツールで処理しやすい代わりに、1行が長く、直接読むのには向きません。
- [all.wiki.txt](all.wiki.txt): テキスト形式のダンプファイルです。
   - プレーンテキスト形式のダンプファイルです。
   - 本来のWiki構文の塊であり、直接読むのに向いていますが、ツールで正確に処理することは困難です。

### 更新手順

1. [WikiWikiの管理ページ](https://c.wikiwiki.jp/wiki/mifai2024/advanced/dump)からダンプファイルをダウンロードする。
   - `エンコードされているページ名をディレクトリ階層つきのファイルにデコード` はオフでよい。
2. [/dumps](/dumps) に `*.tar.gz` ファイルを配置する。
3. `./gradlew dump` を実行する.

## IFRKU Wiki Skill

IFRKU非公式Wikiの記述規約を提供するClaude用のスキルです。
Wiki記事の編集やレビューの際に参照してください。

[ifrku-wiki-skill/README.md](ifrku-wiki-skill/README.md) を参照してください。

## wikiwikiスクリプト

wikiwiki REST APIを操作するスクリプト群です。
環境変数 `WIKIWIKI_KEY_ID` と `WIKIWIKI_SECRET` にAPIキーのIDとシークレットを設定してから使用してください。

### scripts/wikiwiki-auth.sh

認証トークンを取得し、 `~/.wikiwiki/TOKEN` に保存します。
トークンの有効期限は24時間です。
他のスクリプトを使う前に実行してください。

---

コマンド例

```
scripts/wikiwiki-auth.sh
```

### scripts/wikiwiki-get-page.sh

指定したページの内容を取得します。
ページ名はURLエンコードされるため、日本語ページ名もそのまま渡せます。

---

コマンド例

```
scripts/wikiwiki-get-page.sh FrontPage
```

---

出力例

```
{
    "page": "ページのタイトル",
    "source": "ページの内容",
    "timestamp": "2000-01-01T00:00:00+00:00"
}
```

### scripts/wikiwiki-list-pages.sh

全ページの一覧を取得します。

---

コマンド例

```
scripts/wikiwiki-list-pages.sh
```
