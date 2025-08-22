package org.demon.vulmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.demon.vulmanage.model.Project;

/**
 * 项目数据访问层
 * 使用MyBatis-Plus内置方法，避免自定义SQL
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    // 所有方法都使用MyBatis-Plus内置方法
    // 通过Service层的LambdaQueryWrapper实现查询逻辑
}