package org.demon.vulmanage.util;

import org.demon.vulmanage.exception.BusinessException;
import org.demon.vulmanage.security.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Spring Security 工具类
 * 用于获取当前登录用户信息
 */
@Component
public class SecurityUtil {

    /**
     * 获取当前登录用户的Authentication对象
     * @return Authentication对象，如果未登录则返回null
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户的CustomUser对象
     * @return CustomUser对象
     * @throws BusinessException 如果用户未登录或认证信息无效，抛出401错误
     */
    public static CustomUser getCurrentUser() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUser) {
            return (CustomUser) authentication.getPrincipal();
        }
        throw new BusinessException(401, "用户未登录或认证信息无效");
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     * @throws BusinessException 如果用户未登录、认证信息无效或用户ID为空，抛出401错误
     */
    public static Long getCurrentUserId() {
        CustomUser currentUser = getCurrentUser();
        if (currentUser != null && currentUser.getUser() != null && currentUser.getUser().getId() != null) {
            return currentUser.getUser().getId();
        }
        throw new BusinessException(401, "无法获取用户ID，用户信息不完整");
    }

    /**
     * 获取当前登录用户的用户名
     * @return 用户名
     * @throws BusinessException 如果用户未登录或认证信息无效，抛出401错误
     */
    public static String getCurrentUsername() {
        CustomUser currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUsername();
        }
        throw new BusinessException(401, "无法获取用户名，用户信息不完整");
    }

    /**
     * 检查是否已登录
     * @return true表示已登录，false表示未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               authentication.getPrincipal() instanceof CustomUser;
    }
}