package org.demon.vulmanage.vo.project;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目信息VO
 */
@Data
public class ProjectVo {
    
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目创建用户ID
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
}