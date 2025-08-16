package org.demon.vulmanage.security;

import org.demon.vulmanage.model.Role;
import org.demon.vulmanage.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户类，实现Spring Security的UserDetails接口
 * 用于认证和授权，与持久化实体User分离
 */
public class CustomUser implements UserDetails {
    
    private final User user;
    private final List<Role> roles;
    
    public CustomUser(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired() != null ? user.getAccountNonExpired() : true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked() != null ? user.getAccountNonLocked() : true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : true;
    }
    
    @Override
    public boolean isEnabled() {
        return user.getEnabled() != null ? user.getEnabled() : true;
    }
    
    /**
     * 获取原始用户实体
     */
    public User getUser() {
        return user;
    }
    
    /**
     * 获取用户角色列表
     */
    public List<Role> getRoles() {
        return roles;
    }
}