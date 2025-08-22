package org.demon.vulmanage.controller;

import org.demon.vulmanage.common.Result;
import org.demon.vulmanage.param.asset.AssetCreateParam;
import org.demon.vulmanage.param.asset.AssetQueryParam;
import org.demon.vulmanage.param.asset.AssetUpdateParam;
import org.demon.vulmanage.service.AssetService;
import org.demon.vulmanage.util.SecurityUtil;
import org.demon.vulmanage.vo.asset.AssetVo;
import org.demon.vulmanage.vo.common.PageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产管理控制器
 * 提供资产的CRUD和分页查询API
 */
@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    /**
     * 创建资产
     * @param param 资产创建参数
     * @return 创建的资产
     */
    @PostMapping
    public Result<AssetVo> createAsset(@RequestBody AssetCreateParam param) {
        try {
            // 使用SecurityUtil获取当前用户ID
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (currentUserId != null) {
                param.setCreatedUserId(currentUserId);
            }
            
            AssetVo createdAsset = assetService.createAsset(param);
            return Result.success(createdAsset);
        } catch (Exception e) {
            return Result.error("创建资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新资产
     * @param id 资产ID
     * @param param 资产更新参数
     * @return 更新的资产
     */
    @PutMapping("/{id}")
    public Result<AssetVo> updateAsset(@PathVariable Long id, @RequestBody AssetUpdateParam param) {
        try {
            param.setId(id);
            AssetVo updatedAsset = assetService.updateAsset(param);
            if (updatedAsset != null) {
                return Result.success(updatedAsset);
            } else {
                return Result.error("资产不存在");
            }
        } catch (Exception e) {
            return Result.error("更新资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除资产
     * @param id 资产ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        try {
            boolean deleted = assetService.deleteAsset(id);
            if (deleted) {
                return Result.success(null);
            } else {
                return Result.error("资产不存在或删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取资产
     * @param id 资产ID
     * @return 资产信息
     */
    @GetMapping("/{id}")
    public Result<AssetVo> getAssetById(@PathVariable Long id) {
        try {
            AssetVo asset = assetService.getAssetById(id);
            if (asset != null) {
                return Result.success(asset);
            } else {
                return Result.error("资产不存在");
            }
        } catch (Exception e) {
            return Result.error("获取资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询资产
     * @param param 查询参数
     * @return 分页结果
     */
    @GetMapping
    public Result<PageVo<AssetVo>> getAssetsPage(AssetQueryParam param) {
        try {
            PageVo<AssetVo> assetsPage = assetService.getAssetsPage(param);
            return Result.success(assetsPage);
        } catch (Exception e) {
            return Result.error("查询资产失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有资产
     * @return 资产列表
     */
    @GetMapping("/all")
    public Result<List<AssetVo>> getAllAssets() {
        try {
            List<AssetVo> assets = assetService.getAllAssets();
            return Result.success(assets);
        } catch (Exception e) {
            return Result.error("获取资产列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID获取资产列表
     * @param projectId 项目ID
     * @return 资产列表
     */
    @GetMapping("/project/{projectId}")
    public Result<List<AssetVo>> getAssetsByProjectId(@PathVariable Long projectId) {
        try {
            List<AssetVo> assets = assetService.getAssetsByProjectId(projectId);
            return Result.success(assets);
        } catch (Exception e) {
            return Result.error("获取项目资产列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据创建用户ID获取资产列表
     * @param createdUserId 创建用户ID
     * @return 资产列表
     */
    @GetMapping("/user/{createdUserId}")
    public Result<List<AssetVo>> getAssetsByCreatedUserId(@PathVariable Long createdUserId) {
        try {
            List<AssetVo> assets = assetService.getAssetsByCreatedUserId(createdUserId);
            return Result.success(assets);
        } catch (Exception e) {
            return Result.error("获取用户资产列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新资产状态
     * @param id 资产ID
     * @param status 新状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateAssetStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean updated = assetService.updateAssetStatus(id, status);
            if (updated) {
                return Result.success(null);
            } else {
                return Result.error("资产不存在或状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新资产状态失败: " + e.getMessage());
        }
    }
}