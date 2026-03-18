// WikiWiki.jp 管理コンソールからダンプをダウンロードするPlaywrightスクリプト
//
// 環境変数:
//   WIKIWIKI_KEY_ID  - WikiWikiコントロールパネルのログインID（メールアドレス）
//   WIKIWIKI_SECRET  - WikiWikiコントロールパネルのパスワード
//
// 使い方:
//   npx playwright install chromium
//   node scripts/download-dump.mjs

import { chromium } from "playwright";
import { mkdirSync, appendFileSync } from "fs";
import { resolve, join } from "path";

const WIKI_NAME = "mifai2024";
const CONTROL_PANEL_URL = `https://c.wikiwiki.jp/wiki/${WIKI_NAME}/`;
const DUMP_PAGE_URL = `https://c.wikiwiki.jp/wiki/${WIKI_NAME}/advanced/dump`;
const DUMPS_DIR = resolve(process.cwd(), "dumps");
const SCREENSHOTS_DIR = resolve(process.cwd(), "screenshots");
const DOWNLOAD_TIMEOUT_MS = 120000;

const keyId = process.env.WIKIWIKI_KEY_ID;
const secret = process.env.WIKIWIKI_SECRET;

if (!keyId || !secret) {
  console.error(
    "Error: WIKIWIKI_KEY_ID and WIKIWIKI_SECRET environment variables are required."
  );
  process.exit(1);
}

mkdirSync(SCREENSHOTS_DIR, { recursive: true });
mkdirSync(DUMPS_DIR, { recursive: true });

async function takeScreenshot(page, name) {
  const path = join(SCREENSHOTS_DIR, `${name}.png`);
  await page.screenshot({ path, fullPage: true });
  console.log(`Screenshot saved: ${path}`);
}

async function main() {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({ acceptDownloads: true });
  const page = await context.newPage();

  try {
    // ログインページへアクセス
    console.log(`Navigating to ${CONTROL_PANEL_URL} ...`);
    await page.goto(CONTROL_PANEL_URL, { waitUntil: "domcontentloaded" });
    await takeScreenshot(page, "01-initial");

    // ログインフォームの入力
    console.log("Filling login form...");

    // メールアドレスフィールドを探す（複数のセレクタを試行）
    const emailField = page.locator(
      'input[type="email"], input[name="email"], input[name="name"], input[name="login"]'
    ).first();
    await emailField.waitFor({ timeout: 30000 });
    await emailField.fill(keyId);

    // パスワードフィールドを探す
    const passwordField = page.locator('input[type="password"]').first();
    await passwordField.waitFor({ timeout: 10000 });
    await passwordField.fill(secret);

    await takeScreenshot(page, "02-filled-login");

    // ログインボタンを押す
    const submitButton = page.locator(
      'button[type="submit"], input[type="submit"]'
    ).first();
    await submitButton.click();

    // ログイン完了を待つ
    await page.waitForLoadState("networkidle");
    await takeScreenshot(page, "03-after-login");

    console.log(`Current URL: ${page.url()}`);

    // ダンプページへ移動
    console.log(`Navigating to ${DUMP_PAGE_URL} ...`);
    await page.goto(DUMP_PAGE_URL, { waitUntil: "domcontentloaded" });
    await page.waitForLoadState("networkidle");
    await takeScreenshot(page, "04-dump-page");

    console.log(`Current URL: ${page.url()}`);

    // ダンプのダウンロード
    // ダウンロードリンク/ボタンを探す
    console.log("Looking for dump download link/button...");

    // ダウンロードをトリガーしてファイルを取得
    const [download] = await Promise.all([
      page.waitForEvent("download", { timeout: DOWNLOAD_TIMEOUT_MS }),
      // tar.gz のリンクまたはダウンロードボタンをクリック
      (async () => {
        // tar.gz リンクを探す
        const tarGzLink = page.locator('a[href*=".tar.gz"]').first();
        if ((await tarGzLink.count()) > 0) {
          console.log("Found tar.gz download link");
          await tarGzLink.click();
          return;
        }

        // ダウンロードボタンを探す
        const downloadButton = page
          .locator("a, button")
          .filter({ hasText: /ダウンロード|download/i })
          .first();
        if ((await downloadButton.count()) > 0) {
          console.log("Found download button");
          await downloadButton.click();
          return;
        }

        // フォーム送信ボタンを探す
        const submitBtn = page
          .locator('input[type="submit"], button[type="submit"]')
          .first();
        if ((await submitBtn.count()) > 0) {
          console.log("Found submit button on dump page");
          await submitBtn.click();
          return;
        }

        throw new Error("Could not find dump download link or button");
      })(),
    ]);

    console.log(`Download started: ${download.suggestedFilename()}`);
    await takeScreenshot(page, "05-download-started");

    // ダウンロードしたファイルを保存
    const destPath = join(DUMPS_DIR, download.suggestedFilename());
    await download.saveAs(destPath);
    console.log(`Dump saved to: ${destPath}`);

    // ダウンロードしたファイル名を出力（後続ステップで使用可能）
    if (process.env.GITHUB_OUTPUT) {
      appendFileSync(
        process.env.GITHUB_OUTPUT,
        `dump-filename=${download.suggestedFilename()}\n`
      );
    }

    await takeScreenshot(page, "06-done");
    console.log("Done!");
  } catch (error) {
    await takeScreenshot(page, "99-error");
    console.error("Error:", error.message);
    console.error(error.stack);
    process.exit(1);
  } finally {
    await browser.close();
  }
}

main();
