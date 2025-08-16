package org.demon.vulmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.demon.vulmanage.model.User;

/**
 * 用户数据访问层
 * 使用MyBatis-Plus内置方法，避免自定义SQL
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 所有方法都使用MyBatis-Plus内置方法
    // 通过Service层的LambdaQueryWrapper实现查询逻辑
}