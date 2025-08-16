-- 创建漏洞表
-- 用于存储安全漏洞的基本信息和详细描述
CREATE TABLE vulnerabilities (
    id BIGSERIAL PRIMARY KEY,                    -- 漏洞唯一标识符，自增主键
    vulnerability_number VARCHAR(255) UNIQUE,   -- 漏洞编号，业务唯一标识，如CVE-2023-0001
    name VARCHAR(255),                          -- 漏洞名称，简短描述漏洞类型
    type VARCHAR(255),                          -- 漏洞类型，如SQL注入、XSS、CSRF等
    severity VARCHAR(255),                      -- 漏洞严重程度，如严重、高危、中危、低危
    scope VARCHAR(255),                         -- 影响范围，描述漏洞影响的系统或组件
    discovery_date DATE,                        -- 漏洞发现日期
    summary TEXT,                               -- 漏洞概述，详细描述漏洞的基本情况
    reproduction_steps TEXT,                    -- 漏洞复现步骤，详细说明如何重现该漏洞
    fix_suggestion TEXT,                        -- 修复建议，提供漏洞修复的具体方案
    status VARCHAR(255),                        -- 漏洞状态，如未修复、修复中、已修复、已验证
    created_at TIMESTAMP,                       -- 记录创建时间
    updated_at TIMESTAMP                        -- 记录最后更新时间
);

-- 创建漏洞URL关联表
-- 用于存储与漏洞相关的URL地址，支持一个漏洞对应多个URL
CREATE TABLE vulnerability_urls (
    vulnerability_id BIGINT NOT NULL,           -- 关联的漏洞ID，外键引用vulnerabilities表
    url VARCHAR(2048),                          -- 漏洞相关的URL地址，最大长度2048字符
    FOREIGN KEY (vulnerability_id) REFERENCES vulnerabilities(id) ON DELETE CASCADE  -- 外键约束，级联删除
);

-- 创建漏洞历史记录表
-- 用于记录漏洞信息的修改历史，实现审计追踪功能
CREATE TABLE vulnerability_history (
    id BIGSERIAL PRIMARY KEY,                   -- 历史记录唯一标识符，自增主键
    vulnerability_id BIGINT,                    -- 关联的漏洞ID，引用vulnerabilities表
    field_name VARCHAR(255),                    -- 被修改的字段名称
    old_value TEXT,                             -- 修改前的字段值
    new_value TEXT,                             -- 修改后的字段值
    modified_at TIMESTAMP                       -- 修改时间戳
);

-- 创建索引以提高查询性能
CREATE INDEX idx_vulnerabilities_number ON vulnerabilities(vulnerability_number);
CREATE INDEX idx_vulnerabilities_type ON vulnerabilities(type);
CREATE INDEX idx_vulnerabilities_severity ON vulnerabilities(severity);
CREATE INDEX idx_vulnerabilities_status ON vulnerabilities(status);
CREATE INDEX idx_vulnerabilities_discovery_date ON vulnerabilities(discovery_date);
CREATE INDEX idx_vulnerability_history_vulnerability_id ON vulnerability_history(vulnerability_id);
CREATE INDEX idx_vulnerability_urls_vulnerability_id ON vulnerability_urls(vulnerability_id);