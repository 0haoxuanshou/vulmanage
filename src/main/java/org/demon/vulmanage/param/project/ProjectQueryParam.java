package org.demon.vulmanage.param.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.demon.vulmanage.param.common.PageParam;

/**
 * 项目查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectQueryParam extends PageParam {
    
    /**
     * 项目名称（模糊查询）
     */
    private String name;
    
    /**
     * 创建用户ID
     */
    private Long createdUserId;
}