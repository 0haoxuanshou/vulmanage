package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户Token实体类
 * 用于存储用户登录token信息，实现token持久化
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_tokens")
public class UserToken {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联的用户ID
     */
    private Long userId;
    
    /**
     * 用户名，冗余字段便于查询
     */
    private String username;
    
    /**
     * JWT token字符串
     */
    @TableField("token_string")
    private String token;
    
    /**
     * token类型，默认Bearer
     */
    private String tokenType;
    
    /**
     * token过期时间
     */
    private LocalDateTime expiresAt;
    
    /**
     * token是否激活
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;
    
    /**
     * 登录IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理信息
     */
    private String userAgent;
}