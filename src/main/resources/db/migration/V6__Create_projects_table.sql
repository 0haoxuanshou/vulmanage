-- 创建项目表
-- 用于存储项目基本信息
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,                   -- 项目唯一标识符，自增主键
    name VARCHAR(255) NOT NULL,                 -- 项目名称，不能为空
    created_user_id BIGINT NOT NULL,            -- 项目创建用户ID，外键引用users表
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP   -- 更新时间
);

-- 添加外键约束
ALTER TABLE projects 
ADD CONSTRAINT fk_projects_created_user_id 
FOREIGN KEY (created_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 创建索引以提高查询性能
CREATE INDEX idx_projects_name ON projects(name);
CREATE INDEX idx_projects_created_user_id ON projects(created_user_id);
CREATE INDEX idx_projects_created_at ON projects(created_at);

-- 添加更新时间触发器
CREATE TRIGGER update_projects_updated_at BEFORE UPDATE ON projects
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 添加表和字段注释
COMMENT ON TABLE projects IS '项目表';
COMMENT ON COLUMN projects.id IS '项目ID';
COMMENT ON COLUMN projects.name IS '项目名称';
COMMENT ON COLUMN projects.created_user_id IS '创建用户ID';
COMMENT ON COLUMN projects.created_at IS '创建时间';
COMMENT ON COLUMN projects.updated_at IS '更新时间';