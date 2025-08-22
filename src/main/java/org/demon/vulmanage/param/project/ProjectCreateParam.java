package org.demon.vulmanage.param.project;

import lombok.Data;

/**
 * 项目创建参数
 */
@Data
public class ProjectCreateParam {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目创建用户ID
     */
    private Long createdUserId;
}