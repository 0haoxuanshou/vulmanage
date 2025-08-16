package org.demon.vulmanage.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.demon.vulmanage.service.TokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 检查header格式是否正确
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 移除"Bearer "前缀
            username = tokenService.getUsernameFromToken(token);
        }

        // 如果token有效且当前没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 验证token
            if (tokenService.isTokenValid(token)) {
                // 创建认证对象
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                // 设置详细信息
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置到安全上下文
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // 跳过登录和登出端点的过滤
        return path.equals("/api/auth/login") || 
               path.equals("/api/auth/logout") ||
               path.startsWith("/api/auth/status");
    }
}