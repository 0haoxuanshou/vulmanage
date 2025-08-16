package org.demon.vulmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.demon.vulmanage.mapper.SystemConfigMapper;
import org.demon.vulmanage.mapper.UserMapper;
import org.demon.vulmanage.model.Role;
import org.demon.vulmanage.model.SystemConfig;
import org.demon.vulmanage.model.User;
import org.demon.vulmanage.security.CustomUser;
import org.demon.vulmanage.service.UserService;
import org.demon.vulmanage.exception.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final SystemConfigMapper systemConfigMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getEnabled, true);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 获取用户角色（这里简化处理，实际项目中需要通过UserRoleMapper查询）
        List<Role> roles = List.of(); // TODO: 实现角色查询逻辑
        
        return new CustomUser(user, roles);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return Optional.ofNullable(userMapper.selectOne(wrapper));
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return Optional.ofNullable(userMapper.selectOne(wrapper));
    }
    
    @Override
    public User createUser(String username, String password, String email, String fullName) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullName(fullName)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        userMapper.insert(user);
        
        // 注意：用户角色关联需要在用户创建后通过其他方式处理
        // 这里暂时简化，实际项目中可能需要额外的UserRoleMapper
        
        return user;
    }
    
    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean hasAnyUser() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        return userMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public void updatePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在，ID: " + userId);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public void setUserEnabled(Long userId, boolean enabled) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在，ID: " + userId);
        }
        user.setEnabled(enabled);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public boolean isSystemInitialized() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, "system.initialized");
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        return config != null && "true".equals(config.getConfigValue());
    }
    
    @Override
    public void markSystemAsInitialized() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, "system.initialized");
        SystemConfig config = systemConfigMapper.selectOne(wrapper);
        
        if (config != null) {
            config.setConfigValue("true");
            config.setUpdatedAt(LocalDateTime.now());
            systemConfigMapper.updateById(config);
        } else {
            config = SystemConfig.builder()
                    .configKey("system.initialized")
                    .configValue("true")
                    .description("系统是否已完成初始化设置")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            systemConfigMapper.insert(config);
        }
    }
}