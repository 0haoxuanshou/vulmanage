-- 创建任务表
-- 用于存储项目下的任务信息
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,                   -- 任务唯一标识符，自增主键
    name VARCHAR(255) NOT NULL,                 -- 任务名称，不能为空
    type VARCHAR(50) NOT NULL,                  -- 任务类型：SCAN_TARGET（扫描任务目标）, OTHER（其他）
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- 任务状态：PENDING（待执行）, RUNNING（执行中）, COMPLETED（已完成）, FAILED（失败）
    description TEXT,                           -- 任务描述
    project_id BIGINT NOT NULL,                -- 所属项目ID，外键引用projects表
    created_user_id BIGINT NOT NULL,           -- 任务创建用户ID，外键引用users表
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 更新时间
    started_at TIMESTAMP,                      -- 开始执行时间
    completed_at TIMESTAMP                     -- 完成时间
);

-- 添加外键约束
ALTER TABLE tasks 
ADD CONSTRAINT fk_tasks_project_id 
FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE tasks 
ADD CONSTRAINT fk_tasks_created_user_id 
FOREIGN KEY (created_user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加检查约束
ALTER TABLE tasks 
ADD CONSTRAINT chk_tasks_type 
CHECK (type IN ('SCAN_TARGET', 'OTHER'));

ALTER TABLE tasks 
ADD CONSTRAINT chk_tasks_status 
CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED'));

-- 创建索引以提高查询性能
CREATE INDEX idx_tasks_name ON tasks(name);
CREATE INDEX idx_tasks_type ON tasks(type);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_created_user_id ON tasks(created_user_id);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);

-- 添加更新时间触发器
CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 添加表和字段注释
COMMENT ON TABLE tasks IS '任务表';
COMMENT ON COLUMN tasks.id IS '任务ID';
COMMENT ON COLUMN tasks.name IS '任务名称';
COMMENT ON COLUMN tasks.type IS '任务类型';
COMMENT ON COLUMN tasks.status IS '任务状态';
COMMENT ON COLUMN tasks.description IS '任务描述';
COMMENT ON COLUMN tasks.project_id IS '所属项目ID';
COMMENT ON COLUMN tasks.created_user_id IS '创建用户ID';
COMMENT ON COLUMN tasks.created_at IS '创建时间';
COMMENT ON COLUMN tasks.updated_at IS '更新时间';
COMMENT ON COLUMN tasks.started_at IS '开始执行时间';
COMMENT ON COLUMN tasks.completed_at IS '完成时间';