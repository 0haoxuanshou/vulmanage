package org.demon.vulmanage.param.project;

import lombok.Data;

/**
 * 项目更新参数
 */
@Data
public class ProjectUpdateParam {
    
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    private String name;
}