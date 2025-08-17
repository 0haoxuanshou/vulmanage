package org.demon.vulmanage.controller;

import lombok.RequiredArgsConstructor;
import org.demon.vulmanage.common.Result;
import org.demon.vulmanage.exception.BusinessException;
import org.demon.vulmanage.model.User;
import org.demon.vulmanage.param.auth.LoginParam;
import org.demon.vulmanage.service.TokenService;
import org.demon.vulmanage.service.UserService;
import org.demon.vulmanage.vo.auth.LoginVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/status")
    public Result<Map<String, Object>> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            return Result.success(Map.of(
                    "authenticated", true,
                    "username", authentication.getName()
            ));
        }
        return Result.success(Map.of("authenticated", false));
    }

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginParam loginParam, HttpServletRequest request) {
        // 从数据库查询用户
        User user = userService.findByUsername(loginParam.getUsername())
                .orElse(null);

        // 验证用户存在且密码正确
        if (user == null || !user.getEnabled() ||
                !passwordEncoder.matches(loginParam.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 获取客户端信息
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // 生成token
        String token = tokenService.login(loginParam.getUsername(), ipAddress, userAgent);

        // 计算过期时间（从token服务获取剩余时间）
        Long remainingTime = tokenService.getTokenRemainingTime(token);
        long expiresAt = System.currentTimeMillis() + (remainingTime * 1000);

        LoginVo loginVo = new LoginVo(token, loginParam.getUsername(), expiresAt);
        return Result.success("登录成功", loginVo);
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.logout(token);
        }
        return Result.success();
    }


}