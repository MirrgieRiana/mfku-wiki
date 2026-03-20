# WikiWiki API認証

認証情報ファイルの保管場所:
- `~/.ssh/WIKIWIKI_KEY_ID`
- `~/.ssh/WIKIWIKI_SECRET`

認証の実行例:

```
WIKIWIKI_KEY_ID=$(cat ~/.ssh/WIKIWIKI_KEY_ID) WIKIWIKI_SECRET=$(cat ~/.ssh/WIKIWIKI_SECRET) scripts/wikiwiki-auth.sh
```
