package org.demon.vulmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demon.vulmanage.mapper.AssetMapper;
import org.demon.vulmanage.mapper.ProjectMapper;
import org.demon.vulmanage.mapper.UserMapper;
import org.demon.vulmanage.model.Asset;
import org.demon.vulmanage.model.Project;
import org.demon.vulmanage.model.User;
import org.demon.vulmanage.param.asset.AssetCreateParam;
import org.demon.vulmanage.param.asset.AssetQueryParam;
import org.demon.vulmanage.param.asset.AssetUpdateParam;
import org.demon.vulmanage.service.AssetService;
import org.demon.vulmanage.vo.asset.AssetVo;
import org.demon.vulmanage.vo.common.PageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产服务实现类
 * 实现资产管理的具体业务逻辑
 */
@Service
public class AssetServiceImpl implements AssetService {
    
    @Autowired
    private AssetMapper assetMapper;
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public AssetVo createAsset(AssetCreateParam param) {
        Asset asset = new Asset();
        BeanUtils.copyProperties(param, asset);
        asset.setStatus(Asset.AssetStatus.ACTIVE.name());
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.insert(asset);
        return getAssetById(asset.getId());
    }
    
    @Override
    public AssetVo updateAsset(AssetUpdateParam param) {
        Asset asset = new Asset();
        BeanUtils.copyProperties(param, asset);
        asset.setUpdatedAt(LocalDateTime.now());
        assetMapper.updateById(asset);
        return getAssetById(param.getId());
    }
    
    @Override
    public boolean deleteAsset(Long id) {
        return assetMapper.deleteById(id) > 0;
    }
    
    @Override
    public AssetVo getAssetById(Long id) {
        Asset asset = assetMapper.selectById(id);
        if (asset != null) {
            return convertToVo(asset);
        }
        return null;
    }
    
    /**
     * 将Asset实体转换为AssetVo
     */
    private AssetVo convertToVo(Asset asset) {
        AssetVo vo = new AssetVo();
        BeanUtils.copyProperties(asset, vo);
        
        // 设置类型描述
        try {
            Asset.AssetType assetType = Asset.AssetType.valueOf(asset.getType());
            vo.setTypeDescription(assetType.getDescription());
        } catch (Exception e) {
            vo.setTypeDescription(asset.getType());
        }
        
        // 设置状态描述
        try {
            Asset.AssetStatus assetStatus = Asset.AssetStatus.valueOf(asset.getStatus());
            vo.setStatusDescription(assetStatus.getDescription());
        } catch (Exception e) {
            vo.setStatusDescription(asset.getStatus());
        }
        
        // 设置项目名称
        if (asset.getProjectId() != null) {
            Project project = projectMapper.selectById(asset.getProjectId());
            if (project != null) {
                vo.setProjectName(project.getName());
            }
        }
        
        // 设置创建用户名称
        if (asset.getCreatedUserId() != null) {
            User user = userMapper.selectById(asset.getCreatedUserId());
            if (user != null) {
                vo.setCreatedUserName(user.getUsername());
            }
        }
        
        return vo;
    }
    
    @Override
    public PageVo<AssetVo> getAssetsPage(AssetQueryParam param) {
        Page<Asset> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(param.getName())) {
            wrapper.like(Asset::getName, param.getName());
        }
        if (StringUtils.hasText(param.getType())) {
            wrapper.eq(Asset::getType, param.getType());
        }
        if (StringUtils.hasText(param.getValue())) {
            wrapper.like(Asset::getValue, param.getValue());
        }
        if (StringUtils.hasText(param.getStatus())) {
            wrapper.eq(Asset::getStatus, param.getStatus());
        }
        if (param.getProjectId() != null) {
            wrapper.eq(Asset::getProjectId, param.getProjectId());
        }
        if (param.getCreatedUserId() != null) {
            wrapper.eq(Asset::getCreatedUserId, param.getCreatedUserId());
        }
        
        wrapper.orderByDesc(Asset::getCreatedAt);
        
        IPage<Asset> assetPage = assetMapper.selectPage(page, wrapper);
        List<AssetVo> assetVos = assetPage.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        return new PageVo<>(assetVos, assetPage.getTotal(), (int) assetPage.getCurrent(), (int) assetPage.getSize());
    }
    
    @Override
    public List<AssetVo> getAllAssets() {
        List<Asset> assets = assetMapper.selectList(new LambdaQueryWrapper<Asset>()
                .orderByDesc(Asset::getCreatedAt));
        return assets.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AssetVo> getAssetsByProjectId(Long projectId) {
        List<Asset> assets = assetMapper.selectList(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getProjectId, projectId)
                .orderByDesc(Asset::getCreatedAt));
        return assets.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AssetVo> getAssetsByCreatedUserId(Long createdUserId) {
        List<Asset> assets = assetMapper.selectList(new LambdaQueryWrapper<Asset>()
                .eq(Asset::getCreatedUserId, createdUserId)
                .orderByDesc(Asset::getCreatedAt));
        return assets.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean updateAssetStatus(Long id, String status) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setStatus(status);
        asset.setUpdatedAt(LocalDateTime.now());
        return assetMapper.updateById(asset) > 0;
    }
}