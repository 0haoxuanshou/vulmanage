package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 专注于数据持久化，不包含认证授权逻辑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private String fullName;
    
    private Boolean enabled;
    
    private Boolean accountNonExpired;
    
    private Boolean accountNonLocked;
    
    private Boolean credentialsNonExpired;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * 用户角色列表，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private List<Role> roles;
    

}