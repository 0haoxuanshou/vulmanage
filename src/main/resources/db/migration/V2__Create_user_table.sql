-- 创建角色表
-- 用于存储系统角色信息
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,                   -- 角色唯一标识符，自增主键
    name VARCHAR(50) UNIQUE NOT NULL,           -- 角色名称，如ADMIN、USER等
    description VARCHAR(255),                   -- 角色描述
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- 创建用户表
-- 用于存储用户登录和基本信息
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,                   -- 用户唯一标识符，自增主键
    username VARCHAR(50) UNIQUE NOT NULL,       -- 用户名，唯一
    password VARCHAR(255) NOT NULL,             -- 密码，BCrypt加密存储
    email VARCHAR(100) UNIQUE,                  -- 邮箱地址，唯一
    full_name VARCHAR(100),                     -- 用户全名
    enabled BOOLEAN DEFAULT TRUE,               -- 账户是否启用
    account_non_expired BOOLEAN DEFAULT TRUE,   -- 账户是否未过期
    account_non_locked BOOLEAN DEFAULT TRUE,    -- 账户是否未锁定
    credentials_non_expired BOOLEAN DEFAULT TRUE, -- 凭证是否未过期
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- 创建用户角色关联表
-- 用于实现用户和角色的多对多关系
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,                    -- 用户ID，外键引用users表
    role_id BIGINT NOT NULL,                    -- 角色ID，外键引用roles表
    PRIMARY KEY (user_id, role_id),            -- 复合主键
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 创建系统配置表
-- 用于存储系统配置信息，如是否已初始化等
CREATE TABLE system_config (
    id BIGSERIAL PRIMARY KEY,                   -- 配置唯一标识符，自增主键
    config_key VARCHAR(100) UNIQUE NOT NULL,    -- 配置键，唯一
    config_value VARCHAR(500),                  -- 配置值
    description VARCHAR(255),                   -- 配置描述
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- 插入默认角色数据
INSERT INTO roles (name, description) VALUES 
('ADMIN', '管理员角色，拥有所有权限'),
('USER', '普通用户角色，拥有基本权限');

-- 插入系统初始化配置
INSERT INTO system_config (config_key, config_value, description) VALUES 
('system.initialized', 'false', '系统是否已完成初始化设置');

-- 创建索引以提高查询性能
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_system_config_key ON system_config(config_key);

-- 添加更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为用户表添加更新时间触发器
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 为角色表添加更新时间触发器
CREATE TRIGGER update_roles_updated_at BEFORE UPDATE ON roles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 为系统配置表添加更新时间触发器
CREATE TRIGGER update_system_config_updated_at BEFORE UPDATE ON system_config
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();