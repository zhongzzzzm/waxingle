# 娃醒了 · Wa Xing Le

> 半夜半梦半醒，点亮屏幕、点一下就好 —— 一键记录宝宝夜里醒来的次数。
>
> One tap to log your baby's night wakings. Built with uni-app x (UTS / .uvue → native Android).

[![CI](https://github.com/zhongziming/waxingle/actions/workflows/ci.yml/badge.svg)](https://github.com/zhongziming/waxingle/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## 特性

- **一键记录**：深夜场景为唯一核心，大按钮直接点；800ms 防抖防手抖误触，记录后 5 秒内可撤销。
- **历史回顾**：按"夜"分组展示，历史页首位高亮"今晚已醒 / 昨晚醒 X 次"，明细与删除集中在历史页。
- **每晨通知**：每天 09:00（±10 分钟系统窗口）本地通知"昨晚醒了 N 次"，可在记录页齿轮关闭。
- **夜归属规则**：凌晨 00:00–06:59 的醒来归"昨晚"，一夜 = 前日 20:00 → 次日 07:00。
- **深色主题**：MVP 固定深色，为深夜暗光环境设计。

## 隐私

- 所有记录仅保存在手机本地（按月分桶的本地存储），无后端、无账号体系。
- 不联网、不申请网络与存储权限；唯一申请的权限是 Android 通知（`POST_NOTIFICATIONS`，用于每晨通知，拒绝或关闭通知后仅影响提醒，不影响记录）。

## 安装

前往 [Releases](https://github.com/zhongziming/waxingle/releases) 页下载维护者云打包的 APK（Android）。如暂无所需版本，可按下节从源码自行编译。

## 从源码运行

需要 [HBuilderX](https://www.dcloud.io/hbuilderx.html) 5.26+（uni-app x 与 UTS 插件需要较新版本）。

**图形界面：**

1. 文件 → 导入 → 从本地目录导入，选择本仓库根目录。
2. 连接已开启 USB 调试的 Android 手机 → 运行 → 运行到手机或模拟器 → Android 标准基座。
3. 每晨通知依赖 UTS 原生插件，需自定义基座或云打包才能真机验证（标准基座不含插件原生配置）。

**命令行（编译验证，无需真机）：**

```bash
# macOS，Windows 为 HBuilderX 安装目录下的 cli.exe，命令相同
/Applications/HBuilderX.app/Contents/MacOS/cli project open --path "$(pwd)"
/Applications/HBuilderX.app/Contents/MacOS/cli launch app-android --project waxingle --compile true
```

**打包发行：** HBuilderX → 发行 → 原生 App-云打包。打包前建议在 DCloud 开发者中心生成自己的 appid 与包名（仓库自带的 appid 仅供开发调试）；完整发布流程见 [docs/RELEASE.md](docs/RELEASE.md)。

## 工程结构

```text
waxingle/
├── main.uts                  # 入口
├── App.uvue
├── pages.json                # 页面与 tabBar 配置
├── manifest.json
├── uni.scss
├── common/
│   ├── time.uts              # 时间/夜归属规则工具
│   └── store.uts             # 本地存储（按月分桶）
├── pages/
│   ├── record/record.uvue    # Tab1 记录：一键记录 + 今晚统计 + 撤销
│   └── history/history.uvue  # Tab2 历史：按夜分组 + 展开 + 汇总 + 删除
├── static/tabbar/            # tab 图标（81x81，由脚本生成）
├── static/appicon/           # App 图标（A「星月」，Android 尺寸已配 manifest）
├── uni_modules/wa-notify/    # 每晨通知 UTS 插件（AlarmManager + 静态 receiver，触发后自排次日）
├── scripts/gen_tabbar_icons.py   # tab 图标生成脚本（纯 Python 标准库）
├── docs/                     # 设计方案、发布流程
└── .github/                  # CI/Release 工作流、Issue/PR 模板
```

## 实现要点

- **存储**：纯本地，`wa_records_YYYYMM` 按月分桶存 JSON 字符串，`wa_months` 存月份索引（`common/store.uts`）。
- **夜归属**：`common/time.uts` 的 `nightDate` 实现，改动需同步设计文档并回归"昨晚/今晚"文案。
- **tab 图标**：`python3 scripts/gen_tabbar_icons.py` 生成；CI 会用 `--check` 校验仓库图标与脚本一致。
- **每晨通知**：`uni_modules/wa-notify` —— AlarmManager.setWindow 09:00±10min + 静态 receiver 触发后自排次日；强杀 App 后通知暂停、下次打开自愈。
- **UTS 注意事项**（实际踩过的坑）：函数必须先声明后使用（无提升）；`onLaunch` 回调需带 `OnLaunchOptions` 参数；`uni.vibrateShort` 必须传 options（`{ type: 'light' }`）。

## Roadmap

- 浅色主题（跟随系统）
- 夜间界点（07:00 / 20:00）可设置
- 数据导出（剪贴板 / 文件）
- 周趋势小图

完整迭代规划与需求背景见 [docs/设计方案.md](docs/设计方案.md)。

## 参与贡献

欢迎 Issue 与 PR：[CONTRIBUTING.md](CONTRIBUTING.md)。

## 许可证

[MIT](LICENSE)
