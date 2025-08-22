package org.demon.vulmanage.vo.asset;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产信息VO
 */
@Data
public class AssetVo {
    
    /**
     * 资产ID
     */
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
     * 资产类型描述
     */
    private String typeDescription;
    
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
     * 资产状态描述
     */
    private String statusDescription;
    
    /**
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 资产创建用户ID
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