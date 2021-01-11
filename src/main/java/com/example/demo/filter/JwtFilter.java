package com.example.demo.filter;

import com.example.demo.service.TokenServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final TokenServiceImpl tokenService;

    public JwtFilter(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * 验证authorization
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.err.println("==================================================");
        String jwtToken = httpServletRequest.getHeader("authorization");
        System.out.println(jwtToken);
        //解析authorization
        Claims claims = tokenService.parseToken(jwtToken);
        if(claims!=null){
            //获取当前登录用户名//获取用户角色
            String username = claims.getSubject();
            Collection<GrantedAuthority> authorities=tokenService.getAuthority(claims);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
