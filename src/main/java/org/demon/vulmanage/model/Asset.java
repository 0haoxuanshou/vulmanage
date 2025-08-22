package org.demon.vulmanage.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资产实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("assets")
public class Asset {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 资产名称
     */
    private String name;
    
    /**
     * 资产类型：IP（IP地址）, DOMAIN（域名）
     */
    private String type;
    
    /**
     * 资产值（IP地址或域名）
     */
    private String value;
    
    /**
     * 资产描述
     */
    private String description;
    
    /**
     * 资产状态：ACTIVE（活跃）, INACTIVE（非活跃）
     */
    private String status;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 资产创建用户ID
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
     * 项目名称，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private String projectName;
    
    /**
     * 创建用户名称，不存储在数据库中
     * 通过关联查询获取
     */
    @TableField(exist = false)
    private String createdUserName;
    
    /**
     * 资产类型枚举
     */
    public enum AssetType {
        IP("IP地址"),
        DOMAIN("域名");
        
        private final String description;
        
        AssetType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 资产状态枚举
     */
    public enum AssetStatus {
        ACTIVE("活跃"),
        INACTIVE("非活跃");
        
        private final String description;
        
        AssetStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}