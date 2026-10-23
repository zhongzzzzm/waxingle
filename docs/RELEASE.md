# 发布流程（维护者）

本文档面向维护者；贡献者无需关心发布环节。

## 1. 更新版本号

`manifest.json`：

- `versionName`：对外版本号（`x.y.z`，语义化版本）。
- `versionCode`：整数，每次发布 +1（当前 `100` 对应 `1.0.0`）。

## 2. 更新 CHANGELOG.md

新增版本段落，日期用发布当天，只写面向用户的可见变化。

## 3. 提交并打 tag

```bash
git commit -am "chore(release): v1.0.1"
git tag v1.0.1
git push origin main --tags
```

推送 `v*` tag 后，GitHub Actions（`release.yml`）会自动：

1. 校验 tag（去掉 `v` 前缀）与 `manifest.json` 的 `versionName` 一致，不一致则失败；
2. 创建对应的 GitHub Release，附上变更说明与自动生成的 commit 列表。

## 4. 云打包并上传 APK

Release 的 Assets 里的 APK 由维护者手动上传（HBuilderX 云打包依赖 DCloud 账号，无法自动化）：

1. HBuilderX → 发行 → 原生 App-云打包（Android，自己的证书或 DCloud 公共测试证书）。
2. 打包完成后将 APK 上传到第 3 步生成的 Release 的 Assets，命名为 `wa-xing-le-v1.0.1.apk`。
3. README 的安装入口始终指向 Releases 页，无需改动。

## 5. iOS（可选）

iOS 发行需要 Apple 开发者账号与 TestFlight / App Store 审核流程，另行处理；当前代码 iOS 端未验证。
