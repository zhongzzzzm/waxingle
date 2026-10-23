# 贡献指南

感谢关注「娃醒了」！欢迎通过 Issue 反馈问题、提功能建议，或直接提交 PR。

## 环境要求

| 工具 | 版本 | 用途 |
| --- | --- | --- |
| [HBuilderX](https://www.dcloud.io/hbuilderx.html) | 5.26+（uni-app x 与 UTS 插件需较新版本） | 运行 / 编译 / 打包 |
| Android 手机 | 开启 USB 调试 | 真机验证（iOS 未验证，欢迎反馈） |
| Python 3 | 任意 3.x | 仅修改 tab 图标样式时需要 |

## 快速开始

1. Fork 并克隆仓库，HBuilderX → 文件 → 导入 → 从本地目录导入，选择仓库根目录。
2. 连接 Android 手机 → 运行 → 运行到手机或模拟器 → Android 标准基座。
3. 纯编译验证（无需真机）：

```bash
/Applications/HBuilderX.app/Contents/MacOS/cli project open --path "$(pwd)"
/Applications/HBuilderX.app/Contents/MacOS/cli launch app-android --project waxingle --compile true
```

注意：每晨通知功能依赖 UTS 原生插件（`uni_modules/wa-notify`），标准基座不含插件原生配置，验证该功能需自定义基座或云打包。

## 提交前自查

CI 会在 push / PR 时自动执行以下检查，建议本地先跑一遍：

```bash
# 1. tab 图标与生成脚本一致（改过图标脚本后必须重跑生成）
python3 scripts/gen_tabbar_icons.py --check

# 2. 文档 markdownlint（规则见 .markdownlint.json）
npx -y markdownlint-cli "**/*.md"
```

另外请确认：涉及页面的改动已通过 HBuilderX 编译（CLI 或 GUI 运行到基座），GitHub Actions 无法编译 uni-app x（HBuilderX 无 headless Linux CLI），编译验证只能在本地完成。

## 代码约定

- **UTS 语法**：函数必须先声明后使用（无提升）；`onLaunch` 回调需带 `OnLaunchOptions` 参数；`uni.vibrateShort` 必须传 options（`{ type: 'light' }`）。
- **夜归属规则**是核心语义（凌晨 00:00–06:59 归"昨晚"，一夜 = 前日 20:00 → 次日 07:00，实现在 `common/time.uts`），改动必须同步 `docs/设计方案.md` 并回归记录页 / 历史页的"今晚 / 昨晚"文案。
- **固定深色主题**：MVP 阶段不做浅色主题，配色集中在 `pages.json`（导航/tabBar）与页面 style 中。
- **存储结构**：`wa_records_YYYYMM` 按月分桶 + `wa_months` 月份索引（`common/store.uts`），改结构需考虑老数据兼容。
- **文档同步**：改动涉及交互流程、存储结构、通知行为时，同步更新 `docs/设计方案.md`，保持文档与代码一致。

## 提交规范

Commit message 使用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/)：

```text
feat: 支持数据导出到剪贴板
fix: 撤销按钮在 5 秒边界未置灰
docs: 补充每晨通知交互说明
chore: 更新 CI 检查项
```

一次 PR 聚焦一件事，小步提交，便于 review。

## 提交 PR

1. 从 `main` 拉出功能分支（如 `feat/export-clipboard`）。
2. 完成开发并通过上述自查。
3. 使用 PR 模板填写改动说明与自查清单，关联对应 Issue（`Closes #123`）。

## 反馈问题

- Bug 请用 Issue 模板"Bug 反馈"，附上手机型号 / Android 版本 / 复现步骤。
- 功能建议请先看 README 的 Roadmap 与 `docs/设计方案.md` 第 6 节迭代规划，避免重复提案。
