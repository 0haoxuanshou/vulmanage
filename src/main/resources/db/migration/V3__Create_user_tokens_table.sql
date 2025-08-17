-- V3__Create_user_tokens_table.sql
-- 创建用户token表用于持久化存储登录token

CREATE TABLE user_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    token_string TEXT NOT NULL,
    token_type VARCHAR(20) NOT NULL DEFAULT 'Bearer',
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT
);

-- 添加外键约束
ALTER TABLE user_tokens 
ADD CONSTRAINT fk_user_tokens_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加唯一约束，确保每个用户只有一个活跃token
CREATE UNIQUE INDEX idx_user_tokens_user_active 
ON user_tokens (user_id, is_active) 
WHERE is_active = TRUE;

-- 添加索引以提高查询性能
CREATE INDEX idx_user_tokens_token_string ON user_tokens (token_string);
CREATE INDEX idx_user_tokens_username ON user_tokens (username);
CREATE INDEX idx_user_tokens_expires_at ON user_tokens (expires_at);
CREATE INDEX idx_user_tokens_is_active ON user_tokens (is_active);
CREATE INDEX idx_user_tokens_created_at ON user_tokens (created_at);

-- 创建触发器自动更新updated_at字段
CREATE OR REPLACE FUNCTION update_user_tokens_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_tokens_updated_at 
    BEFORE UPDATE ON user_tokens 
    FOR EACH ROW 
    EXECUTE FUNCTION update_user_tokens_updated_at_column();

-- 添加注释
COMMENT ON TABLE user_tokens IS '用户登录token表';
COMMENT ON COLUMN user_tokens.id IS '主键ID';
COMMENT ON COLUMN user_tokens.user_id IS '用户ID，关联users表';
COMMENT ON COLUMN user_tokens.username IS '用户名';
COMMENT ON COLUMN user_tokens.token_string IS 'JWT token字符串';
COMMENT ON COLUMN user_tokens.token_type IS 'token类型，默认Bearer';
COMMENT ON COLUMN user_tokens.expires_at IS 'token过期时间';
COMMENT ON COLUMN user_tokens.is_active IS '是否激活状态';
COMMENT ON COLUMN user_tokens.created_at IS '创建时间';
COMMENT ON COLUMN user_tokens.updated_at IS '更新时间';
COMMENT ON COLUMN user_tokens.last_used_at IS '最后使用时间';
COMMENT ON COLUMN user_tokens.ip_address IS '客户端IP地址';
COMMENT ON COLUMN user_tokens.user_agent IS '客户端用户代理信息';