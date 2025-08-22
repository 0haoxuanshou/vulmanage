package org.demon.vulmanage.param.asset;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.demon.vulmanage.param.common.PageParam;

/**
 * 资产查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetQueryParam extends PageParam {
    
    /**
     * 资产名称（模糊查询）
     */
    private String name;
    
    /**
     * 资产类型：IP（IP地址）, DOMAIN（域名）
     */
    private String type;
    
    /**
     * 资产值（模糊查询）
     */
    private String value;
    
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
}