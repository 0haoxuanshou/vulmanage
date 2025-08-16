package org.demon.vulmanage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统初始化设置控制器
 */
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@Slf4j
public class SetupController {
    
    private final UserService userService;
    
    /**
     * 检查系统是否已初始化
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSetupStatus() {
        boolean isInitialized = userService.isSystemInitialized();
        boolean hasUsers = userService.hasAnyUser();
        
        return ResponseEntity.ok(Map.of(
                "initialized", isInitialized,
                "hasUsers", hasUsers,
                "needsSetup", !isInitialized && !hasUsers
        ));
    }
    
    /**
     * 创建初始管理员账号
     */
    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> createInitialAdmin(
            @RequestBody CreateAdminRequest request) {
        
        // 检查系统是否已初始化
        if (userService.isSystemInitialized()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "系统已经初始化，无法重复设置管理员账号"
            ));
        }
        
        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名已存在"
            ));
        }
        
        // 检查邮箱是否已存在
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "邮箱已存在"
            ));
        }
        
        // 创建管理员用户
        userService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName()
        );
        
        // 标记系统为已初始化
        userService.markSystemAsInitialized();
        
        log.info("初始管理员账号创建成功: {}", request.getUsername());
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "管理员账号创建成功"
        ));
    }
    
    /**
     * 创建管理员请求DTO
     */
    public static class CreateAdminRequest {
        private String username;
        private String password;
        private String email;
        private String fullName;
        
        // Getters and Setters
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}