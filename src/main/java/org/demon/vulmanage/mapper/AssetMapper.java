package org.demon.vulmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.demon.vulmanage.model.Asset;

/**
 * 资产数据访问层
 * 使用MyBatis-Plus内置方法，避免自定义SQL
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
    // 所有方法都使用MyBatis-Plus内置方法
    // 通过Service层的LambdaQueryWrapper实现查询逻辑
}