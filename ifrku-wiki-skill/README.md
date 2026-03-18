# ifrku-wiki-skill

IFRKU非公式Wikiの記述規約スキルです。

## wikiwikiスクリプト

wikiwiki REST APIを操作するスクリプト群です。
環境変数 `WIKIWIKI_KEY_ID` と `WIKIWIKI_SECRET` にAPIキーのIDとシークレットを設定してから使用してください。

### scripts/wikiwiki-auth.sh

認証トークンを取得し、 `~/.wikiwiki/TOKEN` に保存します。
トークンの有効期限は24時間です。
他のスクリプトを使う前に実行してください。

```
scripts/wikiwiki-auth.sh
```

### scripts/wikiwiki-get-page.sh

指定したページの内容を取得します。
ページ名はURLエンコードされるため、日本語ページ名もそのまま渡せます。

```
scripts/wikiwiki-get-page.sh FrontPage
```

### scripts/wikiwiki-list-pages.sh

全ページの一覧を取得します。

```
scripts/wikiwiki-list-pages.sh
```
