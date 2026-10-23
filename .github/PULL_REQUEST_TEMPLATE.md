## 改动说明

<!-- 简述改了什么、为什么这么改 -->

## 改动类型

- [ ] feat 新功能
- [ ] fix 缺陷修复
- [ ] docs 文档
- [ ] style 样式与图标
- [ ] chore 构建 / CI / 杂项

## 关联 Issue

<!-- 例如：Closes #12 -->

## 自查清单

- [ ] 已通过 HBuilderX 编译验证（`cli launch app-android --project waxingle --compile true`，或 GUI 运行到基座）
- [ ] 本地通过 CI 检查：`python3 scripts/gen_tabbar_icons.py --check` 与 `npx -y markdownlint-cli "**/*.md"`
- [ ] 涉及交互 / 流程 / 存储结构 / 通知行为时，已同步 docs/设计方案.md
- [ ] 面向用户的变化已更新 CHANGELOG.md
- [ ] 修改过 tab 图标样式时，已重跑 `python3 scripts/gen_tabbar_icons.py`
