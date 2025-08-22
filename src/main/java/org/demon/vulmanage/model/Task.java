package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tasks")
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务类型：SCAN_TARGET（扫描任务目标）, OTHER（其他）
     */
    private String type;
    
    /**
     * 任务状态：PENDING（待执行）, RUNNING（执行中）, COMPLETED（已完成）, FAILED（失败）
     */
    private String status;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 任务创建用户ID
     */
    private Long createdUserId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 开始执行时间
     */
    private LocalDateTime startedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 项目名称，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private String projectName;
    
    /**
     * 创建用户名称，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private String createdUserName;
    
    /**
     * 任务类型枚举
     */
    public enum TaskType {
        SCAN_TARGET("扫描任务目标"),
        OTHER("其他");
        
        private final String description;
        
        TaskType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING("待执行"),
        RUNNING("执行中"),
        COMPLETED("已完成"),
        FAILED("失败");
        
        private final String description;
        
        TaskStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}