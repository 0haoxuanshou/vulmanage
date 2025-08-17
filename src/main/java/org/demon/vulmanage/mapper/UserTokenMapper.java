package org.demon.vulmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.demon.vulmanage.model.UserToken;

/**
 * 用户Token数据访问层
 * 使用MyBatis-Plus提供的基础CRUD方法
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
    // 使用MyBatis-Plus的内置方法，无需自定义SQL
}