# 更新日志

本项目遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循语义化版本。

## 1.0.0 (2026-10-23)

### 新增

- 记录页：一键记录今晚夜醒次数，800ms 防抖防误触，记录后 5 秒内可撤销。
- 历史页：按"夜"分组展示明细，支持删除，首位高亮"今晚已醒 / 昨晚醒 X 次"胶囊。
- 夜归属规则：凌晨 00:00–06:59 的醒来归"昨晚"，一夜 = 前日 20:00 → 次日 07:00。
- 每晨通知：每天 09:00（±10 分钟系统窗口）本地通知"昨晚醒了 N 次"，记录页齿轮开关；实现为 UTS 插件 `uni_modules/wa-notify`（AlarmManager.setWindow + 静态 receiver，触发后自排次日），强杀 App 后通知暂停、下次打开自愈。
- App 图标（星月）与由脚本生成的 tab 图标（`scripts/gen_tabbar_icons.py`）。
- 纯本地存储：按月分桶 `wa_records_YYYYMM` + 月份索引 `wa_months`，无网络请求。
