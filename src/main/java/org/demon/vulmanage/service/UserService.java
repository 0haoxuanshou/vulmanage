package org.demon.vulmanage.service;

import org.demon.vulmanage.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 创建新用户
     */
    User createUser(String username, String password, String email, String fullName);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查是否存在任何用户
     */
    boolean hasAnyUser();
    
    /**
     * 更新用户密码
     */
    void updatePassword(Long userId, String newPassword);
    
    /**
     * 启用或禁用用户
     */
    void setUserEnabled(Long userId, boolean enabled);
    
    /**
     * 检查系统是否已初始化
     */
    boolean isSystemInitialized();
    
    /**
     * 标记系统为已初始化
     */
    void markSystemAsInitialized();
}