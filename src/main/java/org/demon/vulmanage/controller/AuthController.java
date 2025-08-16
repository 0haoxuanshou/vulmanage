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
    public Result<LoginVo> login(@RequestBody LoginParam loginParam) {
        // 从数据库查询用户
        User user = userService.findByUsername(loginParam.getUsername())
                .orElse(null);

        // 验证用户存在且密码正确
        if (user == null || !user.getEnabled() ||
                !passwordEncoder.matches(loginParam.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成token
        String token = tokenService.login(loginParam.getUsername());

        // 计算过期时间（假设token有效期为24小时）
        long expiresAt = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        LoginVo loginVo = new LoginVo(token, loginParam.getUsername(), expiresAt);
        return Result.success("登录成功", loginVo);
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