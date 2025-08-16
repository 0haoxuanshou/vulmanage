# 安全漏洞管理系统需求文档

## 1. 项目概述

### 1.1 项目背景
开发一个用于存储和管理安全漏洞信息的系统，帮助安全团队有效地跟踪、管理和分析安全漏洞。

### 1.2 项目目标
构建一个功能完善的安全漏洞管理平台，支持漏洞信息的录入、编辑、查询和分析，提高安全团队的工作效率。

## 2. 功能需求

### 2.1 用户管理

#### 2.1.1 用户登录
- 支持单一账号登录（账号密码存储在配置文件中）
- 登录失败时提供明确的错误提示
- 实现登录状态保持（Session/Token）
- 登录成功后重定向到系统主页

#### 2.1.2 密码管理
- 用户能够修改登录密码
- 修改密码时需要验证原密码
- 新密码需符合安全策略（长度、复杂度等）
- 密码修改成功后提示用户

### 2.2 漏洞管理

#### 2.2.1 漏洞录入
- 支持添加新的安全漏洞记录
- 漏洞信息应包括：
  - 漏洞编号
  - 漏洞名称
  - 漏洞URL（支持多个URL）
  - 漏洞类型
  - 漏洞等级
  - 影响范围
  - 发现日期
  - 漏洞概述
  - 复现步骤
  - 修复建议
  - 漏洞附件
  - 当前状态（如：未修复、修复中、已修复、已验证）

#### 2.2.2 漏洞编辑
- 允许用户编辑已有漏洞记录的所有字段
- 记录漏洞信息的修改历史
- 支持添加漏洞修复进展更新

#### 2.2.3 漏洞查询与筛选
- 支持按多种条件筛选漏洞：
  - 漏洞编号
  - 漏洞名称（模糊查询）
  - 漏洞URL（模糊查询）
  - 漏洞类型
  - 漏洞等级
  - 发现日期范围
  - 当前状态
- 支持组合条件查询
- 查询结果支持分页显示
- 支持查询结果排序（如按日期、严重程度等）

#### 2.2.4 漏洞详情查看
- 提供漏洞详细信息的查看界面
- 显示漏洞的完整信息和修改历史
- 支持附件的上传、下载和预览

## 3. 非功能需求

### 3.1 性能需求
- 系统响应时间：页面加载时间不超过3秒
- 支持多用户并发访问
- 数据库查询优化，确保复杂查询响应迅速

### 3.2 安全需求
- 密码使用强哈希算法存储在配置文件中
- 防止SQL注入、XSS等常见Web安全漏洞
- 敏感操作需要二次确认
- 实现操作日志记录，便于审计

### 3.3 可用性需求
- 界面设计简洁直观，易于使用
- 提供必要的用户操作指引
- 关键操作提供确认机制，防止误操作
- 支持响应式设计，适配不同设备

## 4. 技术栈

### 4.1 后端技术
- 框架：Spring Boot 3.x
- 构建工具：Maven
- 数据访问：Spring Data JPA/MyBatis
- 安全框架：Spring Security

### 4.2 前端技术
- 框架：Vue.js 3.x
- UI组件库：Element Plus/Vuetify
- 状态管理：Vuex/Pinia
- HTTP客户端：Axios

### 4.3 数据库
- PostgreSQL

## 5. 系统架构

### 5.1 总体架构
- 采用前后端分离架构
- RESTful API设计
- 分层架构：控制层、服务层、数据访问层

### 5.2 数据模型（初步）

#### 用户配置（application.properties/yml）
- username：用户名
- password：密码（加密存储）

#### 漏洞表（vulnerabilities）
- id：漏洞ID
- vulnerability_number：漏洞编号
- name：漏洞名称
- type：漏洞类型
- severity：漏洞等级
- scope：影响范围
- discovery_date：发现日期
- summary：漏洞概述
- reproduction_steps：复现步骤
- fix_suggestion：修复建议
- status：当前状态
- created_at：创建时间
- updated_at：更新时间

#### 漏洞URL表（vulnerability_urls）
- id：URL ID
- vulnerability_id：漏洞ID
- url：URL地址

#### 漏洞附件表（vulnerability_attachments）
- id：附件ID
- vulnerability_id：漏洞ID
- file_name：文件名
- file_path：文件路径
- file_size：文件大小
- upload_time：上传时间

#### 漏洞历史表（vulnerability_history）
- id：历史记录ID
- vulnerability_id：漏洞ID
- field_name：修改字段
- old_value：旧值
- new_value：新值
- modified_at：修改时间

## 6. 接口设计（初步）

### 6.1 用户接口
- POST /api/auth/login：用户登录
- POST /api/auth/logout：用户登出
- PUT /api/auth/password：修改密码

### 6.2 漏洞接口
- GET /api/vulnerabilities：获取漏洞列表（支持筛选）
- GET /api/vulnerabilities/{id}：获取漏洞详情
- POST /api/vulnerabilities：创建新漏洞
- PUT /api/vulnerabilities/{id}：更新漏洞信息
- DELETE /api/vulnerabilities/{id}：删除漏洞
- GET /api/vulnerabilities/{id}/history：获取漏洞修改历史

### 6.3 附件接口
- POST /api/vulnerabilities/{id}/attachments：上传附件
- GET /api/vulnerabilities/{id}/attachments：获取附件列表
- GET /api/attachments/{id}：下载附件
- DELETE /api/attachments/{id}：删除附件

## 7. 开发计划

### 7.1 阶段划分
1. 需求分析与设计（1周）
2. 数据库设计与搭建（1周）
3. 后端开发（3周）
4. 前端开发（3周）
5. 集成测试（1周）
6. 部署上线（1周）

### 7.2 里程碑
- 完成数据库设计
- 完成用户认证模块
- 完成漏洞管理基础功能
- 完成漏洞筛选与查询功能
- 系统集成测试通过
- 系统上线

## 8. 风险评估

### 8.1 潜在风险
- 需求变更可能影响开发进度
- 技术栈选型可能存在兼容性问题
- 数据安全性保障挑战

### 8.2 应对策略
- 采用敏捷开发方法，灵活应对需求变更
- 提前进行技术验证，确保技术栈兼容性
- 加强安全设计，实施严格的数据访问控制