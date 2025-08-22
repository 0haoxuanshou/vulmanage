# Changelog

## 2025-08-22

### 🚀 主要变动 (Major Changes)

1. **Dashboard统计功能增强** - 添加漏洞、项目、任务、资产数量统计展示
2. **项目详情页布局优化** - 重构页面布局，统一展示基本信息和统计数据
3. **项目详情页资产列表修复** - 修复资产列表在页面初始化时的显示问题
4. **模板文件路径处理优化** - 改进Docker环境下的模板文件路径处理机制
5. **跨平台兼容性改进** - 使用系统临时目录替代相对路径，提升环境兼容性
6. **API调用逻辑优化** - 优化前端页面的后端API调用时机和错误处理
7. **响应式布局实现** - 项目详情页实现响应式网格布局设计
8. **数据加载状态管理** - 添加统计数据的加载状态和错误处理机制
9. **视觉效果提升** - 优化Dashboard和项目详情页的视觉展示效果
10. **并发API调用实现** - Dashboard统计数据采用并发API调用提升性能

### 🐛 已知问题修复 (Bug Fixes)

- 修复Docker环境中导出报告时"Cannot create directory 'templates'"错误
- 修复项目详情页资产列表初始化显示问题
- 修复模板文件路径在不同环境下的兼容性问题

---

## 2025-08-17

### 🚀 新增功能 (New Features)

#### 漏洞模板管理系统
- **模板管理功能**: 新增完整的漏洞报告模板管理系统
  - 添加 `VulnerabilityTemplate` 模型和相关数据库表
  - 实现模板的增删改查功能
  - 支持模板文件上传和存储
  - 提供默认模板机制

- **前端模板管理界面**: 新增 `VulnerabilityTemplateList.vue` 页面
  - 模板列表展示和管理
  - 模板文件上传功能
  - 模板编辑和删除操作

#### 报告导出功能增强
- **统一报告导出接口**: 重构报告生成系统
  - 新增 `VulnerabilityReportParam` 统一参数类
  - 实现单个和批量报告的统一处理
  - 支持自定义模板选择

- **模板选择功能**: 在漏洞列表页面添加模板选择
  - 批量导出时支持模板选择弹窗
  - 单个报告生成时支持模板选择
  - 提供默认模板回退机制

#### 文档模板处理系统
- **DOCX模板引擎**: 新增专业的Word文档处理能力
  - 实现 `TemplateDocxService` 模板处理服务
  - 支持HTML内容渲染到Word文档
  - 提供 `HtmlRenderPolicy` 自定义渲染策略

#### 系统信息管理
- **系统信息API**: 新增系统信息查询功能
  - 添加 `SystemInfoController` 和相关服务
  - 提供系统状态和配置信息查询

#### 用户令牌管理
- **Token管理系统**: 增强用户认证机制
  - 新增 `UserToken` 模型和数据库表
  - 实现Token的持久化存储
  - 提供Token管理相关API

### 🔧 功能优化 (Improvements)

#### 模板文件处理优化
- **本地缓存机制**: 优化模板文件访问性能
  - 实现模板文件本地缓存存储
  - 添加文件存在性检查逻辑
  - 自动创建本地存储目录结构
  - 提供完善的异常处理机制

#### API接口重构
- **统一报告接口**: 简化前端API调用
  - 将多个报告生成接口统一为 `generateUnifiedReport`
  - 移除冗余的 `generateReport` 和 `generateBatchReport` 方法
  - 优化参数传递和错误处理

#### 前端用户体验改进
- **模板选择交互**: 提升用户操作体验
  - 添加友好的模板选择弹窗
  - 提供默认模板提示信息
  - 优化批量操作流程

### 🗑️ 代码清理 (Code Cleanup)

#### 移除废弃代码
- **清理无用接口**: 移除不再使用的报告生成方法
  - 删除 `VulnerabilityBatchReportParam` 类
  - 移除 `generateVulnerabilityReport` 和 `generateBatchVulnerabilityReport` 方法
  - 清理前端废弃的API调用

### 🔄 数据库变更 (Database Changes)

#### 新增数据表
- **V3__Create_user_tokens_table.sql**: 用户令牌管理表
- **V5__Create_vulnerability_template_table.sql**: 漏洞模板管理表

#### 模板文件
- **vulnerability_batch_report_template.docx**: 批量报告默认模板
- **vulnerability_report_template.docx**: 单个报告默认模板

### 🏗️ 基础设施改进 (Infrastructure)

#### Docker配置优化
- **环境配置**: 添加 `.env` 环境变量配置文件
- **Nginx配置**: 优化反向代理配置
- **Docker Compose**: 更新服务编排配置

#### 项目配置
- **Web配置**: 新增 `WebConfig` 跨域和静态资源配置
- **安全配置**: 更新 `SecurityConfig` 认证和授权规则
- **依赖管理**: 更新 `pom.xml` 项目依赖

### 🎯 技术亮点 (Technical Highlights)

1. **统一接口设计**: 通过统一的报告生成接口简化了系统架构
2. **智能缓存机制**: 模板文件的本地缓存显著提升了性能
3. **用户体验优化**: 直观的模板选择界面提升了操作便利性
4. **代码质量提升**: 移除冗余代码，提高了代码可维护性
5. **扩展性增强**: 模块化的模板管理系统便于后续功能扩展

---

**注意**: 此版本包含数据库结构变更，部署前请确保运行相应的数据库迁移脚本。