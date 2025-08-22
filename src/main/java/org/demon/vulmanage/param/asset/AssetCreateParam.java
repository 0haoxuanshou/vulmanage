package org.demon.vulmanage.param.asset;

import lombok.Data;

/**
 * 资产创建参数
 */
@Data
public class AssetCreateParam {
    
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
     * 所属项目ID
     */
    private Long projectId;
    
    /**
     * 资产创建用户ID
     */
    private Long createdUserId;
}