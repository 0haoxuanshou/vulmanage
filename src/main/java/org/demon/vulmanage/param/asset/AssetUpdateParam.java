package org.demon.vulmanage.param.asset;

import lombok.Data;

/**
 * 资产更新参数
 */
@Data
public class AssetUpdateParam {
    
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
}