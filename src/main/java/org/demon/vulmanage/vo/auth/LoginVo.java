package org.demon.vulmanage.vo.auth;

/**
 * 登录返回VO
 */
public class LoginVo {
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 令牌过期时间（毫秒时间戳）
     */
    private Long expiresAt;
    
    public LoginVo() {}
    
    public LoginVo(String token, String username, Long expiresAt) {
        this.token = token;
        this.username = username;
        this.expiresAt = expiresAt;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Long getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}