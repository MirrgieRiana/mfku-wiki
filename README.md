# mfku-wiki

## 概要

これは [MFKU非公式Wiki Wiki*](https://wikiwiki.jp/mifai2024/) のダンプです。
静的なテキストファイルとして展開されており、AIツールから利用ができます。

## ページ

[https://mirrgieriana.github.io/mfku-wiki/wiki-dump_out.txt](https://mirrgieriana.github.io/mfku-wiki/wiki-dump_out.txt)

## 更新手順

1. WikiWikiの管理ページからダンプファイルをダウンロードする。
    - `エンコードされているページ名をディレクトリ階層つきのファイルにデコード` はオフでよい。 
2. 本リポジトリのルートに `*.tar.gz` ファイルを配置する。
3. `gradlew dump` を実行する.
