-- 创建资产表
-- 用于存储项目下的资产信息（IP地址或域名）
CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,                   -- 资产唯一标识符，自增主键
    name VARCHAR(255) NOT NULL,                 -- 资产名称，不能为空
    type VARCHAR(50) NOT NULL,                  -- 资产类型：IP（IP地址）, DOMAIN（域名）
    value VARCHAR(255) NOT NULL,                -- 资产值（IP地址或域名）
    description TEXT,                           -- 资产描述
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- 资产状态：ACTIVE（活跃）, INACTIVE（非活跃）
    project_id BIGINT NOT NULL,                -- 所属项目ID，外键引用projects表
    created_user_id BIGINT NOT NULL,           -- 资产创建用户ID，外键引用users表
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- 添加外键约束
ALTER TABLE assets 
ADD CONSTRAINT fk_assets_project_id 
FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE assets 
ADD CONSTRAINT fk_assets_created_user_id 
FOREIGN KEY (created_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加检查约束
ALTER TABLE assets 
ADD CONSTRAINT chk_assets_type 
CHECK (type IN ('IP', 'DOMAIN'));

ALTER TABLE assets 
ADD CONSTRAINT chk_assets_status 
CHECK (status IN ('ACTIVE', 'INACTIVE'));

-- 添加唯一约束（同一项目下的资产值不能重复）
ALTER TABLE assets 
ADD CONSTRAINT uk_assets_project_value 
UNIQUE (project_id, value);

-- 创建索引以提高查询性能
CREATE INDEX idx_assets_name ON assets(name);
CREATE INDEX idx_assets_type ON assets(type);
CREATE INDEX idx_assets_value ON assets(value);
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_assets_project_id ON assets(project_id);
CREATE INDEX idx_assets_created_user_id ON assets(created_user_id);
CREATE INDEX idx_assets_created_at ON assets(created_at);

-- 添加更新时间触发器
CREATE TRIGGER update_assets_updated_at BEFORE UPDATE ON assets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 添加表和字段注释
COMMENT ON TABLE assets IS '资产表';
COMMENT ON COLUMN assets.id IS '资产ID';
COMMENT ON COLUMN assets.name IS '资产名称';
COMMENT ON COLUMN assets.type IS '资产类型';
COMMENT ON COLUMN assets.value IS '资产值（IP地址或域名）';
COMMENT ON COLUMN assets.description IS '资产描述';
COMMENT ON COLUMN assets.status IS '资产状态';
COMMENT ON COLUMN assets.project_id IS '所属项目ID';
COMMENT ON COLUMN assets.created_user_id IS '创建用户ID';
COMMENT ON COLUMN assets.created_at IS '创建时间';
COMMENT ON COLUMN assets.updated_at IS '更新时间';