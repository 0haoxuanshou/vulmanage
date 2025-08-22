package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("projects")
public class Project {
    
    @TableId(type = IdType.AUTO)
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
     * 创建用户信息，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private String createdUserName;
}