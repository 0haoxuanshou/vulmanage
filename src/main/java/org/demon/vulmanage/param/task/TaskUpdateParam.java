package org.demon.vulmanage.param.task;

import lombok.Data;

/**
 * 任务更新参数
 */
@Data
public class TaskUpdateParam {
    
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
     * 任务状态：PENDING（待执行）, RUNNING（执行中）, COMPLETED（已完成）, FAILED（失败）
     */
    private String status;
    
    /**
     * 任务描述
     */
    private String description;
}