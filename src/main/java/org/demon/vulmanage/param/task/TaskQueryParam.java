package org.demon.vulmanage.param.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.demon.vulmanage.param.common.PageParam;

/**
 * 任务查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskQueryParam extends PageParam {
    
    /**
     * 任务名称（模糊查询）
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
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 任务创建用户ID
     */
    private Long createdUserId;
}