package org.demon.vulmanage.vo.task;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务信息VO
 */
@Data
public class TaskVo {
    
    /**
     * 任务ID
     */
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
     * 任务类型描述
     */
    private String typeDescription;
    
    /**
     * 任务状态：PENDING（待执行）, RUNNING（执行中）, COMPLETED（已完成）, FAILED（失败）
     */
    private String status;
    
    /**
     * 任务状态描述
     */
    private String statusDescription;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 任务创建用户ID
     */
    private Long createdUserId;
    
    /**
     * 创建用户名称
     */
    private String createdUserName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 开始执行时间
     */
    private LocalDateTime startedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}