package org.demon.vulmanage.service;

import lombok.RequiredArgsConstructor;
import org.demon.vulmanage.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    
    // 存储用户当前有效的token，实现单点登录
    // key: username, value: token
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();
    
    // 存储token对应的用户名，用于快速查找
    // key: token, value: username
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    /**
     * 用户登录，生成新token并使旧token失效
     */
public String login(String username) {
        // 如果用户已有活跃token，先将其失效
        invalidateUserToken(username);
        
        // 生成新token
        String newToken = jwtUtil.generateToken(username);
        
        // 存储新token
        activeTokens.put(username, newToken);
        tokenToUser.put(newToken, username);
        
        return newToken;
    }

    /**
     * 验证token是否有效
     */
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // 检查token是否在活跃列表中
        String username = tokenToUser.get(token);
        if (username == null) {
            return false;
        }
        
        // 检查是否是用户当前的活跃token
        String activeToken = activeTokens.get(username);
        if (!token.equals(activeToken)) {
            // token已被新登录替换，清理过期映射
            tokenToUser.remove(token);
            return false;
        }
        
        // 验证JWT token本身是否有效
        if (!jwtUtil.isTokenValid(token)) {
            // token已过期，清理
            invalidateUserToken(username);
            return false;
        }
        
        return true;
    }

    /**
     * 从token获取用户名
     */
    public String getUsernameFromToken(String token) {
        if (!isTokenValid(token)) {
            return null;
        }
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * 用户登出
     */
    public void logout(String token) {
        String username = tokenToUser.get(token);
        if (username != null) {
            invalidateUserToken(username);
        }
    }

    /**
     * 使指定用户的token失效
     */
    private void invalidateUserToken(String username) {
        String oldToken = activeTokens.remove(username);
        if (oldToken != null) {
            tokenToUser.remove(oldToken);
        }
    }

    /**
     * 获取token剩余有效时间
     */
    public Long getTokenRemainingTime(String token) {
        if (!isTokenValid(token)) {
            return 0L;
        }
        return jwtUtil.getTokenRemainingTime(token);
    }

    /**
     * 刷新token（可选功能）
     */
    public String refreshToken(String oldToken) {
        String username = getUsernameFromToken(oldToken);
        if (username != null) {
            return login(username); // 重新登录生成新token
        }
        return null;
    }

    /**
     * 获取当前活跃用户数量
     */
    public int getActiveUserCount() {
        return activeTokens.size();
    }

    /**
     * 清理所有过期token（定时任务可调用）
     */
    public void cleanupExpiredTokens() {
        activeTokens.entrySet().removeIf(entry -> {
            String token = entry.getValue();
            boolean isExpired = !jwtUtil.isTokenValid(token);
            if (isExpired) {
                tokenToUser.remove(token);
            }
            return isExpired;
        });
    }
}