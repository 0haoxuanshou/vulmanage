package org.demon.vulmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.mapper.UserTokenMapper;
import org.demon.vulmanage.model.UserToken;
import org.demon.vulmanage.util.JwtUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final UserTokenMapper userTokenMapper;
    private final UserService userService;

    /**
     * 用户登录，生成新token并使旧token失效
     */
    @Transactional
    public String login(String username, String ipAddress, String userAgent) {
        try {
            // 如果用户已有活跃token，先将其失效
            invalidateUserToken(username);
            
            // 生成新token
            String newToken = jwtUtil.generateToken(username);
            
            // 获取用户信息
            var user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
            
            // 计算过期时间
            Date expirationDate = jwtUtil.getExpirationDateFromToken(newToken);
            LocalDateTime expiresAt = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            
            // 创建token记录
            UserToken userToken = UserToken.builder()
                    .userId(user.getId())
                    .username(username)
                    .token(newToken)
                    .tokenType("Bearer")
                    .expiresAt(expiresAt)
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
            
            // 保存到数据库
            userTokenMapper.insert(userToken);
            
            log.info("用户 {} 登录成功，生成新token", username);
            return newToken;
        } catch (Exception e) {
            log.error("用户 {} 登录失败: {}", username, e.getMessage());
            throw new RuntimeException("登录失败", e);
        }
    }

    /**
     * 验证token是否有效
     */
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 从数据库查找token
            LambdaQueryWrapper<UserToken> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserToken::getToken, token)
                       .eq(UserToken::getIsActive, true);
            UserToken userToken = userTokenMapper.selectOne(queryWrapper);
            
            if (userToken == null) {
                return false;
            }
            
            // 检查token是否过期
            if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                // token已过期，将其设为非激活状态
                deactivateTokenByString(token);
                return false;
            }
            
            // 验证JWT token本身是否有效
            if (!jwtUtil.isTokenValid(token)) {
                // JWT token无效，将其设为非激活状态
                deactivateTokenByString(token);
                return false;
            }
            
            // 更新最后使用时间
            updateLastUsedTime(token, LocalDateTime.now());
            
            return true;
        } catch (Exception e) {
            log.error("验证token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从token获取用户名
     */
    public String getUsernameFromToken(String token) {
        if (!isTokenValid(token)) {
            return null;
        }
        
        try {
            LambdaQueryWrapper<UserToken> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserToken::getToken, token)
                       .eq(UserToken::getIsActive, true);
            UserToken userToken = userTokenMapper.selectOne(queryWrapper);
            
            if (userToken != null) {
                return userToken.getUsername();
            }
            return jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("从token获取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 用户登出
     */
    @Transactional
    public void logout(String token) {
        try {
            if (token != null && !token.trim().isEmpty()) {
                deactivateTokenByString(token);
                log.info("用户token已登出");
            }
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
        }
    }

    /**
     * 使指定用户的token失效
     */
    @Transactional
    private void invalidateUserToken(String username) {
        try {
            LambdaUpdateWrapper<UserToken> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserToken::getUsername, username)
                        .set(UserToken::getIsActive, false)
                        .set(UserToken::getUpdatedAt, LocalDateTime.now());
            userTokenMapper.update(null, updateWrapper);
            log.debug("用户 {} 的所有token已失效", username);
        } catch (Exception e) {
            log.error("使用户token失效失败: {}", e.getMessage());
        }
    }
    
    /**
     * 根据token字符串使token失效
     */
    @Transactional
    private void deactivateTokenByString(String token) {
        try {
            LambdaUpdateWrapper<UserToken> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserToken::getToken, token)
                        .set(UserToken::getIsActive, false)
                        .set(UserToken::getUpdatedAt, LocalDateTime.now());
            userTokenMapper.update(null, updateWrapper);
        } catch (Exception e) {
            log.error("使token失效失败: {}", e.getMessage());
        }
    }
    
    /**
     * 更新token的最后使用时间
     */
    private void updateLastUsedTime(String token, LocalDateTime lastUsedAt) {
        try {
            LambdaUpdateWrapper<UserToken> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserToken::getToken, token)
                        .set(UserToken::getLastUsedAt, lastUsedAt)
                        .set(UserToken::getUpdatedAt, LocalDateTime.now());
            userTokenMapper.update(null, updateWrapper);
        } catch (Exception e) {
            log.error("更新token最后使用时间失败: {}", e.getMessage());
        }
    }

    /**
     * 获取token剩余有效时间
     */
    public Long getTokenRemainingTime(String token) {
        if (!isTokenValid(token)) {
            return 0L;
        }
        
        try {
            LambdaQueryWrapper<UserToken> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserToken::getToken, token)
                       .eq(UserToken::getIsActive, true);
            UserToken userToken = userTokenMapper.selectOne(queryWrapper);
            
            if (userToken != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiresAt = userToken.getExpiresAt();
                
                if (expiresAt.isAfter(now)) {
                    return java.time.Duration.between(now, expiresAt).getSeconds();
                }
            }
            return jwtUtil.getTokenRemainingTime(token);
        } catch (Exception e) {
            log.error("获取token剩余时间失败: {}", e.getMessage());
            return 0L;
        }
    }

    /**
     * 刷新token（可选功能）
     */
    @Transactional
    public String refreshToken(String oldToken, String ipAddress, String userAgent) {
        String username = getUsernameFromToken(oldToken);
        if (username != null) {
            return login(username, ipAddress, userAgent); // 重新登录生成新token
        }
        return null;
    }

    /**
     * 获取当前活跃用户数量
     */
    public int getActiveUserCount() {
        try {
            LambdaQueryWrapper<UserToken> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserToken::getIsActive, true)
                       .gt(UserToken::getExpiresAt, LocalDateTime.now())
                       .select(UserToken::getUsername);
            return Math.toIntExact(userTokenMapper.selectCount(queryWrapper));
        } catch (Exception e) {
            log.error("获取活跃用户数量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 清理所有过期token（定时任务）
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            // 先将过期的token设为非激活状态
            LambdaQueryWrapper<UserToken> expiredQuery = new LambdaQueryWrapper<>();
            expiredQuery.lt(UserToken::getExpiresAt, LocalDateTime.now())
                       .eq(UserToken::getIsActive, true);
            List<UserToken> expiredTokens = userTokenMapper.selectList(expiredQuery);
            
            for (UserToken token : expiredTokens) {
                deactivateTokenByString(token.getToken());
            }
            
            // 删除过期且非激活的token记录（保留一段时间用于审计）
            LambdaQueryWrapper<UserToken> deleteQuery = new LambdaQueryWrapper<>();
            deleteQuery.lt(UserToken::getExpiresAt, LocalDateTime.now())
                      .eq(UserToken::getIsActive, false);
            int deletedCount = userTokenMapper.delete(deleteQuery);
            
            if (deletedCount > 0) {
                log.info("清理了 {} 个过期token记录", deletedCount);
            }
        } catch (Exception e) {
            log.error("清理过期token失败: {}", e.getMessage());
        }
    }
    
    /**
     * 重载login方法以保持向后兼容
     */
    public String login(String username) {
        return login(username, null, null);
    }
}