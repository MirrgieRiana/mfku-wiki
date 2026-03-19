# ノウハウ

サブプロジェクトに紐づいた永続的なメモリです。
可能な限り綺麗に記述しなければなりません。

## プロジェクト概要

IFRKU非公式Wiki（wikiwiki.jp/mifai2024）の運用支援リポジトリです。
IFR25KUはMirageFairy2024のKUフォーク（Kakera Unofficial）で、Minecraft MODです。
`wiki-dump/` にWikiダンプのビルドスクリプトと出力ファイルがあります。
`ifrku-wiki-skill/` にWiki記述規約スキルがあります。

## 資料

### 更新履歴

CHANGELOGは下記URLから最新のものを取得します。
`https://raw.githubusercontent.com/MirrgieRiana/IFR25KU/refs/heads/main/pages/CHANGELOG.md`

### WikiWiki

`scripts/wikiwiki-*.sh` でWIKIWIKI APIを操作します（wiki ID: mifai2024）。
`scripts/wikiwiki-auth.sh` で認証を行い、トークンは `~/.wikiwiki/TOKEN` に保存されます。
ローカルCLIの場合は `~/.ssh/WIKIWIKI_KEY_ID` と `~/.ssh/WIKIWIKI_SECRET` を環境変数に読み込んでからauthします。
GitHub Copilotの場合は環境変数に設定済みです。

### ソースコード

- IFR25KU本体: GitHubリポジトリ `MirrgieRiana/IFR25KU`
- MirageFairy2019: `../MirageFairy2019`
- MirageFairy2023: `../MirageFairy2023`
- MirageFairy2024: `../MirageFairy2024` （中身がIFR25KUの場合あり）

### ゲームデータ

- 英語名・日本語名・ポエム: `common/src/generated/resources/assets/miragefairy2024/lang/{en_us,ja_jp}.json`
- アイテム定義・Tier・燃料値: `common/src/main/kotlin/miragefairy2024/mod/materials/MaterialsModule.kt`
- ブロック定義・Tier: `common/src/main/kotlin/miragefairy2024/mod/materials/BlockMaterialsModule.kt`

いずれもIFR25KUのリポジトリから取得します。

## 教訓

### Wiki反映状況の確認は全文照合で行う

「反映済み」の断定には、`scripts/wikiwiki-get-page.sh` でWikiページの全文を取得し、CHANGELOGの文言と逐一照合しなければなりません。
`grep -c` の件数では判断できません。件数が一致しても内容が不正確な場合があります。
ページ一覧取得時に存在しなかったページがその後作成されていることもあるため、都度再取得します。

### ゲームデータは推測せずソースコードから取得する

正式名称・ポエム・Tier・燃料値などのゲームデータは、推測や■で誤魔化さず、必ずソースコードから取得します。
燃料値は `fuelValue` から算出します（200 ticks = 精錬1回分）。
Tierは `PoemList(N)` から取得します。 `PoemList(null)` はTier表記なしです。

### 英語ポエムの日本語訳は自分で直訳する

英語版ポエムの日本語訳は、自分で直訳を書かなければなりません。
langファイル（en_us.json / ja_jp.json）はそれぞれ独立した言語テキストです。
英語ポエムと日本語ポエムは対訳ではなく別の内容であり、訳文を提供するエントリーは存在しません。

### 旧作への登場はローカルリポジトリで確認する

新規アイテムが旧作に登場する場合、記事に旧作への言及が必須です。
ローカルリポジトリのlangファイルを直接Grepするのが確実です。GitHub Code Searchは不完全な結果を返すことがあります。
atwiki（w.atwiki.jp/miragefairy2019/）は外部から403でアクセスできないため、ソースコードで代用します。

### 編集提案HTMLの作成手順

diffの生成には手動ではなくdiffコマンドを使用します。
新規ページの提案には日本語名・英語名・日本語ポエム・英語ポエム・英語ポエム直訳・Tier・旧作言及・元ネタ解説をすべて含めます。
Wikiに完全に反映済みの項目は編集提案カードを削除します。
一部のみ反映済みの場合は未記載分だけ差分にします。
