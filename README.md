# LogManager

LogManagerは、Bukkitサーバーでのログファイルを管理しやすくするために作られたプラグインです。

## できること

- 一定以上前のログファイルを削除、特定のファイルに圧縮、または、特定のディレクトリに移動する
- 全ログファイルを展開された状態にする

## 動作条件

- Java8以上
- Bukkit系のサーバー(1.12.2を推奨。それ以外のバージョンは未確認)

## ダウンロード

任意のバージョンの`LogManager.jar`を[releases](https://github.com/kuro46/LogManager/releases)から選んでクリックしてください。
(特に理由がなければ最新のバージョンをダウンロードしてください。)

## 設定

```yaml
# SERVER_DIR/plugins/LogManager/config.yml

# すべての.log.gzファイルを.logに展開するか
decompress-all-logs: false
# 一定以上前のログファイルの扱い方の設定
log-processing:
  # 有効か
  enabled: true
  # 何日前以上のログファイルを扱うか
  process-days-before: 2
  # どうやって処理するか
  # MOVE: 特定のディレクトリに移動する
  # COMPRESS: 特定のファイルに圧縮する(zip形式)
  process-type: MOVE
  # MOVEやCOMPRESSの詳細設定
  options:
    # COMPRESSの詳細設定
    compress:
      # 圧縮先
      file-path: ./logs/logs.zip
    move:
      # 移動先
      # :yearはログファイルの作成年、:monthは月、:dayは日に変換される
      directory: "./logs/:year-:month/"
```
