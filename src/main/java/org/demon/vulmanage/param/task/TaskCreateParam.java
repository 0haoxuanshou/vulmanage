package org.demon.vulmanage.param.task;

import lombok.Data;

/**
 * 任务创建参数
 */
@Data
public class TaskCreateParam {
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务类型：SCAN_TARGET（扫描任务目标）, OTHER（其他）
     */
    private String type;
    
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
}