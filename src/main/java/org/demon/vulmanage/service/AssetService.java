package org.demon.vulmanage.service;

import org.demon.vulmanage.param.asset.AssetCreateParam;
import org.demon.vulmanage.param.asset.AssetQueryParam;
import org.demon.vulmanage.param.asset.AssetUpdateParam;
import org.demon.vulmanage.vo.asset.AssetVo;
import org.demon.vulmanage.vo.common.PageVo;

import java.util.List;

/**
 * 资产服务接口
 * 定义资产管理的业务方法
 */
public interface AssetService {
    
    /**
     * 创建资产
     * @param param 资产创建参数
     * @return 创建的资产
     */
    AssetVo createAsset(AssetCreateParam param);
    
    /**
     * 更新资产
     * @param param 资产更新参数
     * @return 更新的资产
     */
    AssetVo updateAsset(AssetUpdateParam param);
    
    /**
     * 删除资产
     * @param id 资产ID
     * @return 是否删除成功
     */
    boolean deleteAsset(Long id);
    
    /**
     * 根据ID获取资产
     * @param id 资产ID
     * @return 资产信息
     */
    AssetVo getAssetById(Long id);
    
    /**
     * 分页查询资产
     * @param param 查询参数
     * @return 分页结果
     */
    PageVo<AssetVo> getAssetsPage(AssetQueryParam param);
    
    /**
     * 获取所有资产
     * @return 资产列表
     */
    List<AssetVo> getAllAssets();
    
    /**
     * 根据项目ID获取资产列表
     * @param projectId 项目ID
     * @return 资产列表
     */
    List<AssetVo> getAssetsByProjectId(Long projectId);
    
    /**
     * 根据创建用户ID获取资产列表
     * @param createdUserId 创建用户ID
     * @return 资产列表
     */
    List<AssetVo> getAssetsByCreatedUserId(Long createdUserId);
    
    /**
     * 更新资产状态
     * @param id 资产ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateAssetStatus(Long id, String status);
}